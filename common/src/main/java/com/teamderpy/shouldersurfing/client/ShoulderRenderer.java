package com.teamderpy.shouldersurfing.client;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.math.Vec2f;
import com.teamderpy.shouldersurfing.mixins.CameraAccessor;
import com.teamderpy.shouldersurfing.mixins.GuiAccessor;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShoulderRenderer
{
	private static final ShoulderRenderer INSTANCE = new ShoulderRenderer();
	private double cameraDistance;
	private Vec2f lastTranslation = Vec2f.ZERO;
	private Vec2f translation = Vec2f.ZERO;
	private Vec2f projected;
	private Vec2f projected2;
	
	public void offsetCrosshair(PoseStack poseStack, Window window, float partialTicks)
	{
		if(this.projected2 != null && Minecraft.getInstance().player.getUseItemRemainingTicks() > 0)
		{
			Vec2f scaledDimensions = new Vec2f(window.getGuiScaledWidth(), window.getGuiScaledHeight());
			Vec2f dimensions = new Vec2f(window.getScreenWidth(), window.getScreenHeight());
			Vec2f scale = scaledDimensions.divide(dimensions);
			Vec2f center = dimensions.divide(2); // In actual monitor pixels
			Vec2f projectedOffset = this.projected2.subtract(center).scale(scale);
			this.projected = new Vec2f(this.projected2.getX(), center.getY());
			GuiAccessor accessor = (GuiAccessor) Minecraft.getInstance().gui;
			poseStack.pushPose();
			poseStack.last().pose().translate(new Vector3f(projectedOffset.getX(), -projectedOffset.getY(), 0F));
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, Gui.GUI_ICONS_LOCATION);
			accessor.invokeRenderCrosshair(poseStack);
			poseStack.popPose();
		}
		
		if(this.projected != null)
		{
			Vec2f scaledDimensions = new Vec2f(window.getGuiScaledWidth(), window.getGuiScaledHeight());
			Vec2f dimensions = new Vec2f(window.getScreenWidth(), window.getScreenHeight());
			Vec2f scale = scaledDimensions.divide(dimensions);
			Vec2f center = dimensions.divide(2); // In actual monitor pixels
			Vec2f projectedOffset = this.projected.subtract(center).scale(scale);
			Vec2f interpolated = projectedOffset.subtract(this.lastTranslation).scale(partialTicks);
			this.translation = this.lastTranslation.add(interpolated);
		}
		
		if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderInstance.getInstance().doShoulderSurfing())
		{
			poseStack.pushPose();
			poseStack.last().pose().translate(new Vector3f(this.translation.getX(), -this.translation.getY(), 0F));
			this.lastTranslation = this.translation;
		}
		else
		{
			this.lastTranslation = Vec2f.ZERO;
		}
	}
	
	public void clearCrosshairOffset(PoseStack poseStack)
	{
		if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderInstance.getInstance().doShoulderSurfing() && !Vec2f.ZERO.equals(this.lastTranslation))
		{
			poseStack.popPose();
		}
	}
	
	public void offsetCamera(Camera camera, Level level, double partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && level != null)
		{
			CameraAccessor accessor = ((CameraAccessor) camera);
			double x = Mth.lerp(partialTick, camera.getEntity().xo, camera.getEntity().getX());
			double y = Mth.lerp(partialTick, camera.getEntity().yo, camera.getEntity().getY()) + Mth.lerp(partialTick, accessor.getEyeHeightOld(), accessor.getEyeHeight());
			double z = Mth.lerp(partialTick, camera.getEntity().zo, camera.getEntity().getZ());
			accessor.invokeSetPosition(x, y, z);
			Vec3 offset = new Vec3(-Config.CLIENT.getOffsetZ(), Config.CLIENT.getOffsetY(), Config.CLIENT.getOffsetX());
			this.cameraDistance = this.calcCameraDistance(camera, level, accessor.invokeGetMaxZoom(offset.length()));
			Vec3 scaled = offset.normalize().scale(this.cameraDistance);
			accessor.invokeMove(scaled.x, scaled.y, scaled.z);
		}
	}
	
	private double calcCameraDistance(Camera camera, Level level, double distance)
	{
		Vec3 cameraPos = camera.getPosition();
		Vec3 cameraOffset = ShoulderHelper.calcCameraOffset(camera, distance);
		
		for(int i = 0; i < 8; i++)
		{
			Vec3 offset = new Vec3(i & 1, i >> 1 & 1, i >> 2 & 1).scale(2).subtract(1, 1, 1).scale(0.1);
			Vec3 from = cameraPos.add(offset);
			Vec3 to = from.add(cameraOffset);
			ClipContext context = new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, camera.getEntity());
			HitResult hitResult = level.clip(context);
			
			if(hitResult != null)
			{
				double newDistance = hitResult.getLocation().distanceTo(cameraPos);
				
				if(newDistance < distance)
				{
					distance = newDistance;
				}
			}
		}
		
		return distance;
	}
	
	@SuppressWarnings("resource")
	public void updateDynamicRaytrace(Camera camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing())
		{
			MultiPlayerGameMode controller = Minecraft.getInstance().gameMode;
			HitResult hitResult = this.rayTraceFromEyes(camera, controller, this.getPlayerReach(), partialTick);
			Vec3 position = hitResult.getLocation().subtract(camera.getPosition());
			
			
			CameraAccessor accessor = ((CameraAccessor) camera);
			Player player = Minecraft.getInstance().player;
			ItemStack stack = player.getMainHandItem();
			
			if(stack.getItem().equals(Items.BOW) && player.getUseItemRemainingTicks() > 0)
			{
				float x = player.getXRot();
				float y = player.getYRot();
				float z = 0F;
				float velocity = BowItem.getPowerForTime(stack.getItem().getUseDuration(stack) - player.getUseItemRemainingTicks()) * 3F;
				float f = -Mth.sin(y * ((float)Math.PI / 180F)) * Mth.cos(x * ((float)Math.PI / 180F));
				float f1 = -Mth.sin((x + z) * ((float)Math.PI / 180F));
				float f2 = Mth.cos(y * ((float)Math.PI / 180F)) * Mth.cos(x * ((float)Math.PI / 180F));
				Level level = Minecraft.getInstance().level;
				Vec3 playerMotion = player.getDeltaMovement();
				
				double px = Mth.lerp(partialTick, player.xo, player.getX());
				double py = Mth.lerp(partialTick, player.yo, player.getY()) + Mth.lerp(partialTick, accessor.getEyeHeightOld(), accessor.getEyeHeight());
				double pz = Mth.lerp(partialTick, player.zo, player.getZ());
				
				Vec3 start = new Vec3(px, py, pz);
				Vec3 motion = new Vec3(f, f1, f2).normalize().scale(velocity).add(playerMotion.x, player.isOnGround() ? 0.0D : playerMotion.y, playerMotion.z);
				
				for(int i = 0; i < 300; i++)
				{
					Vec3 end = start.add(motion);
					HitResult hitresult = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
					
					if(hitresult.getType() != HitResult.Type.MISS)
					{
						end = hitresult.getLocation();
						this.projected2 = this.project2D(end.subtract(camera.getPosition()), modelViewMatrix, projectionMatrix);
						break;
					}
					
					motion = motion.scale(0.99F).subtract(0, 0.05, 0);
					start = end;
					
					BlockPos blockpos = new BlockPos(start);
					BlockState blockstate = level.getBlockState(blockpos);
					
					if(!blockstate.isAir())
					{
						VoxelShape voxelshape = blockstate.getCollisionShape(level, blockpos);
						
						if(!voxelshape.isEmpty())
						{
							for(AABB aabb : voxelshape.toAabbs())
							{
								if(aabb.move(blockpos).contains(start))
								{
									return;
								}
							}
						}
					}
				}
			}
			
			this.projected = this.project2D(position, modelViewMatrix, projectionMatrix);
		}
	}
	
	private HitResult rayTraceFromEyes(Camera camera, MultiPlayerGameMode gameMode, double playerReachOverride, final float partialTick)
	{
		double playerReach = Math.max(gameMode.getPickRange(), playerReachOverride);
		Entity cameraEntity = camera.getEntity();
		HitResult blockTrace = cameraEntity.pick(playerReach, partialTick, false);
		Vec3 eyePosition = cameraEntity.getEyePosition(partialTick);
		
		if(gameMode.hasFarPickRange())
		{
			playerReach = Math.max(playerReach, gameMode.getPlayerMode().isCreative() ? 6.0D : 3.0D);
		}
		
		double playerReachSqr = playerReach * playerReach;
		
		if(blockTrace != null)
		{
			playerReachSqr = blockTrace.getLocation().distanceToSqr(eyePosition);
		}
		
		Vec3 viewVector = cameraEntity.getViewVector(1.0F);
		Vec3 traceEnd = eyePosition.add(viewVector.scale(playerReach));
		AABB aabb = cameraEntity.getBoundingBox().expandTowards(viewVector.scale(playerReach)).inflate(1.0D, 1.0D, 1.0D);
		EntityHitResult entityTrace = ProjectileUtil.getEntityHitResult(cameraEntity, eyePosition, traceEnd, aabb, entity -> !entity.isSpectator() && entity.isPickable(), playerReachSqr);
		
		if(entityTrace != null)
		{
			double distanceSq = eyePosition.distanceToSqr(entityTrace.getLocation());
			
			if(distanceSq < playerReachSqr || blockTrace == null)
			{
				return entityTrace;
			}
		}
		
		return blockTrace;
	}
	
	@Nullable
	private Vec2f project2D(Vec3 position, Matrix4f modelView, Matrix4f projection)
	{
		Vector4f vec = new Vector4f((float) position.x(), (float) position.y(), (float) position.z(), 1.0F);
		vec.transform(modelView);
		vec.transform(projection);
		
		if(vec.w() == 0.0F)
		{
			return null;
		}
		
		float w = (1.0F / vec.w()) * 0.5F;
		float x = vec.x() * w + 0.5F;
		float y = vec.y() * w + 0.5F;
		float z = vec.z() * w + 0.5F;
		vec.set(x, y, z, w);
		
		x *= Minecraft.getInstance().getWindow().getScreenWidth();
		y *= Minecraft.getInstance().getWindow().getScreenHeight();
		
		if(Float.isInfinite(x) || Float.isInfinite(y) || Float.isNaN(x) || Float.isNaN(y))
		{
			return null;
		}
		
		return new Vec2f(x, y);
	}
	
	public boolean skipRenderPlayer()
	{
		return this.cameraDistance < 0.80 && Config.CLIENT.keepCameraOutOfHead() && ShoulderInstance.getInstance().doShoulderSurfing();
	}
	
	public double getPlayerReach()
	{
		return Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
	}
	
	public double getCameraDistance()
	{
		return this.cameraDistance;
	}
	
	public static ShoulderRenderer getInstance()
	{
		return INSTANCE;
	}
}
