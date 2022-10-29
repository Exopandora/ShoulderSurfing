package com.teamderpy.shouldersurfing.forge;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.teamderpy.shouldersurfing.client.KeyHandler;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import com.teamderpy.shouldersurfing.mixins.CameraAccessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler
{
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent event)
	{
		if(Phase.START.equals(event.phase))
		{
			ShoulderInstance.getInstance().tick();
		}
	}
	
	@SubscribeEvent
	public static void preRenderPlayerEvent(RenderPlayerEvent.Pre event)
	{
		if(event.isCancelable() && event.getEntity().equals(Minecraft.getInstance().player) && Minecraft.getInstance().screen == null && ShoulderRenderer.getInstance().skipRenderPlayer())
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void preRenderGuiOverlayEvent(RenderGuiOverlayEvent.Pre event)
	{
		if(VanillaGuiOverlay.CROSSHAIR.id().equals(event.getOverlay().id()))
		{
			ShoulderRenderer.getInstance().offsetCrosshair(event.getPoseStack(), event.getWindow(), event.getPartialTick());
		}
		//Using BOSS_EVENT_PROGRESS to pop matrix because when CROSSHAIR is cancelled it will not fire RenderGuiOverlayEvent.Post and cause a stack overflow
		else if(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id().equals(event.getOverlay().id()))
		{
			ShoulderRenderer.getInstance().clearCrosshairOffset(event.getPoseStack());
		}
	}
	
	@SubscribeEvent
	@SuppressWarnings("resource")
	public static void computeCameraAnglesEvent(ComputeCameraAngles event)
	{
		ShoulderRenderer.getInstance().offsetCamera(event.getCamera(), Minecraft.getInstance().level, event.getPartialTick());
	}
	
	@SubscribeEvent
	public static void renderLevelStageEvent(RenderLevelStageEvent event)
	{
		if(RenderLevelStageEvent.Stage.AFTER_SKY.equals(event.getStage()))
		{
			ShoulderRenderer.getInstance().updateDynamicRaytrace(event.getCamera(), event.getPoseStack().last().pose(), event.getProjectionMatrix(), event.getPartialTick());
		}
	}
	
	@SubscribeEvent
	public static void renderLevelStageEvent2(RenderLevelStageEvent event)
	{
		if(RenderLevelStageEvent.Stage.AFTER_SKY.equals(event.getStage()))
		{
			CameraAccessor accessor = (CameraAccessor) event.getCamera();
			Player player = Minecraft.getInstance().player;
			ItemStack stack = player.getMainHandItem();
			
			if(stack.getItem().equals(Items.BOW) && player.getUseItemRemainingTicks() > 0)
			{
				if(true) {
					return;
				}
				float x = player.getXRot();
				float y = player.getYRot();
				float z = 0F;
				float velocity = BowItem.getPowerForTime(stack.getItem().getUseDuration(stack) - player.getUseItemRemainingTicks()) * 3F;
				float f = -Mth.sin(y * ((float)Math.PI / 180F)) * Mth.cos(x * ((float)Math.PI / 180F));
				float f1 = -Mth.sin((x + z) * ((float)Math.PI / 180F));
				float f2 = Mth.cos(y * ((float)Math.PI / 180F)) * Mth.cos(x * ((float)Math.PI / 180F));
				Level level = Minecraft.getInstance().level;
				Vec3 playerMotion = player.getDeltaMovement();
				
				double px = Mth.lerp(event.getPartialTick(), player.xo, player.getX());
				double py = Mth.lerp(event.getPartialTick(), player.yo, player.getY()) + Mth.lerp(event.getPartialTick(), accessor.getEyeHeightOld(), accessor.getEyeHeight());
				double pz = Mth.lerp(event.getPartialTick(), player.zo, player.getZ());
				
				Vec3 start = new Vec3(px, py, pz);
				Vec3 motion = new Vec3(f, f1, f2).normalize().scale(velocity).add(playerMotion.x, player.isOnGround() ? 0.0D : playerMotion.y, playerMotion.z);
				
				RenderSystem.setShaderColor(1, 1, 1, 1);
				MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
				VertexConsumer builder = buffer.getBuffer(RenderType.lines());
				
				for(int i = 0; i < 300; i++)
				{
					Vec3 end = start.add(motion);
					HitResult hitresult = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
					
					if(hitresult.getType() != HitResult.Type.MISS)
					{
						end = hitresult.getLocation();
					}
					
					PoseStack poseStack = event.getPoseStack();
					Vec3 projected = event.getCamera().getPosition();
					poseStack.pushPose();
					poseStack.translate(start.x() - projected.x(), start.y() - projected.y(), start.z() - projected.z());
					
					Vec3 delta = end.subtract(start);
					Vec3 normal = delta.normalize();
					Pose pose = poseStack.last();
					Matrix4f matrix = pose.pose();
					
					builder.vertex(matrix, 0, 0, 0)
						.color(1.0F, 0, 0, 1F)
						.normal(pose.normal(), (float) normal.x(), (float) normal.y(), (float) normal.z())
						.endVertex();
					builder.vertex(matrix, (float) delta.x(), (float) delta.y(), (float) delta.z())
						.color(1.0F, 0, 0, 1F)
						.normal(pose.normal(), (float) normal.x(), (float) normal.y(), (float) normal.z())
						.endVertex();
					
					poseStack.popPose();
					
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
//									buffer.endBatch(RenderType.lines());
									return;
								}
							}
						}
					}
				}
				
