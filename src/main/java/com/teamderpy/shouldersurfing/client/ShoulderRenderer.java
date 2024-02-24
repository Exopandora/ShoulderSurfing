package com.teamderpy.shouldersurfing.client;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.valkyrienskies.mod.common.piloting.IShipPilot;
import org.valkyrienskies.mod.common.ships.entity_interaction.EntityShipMountData;
import org.valkyrienskies.mod.common.ships.ship_transform.ShipTransform;
import org.valkyrienskies.mod.common.ships.ship_world.IWorldVS;
import org.valkyrienskies.mod.common.util.ValkyrienUtils;

import com.teamderpy.shouldersurfing.compatibility.EnumShaderCompatibility;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.math.Vec2f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import valkyrienwarfare.api.TransformType;

@SideOnly(Side.CLIENT)
@SuppressWarnings("deprecation")
public class ShoulderRenderer
{
	private static final ShoulderRenderer INSTANCE = new ShoulderRenderer();
	private static final Vec3d VECTOR_NEGATIVE_Y = new Vec3d(0, -1, 0);
	private double cameraDistance;
	private Vec2f lastTranslation = Vec2f.ZERO;
	private Vec2f translation = Vec2f.ZERO;
	private Vec2f projected;
	private EnumShaderCompatibility shaders = EnumShaderCompatibility.NONE;
	private boolean isValkyrienSkiesInstalled = false;
	private double cameraOffsetX;
	private double cameraOffsetY;
	private double cameraOffsetZ;
	private float playerAlpha = 1.0F;
	private boolean shouldRenderTransparent = false;
	