//				buffer.endBatch(RenderType.lines());
			}
			else if(stack.getItem().equals(Items.CROSSBOW) && CrossbowItem.isCharged(stack))
			{
				float velocity = 3.15F;//CrossbowItem.getShootingPower(stack);
	            Vec3 vec3 = player.getViewVector(event.getPartialTick());
				float f = (float) vec3.x();
				float f1 = (float) vec3.y();
				float f2 = (float) vec3.z();
				Level level = Minecraft.getInstance().level;
				Vec3 playerMotion = player.getDeltaMovement();
				double px = Mth.lerp(event.getPartialTick(), player.xo, player.getX());
				double py = Mth.lerp(event.getPartialTick(), player.yo, player.getY()) + Mth.lerp(event.getPartialTick(), accessor.getEyeHeightOld(), accessor.getEyeHeight());
				double pz = Mth.lerp(event.getPartialTick(), player.zo, player.getZ());
				
				Vec3 start = new Vec3(px, py, pz);
				Vec3 oldMotion = player.position().subtract(player.xOld, player.yOld, player.zOld);
				Vec3 motionInterp = oldMotion.lerp(playerMotion, event.getPartialTick());
				Vec3 motion = new Vec3(f, f1, f2).normalize().scale(velocity).add(playerMotion.x, player.isOnGround() ? 0.0D : motionInterp.y, playerMotion.z);
				
				RenderSystem.setShaderColor(1, 1, 1, 1);
				MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
				VertexConsumer builder = buffer.getBuffer(RenderType.lines());
				
				for(int i = 0; i < 300; i++)
				{
					Vec3 end = start.add(motion);
					HitResult hitresult = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
					
					if(hitresult.getType() != HitResult.Type.MISS)
					{
						return;
					}
					
					PoseStack poseStack = event.getPoseStack();
					Vec3 projected = event.getCamera().getPosition();
					poseStack.pushPose();
					poseStack.translate(start.x() - projected.x(), start.y() - projected.y(), start.z() - projected.z());
					
					Vec3 delta = end.subtract(start);
					Vec3 normal = delta.normalize();
					Pose pose = poseStack.last();
					Matrix4f matrix = pose.pose();
					
					builder.vertex(matrix, 0, 0, 0)
						.color(1.0F, 0, 0, 1)
						.normal(pose.normal(), (float) normal.x(), (float) normal.y(), (float) normal.z())
						.endVertex();
					builder.vertex(matrix, (float) delta.x(), (float) delta.y(), (float) delta.z())
						.color(1.0F, 0, 0, 1)
						.normal(pose.normal(), (float) normal.x(), (float) normal.y(), (float) normal.z())
						.endVertex();
					
					poseStack.popPose();
					
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
//									buffer.endBatch(RenderType.lines());
									return;
								}
							}
						}
					}
				}
				
//				buffer.endBatch(RenderType.lines());
			}
		}
	}
	
	@SubscribeEvent
	public static void inputEvent(InputEvent event)
	{
		KeyHandler.onInput();
	}
}