	public void offsetCrosshair(ScaledResolution window, float partialTicks)
	{
		if(this.projected != null)
		{
			Vec2f scaledDimensions = new Vec2f(window.getScaledWidth(), window.getScaledHeight());
			Vec2f dimensions = new Vec2f(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
			Vec2f scale = scaledDimensions.divide(dimensions);
			Vec2f center = dimensions.divide(2); // In actual monitor pixels
			Vec2f projectedOffset = this.projected.subtract(center).scale(scale);
			Vec2f interpolated = projectedOffset.subtract(this.lastTranslation).scale(partialTicks);
			this.translation = this.lastTranslation.add(interpolated);
		}
		
		if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderInstance.getInstance().doShoulderSurfing())
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(this.translation.getX(), -this.translation.getY(), 0.0F);
			this.lastTranslation = this.translation;
		}
		else
		{
			this.lastTranslation = Vec2f.ZERO;
		}
	}
	
	public void clearCrosshairOffset()
	{
		if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderInstance.getInstance().doShoulderSurfing() && !Vec2f.ZERO.equals(this.lastTranslation))
		{
			GlStateManager.popMatrix();
		}
	}
	
	public void offsetCamera(float x, float y, float z, float yaw, float pitch)
	{
		final World world = Minecraft.getMinecraft().world;
		
		if(ShoulderInstance.getInstance().doShoulderSurfing() && world != null)
		{
			ShoulderInstance instance = ShoulderInstance.getInstance();
			Entity cameraEntity = Minecraft.getMinecraft().getRenderViewEntity();
			float partialTick = Minecraft.getMinecraft().getRenderPartialTicks();
			double targetXOffset = Config.CLIENT.getOffsetX();
			double targetYOffset = Config.CLIENT.getOffsetY();
			double targetZOffset = Config.CLIENT.getOffsetZ();
			
			if(cameraEntity.isRiding())
			{
				targetXOffset += Config.CLIENT.getOffsetX() * (Config.CLIENT.getPassengerOffsetXMultiplier() - 1);
				targetYOffset += Config.CLIENT.getOffsetY() * (Config.CLIENT.getPassengerOffsetYMultiplier() - 1);
				targetZOffset += Config.CLIENT.getOffsetZ() * (Config.CLIENT.getPassengerOffsetZMultiplier() - 1);
			}
			
			if(cameraEntity.isSprinting())
			{
				targetXOffset += Config.CLIENT.getOffsetX() * (Config.CLIENT.getSprintOffsetXMultiplier() - 1);
				targetYOffset += Config.CLIENT.getOffsetY() * (Config.CLIENT.getSprintOffsetYMultiplier() - 1);
				targetZOffset += Config.CLIENT.getOffsetZ() * (Config.CLIENT.getSprintOffsetZMultiplier() - 1);
			}
			
			if(Config.CLIENT.doCenterCameraWhenClimbing() && cameraEntity instanceof EntityLivingBase && ((EntityLivingBase) cameraEntity).isOnLadder())
			{
				targetXOffset = 0;
			}
			
			if(ShoulderHelper.angle(cameraEntity.getLookVec(), VECTOR_NEGATIVE_Y) < Config.CLIENT.getCenterCameraWhenLookingDownAngle() * ShoulderHelper.DEG_TO_RAD)
			{
				targetXOffset = 0;
				targetYOffset = 0;
			}
			
			if(Config.CLIENT.doDynamicallyAdjustOffsets())
			{
				Vec3d localCameraOffset = new Vec3d(targetXOffset, targetYOffset, -targetZOffset);
				Vec3d worldCameraOffset = localCameraOffset
					.rotatePitch((float) Math.toRadians(-pitch))
					.rotateYaw((float) Math.toRadians(-yaw));
				Vec3d worldXYOffset = ShoulderHelper.calcRayTraceHeadOffset(worldCameraOffset);
				Vec3d eyePosition = cameraEntity.getPositionEyes(partialTick);
				double absOffsetX = Math.abs(targetXOffset);
				double absOffsetY = Math.abs(targetYOffset);
				double absOffsetZ = Math.abs(targetZOffset);
				double targetX = absOffsetX;
				double targetY = absOffsetY;
				double clearance = Minecraft.getMinecraft().getRenderViewEntity().width / 3.0D;
				
				for(double dz = 0; dz <= absOffsetZ; dz += 0.03125D)
				{
					double scale = dz / absOffsetZ;
					Vec3d from = eyePosition.add(worldCameraOffset.scale(scale));
					Vec3d to = eyePosition.add(worldXYOffset).add(cameraEntity.getLookVec().scale(-dz));
					RayTraceResult hitResult = world.rayTraceBlocks(from, to, false, true, false);
					
					if(hitResult != null)
					{
						double distance = hitResult.hitVec.distanceTo(from);
						double newTargetX = Math.max(distance + absOffsetX * scale - clearance, 0);
						
						if(newTargetX < targetX)
						{
							targetX = newTargetX;
						}
						
						double newTargetY = Math.max(distance + absOffsetY * scale - clearance, 0);
						
						if(newTargetY < targetY)
						{
							targetY = newTargetY;
						}
					}
				}
				
				targetXOffset = Math.signum(Config.CLIENT.getOffsetX()) * targetX;
				targetYOffset = Math.signum(Config.CLIENT.getOffsetY()) * targetY;
			}
			
			instance.setTargetOffsetX(targetXOffset);
			instance.setTargetOffsetY(targetYOffset);
			instance.setTargetOffsetZ(targetZOffset);
			
			double offsetX = ShoulderHelper.lerp(partialTick, instance.getOffsetXOld(), instance.getOffsetX());
			double offsetY = ShoulderHelper.lerp(partialTick, instance.getOffsetYOld(), instance.getOffsetY());
			double offsetZ = ShoulderHelper.lerp(partialTick, instance.getOffsetZOld(), instance.getOffsetZ());
			Vec3d offset = new Vec3d(offsetX, offsetY, offsetZ);
			this.cameraDistance = this.calcCameraDistance(world, offset.length(), yaw, pitch, partialTick);
			Vec3d scaled = offset.normalize().scale(this.cameraDistance);
			this.cameraOffsetX = scaled.x;
			this.cameraOffsetY = scaled.y;
			this.cameraOffsetZ = scaled.z;
			GlStateManager.translate(scaled.x, -scaled.y, -scaled.z);
		}
		else
		{
			GlStateManager.translate(x, y, z);
		}
	}
	
	private double calcCameraDistance(World world, double distance, float yaw, float pitch, float partialTick)
	{
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		Vec3d cameraPos = renderView.getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks());
		Vec3d cameraOffset = ShoulderHelper.calcCameraOffset(distance, yaw, pitch, partialTick);
		
		if(this.isValkyrienSkiesInstalled)
		{
			EntityShipMountData mountData = ValkyrienUtils.getMountedShipAndPos(Minecraft.getMinecraft().getRenderViewEntity());
			
			if(mountData.getMountedShip() != null)
			{
				if(!Config.CLIENT.doCompatibilityValkyrienSkiesCameraShipCollision())
				{
					IShipPilot pilot = (IShipPilot) Minecraft.getMinecraft().player;
					((IWorldVS) world).excludeShipFromRayTracer(pilot.getPilotedShip());
				}
				
				if(mountData.isMounted())
				{
					ShipTransform renderTransform = mountData.getMountedShip().getShipTransformationManager().getRenderTransform();
					cameraOffset = renderTransform.rotate(cameraOffset, TransformType.SUBSPACE_TO_GLOBAL);
				}
			}
		}
		
		for(int i = 0; i < 8; i++)
		{
			Vec3d offset = new Vec3d((i & 1) * 2, (i >> 1 & 1) * 2, (i >> 2 & 1) * 2)
				.subtract(1, 1, 1)
				.scale(0.1F)
				.rotateYaw(-yaw * ShoulderHelper.DEG_TO_RAD);
			Vec3d from = cameraPos.add(offset);
			Vec3d to = from.add(cameraOffset);
			RayTraceResult hitResult = world.rayTraceBlocks(from, to, false, true, false);
			
			if(hitResult != null)
			{
				double newDistance = hitResult.hitVec.distanceTo(cameraPos);
				
				if(newDistance < distance)
				{
					distance = newDistance;
				}
			}
		}
		
		if(this.isValkyrienSkiesInstalled && !Config.CLIENT.doCompatibilityValkyrienSkiesCameraShipCollision())
		{
			EntityShipMountData mountData = ValkyrienUtils.getMountedShipAndPos(Minecraft.getMinecraft().getRenderViewEntity());
			
			if(mountData.getMountedShip() != null)
			{
				IShipPilot pilot = (IShipPilot) Minecraft.getMinecraft().player;
				((IWorldVS) world).unexcludeShipFromRayTracer(pilot.getPilotedShip());
			}
		}
		
		return distance;
	}
	
	public void updateDynamicRaytrace(float partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing())
		{
			Minecraft minecraft = Minecraft.getMinecraft();
			Entity cameraEntity = minecraft.getRenderViewEntity();
			PlayerControllerMP controller = minecraft.playerController;
			RayTraceResult hitResult = ShoulderHelper.traceBlocksAndEntities(cameraEntity, controller, this.getPlayerReach(), false, partialTick, true, false);
			
			if(hitResult != null)
			{
				Vec3d position = hitResult.hitVec.subtract(cameraEntity.getPositionEyes(partialTick).subtract(0, cameraEntity.getEyeHeight(), 0));
				this.projected = this.project2D(position);
			}
		}
	}
	
	@Nullable
	private Vec2f project2D(Vec3d position)
	{
		FloatBuffer screen = GLAllocation.createDirectFloatBuffer(3);
		IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
		FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
		FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
		
		screen.clear();
		modelview.clear();
		projection.clear();
		viewport.clear();
		
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
		
		if(GLU.gluProject((float) position.x, (float) position.y, (float) position.z, modelview, projection, viewport, screen))
		{
			return new Vec2f(screen.get(0), screen.get(1)).divide(this.getShadersResmul());
		}
		
		return null;
	}
	
	public boolean skipEntityRendering()
	{
		return ShoulderInstance.getInstance().doShoulderSurfing() &&
			(this.cameraDistance < Minecraft.getMinecraft().getRenderViewEntity().width * Config.CLIENT.keepCameraOutOfHeadMultiplier()
				|| Minecraft.getMinecraft().getRenderViewEntity().rotationPitch < Config.CLIENT.getCenterCameraWhenLookingDownAngle() - 90);
	}
	
	public boolean preRenderCameraEntity(Entity entity, float partialTick)
	{
		if(this.skipEntityRendering())
		{
			return true;
		}
		
		if(this.shouldRenderTransparent(entity))
		{
			GlStateManager.depthMask(true);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
			this.playerAlpha = (float) MathHelper.clamp(Math.abs(this.cameraOffsetX) / (entity.width / 2.0D), 0.15F, 1.0F);
			this.shouldRenderTransparent = true;
			GlStateManager.color(1.0F, 1.0F, 1.0F, this.playerAlpha);
		}
		
		return false;
	}
	
	public void postRenderCameraEntity()
	{
		if(this.shouldRenderTransparent)
		{
			this.playerAlpha = 1.0F;
			this.shouldRenderTransparent = false;
			GlStateManager.disableBlend();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		}
	}
	
	private boolean shouldRenderTransparent(Entity entity)
	{
		return ShoulderInstance.getInstance().doShoulderSurfing() && Math.abs(this.cameraOffsetX) < (entity.width / 2.0D);
	}
	
	public double getPlayerReach()
	{
		return Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
	}
	
	public double getCameraDistance()
	{
		return this.cameraDistance;
	}
	
	public double getCameraOffsetX()
	{
		return this.cameraOffsetX;
	}
	
	public double getCameraOffsetY()
	{
		return this.cameraOffsetY;
	}
	
	public double getCameraOffsetZ()
	{
		return this.cameraOffsetZ;
	}
	
	public float getPlayerAlpha()
	{
		return this.playerAlpha;
	}
	
	public boolean shouldRenderTransparent()
	{
		return this.shouldRenderTransparent;
	}
	
	public static ShoulderRenderer getInstance()
	{
		return INSTANCE;
	}
	
	public void setShaderType(EnumShaderCompatibility shaders)
	{
		this.shaders = shaders;
	}
	
	public void setValkyrienSkiesInstalled(boolean isValkyrienSkiesInstalled)
	{
		this.isValkyrienSkiesInstalled = isValkyrienSkiesInstalled;
	}
	
	private float getShadersResmul()
	{
		switch(this.shaders)
		{
			case OLD:
				return shadersmod.client.Shaders.shaderPackLoaded ? shadersmod.client.Shaders.configRenderResMul : 1.0F;
			case NEW:
				return net.optifine.shaders.Shaders.shaderPackLoaded ? net.optifine.shaders.Shaders.configRenderResMul : 1.0F;
			default:
				return 1.0F;
		}
	}
}
