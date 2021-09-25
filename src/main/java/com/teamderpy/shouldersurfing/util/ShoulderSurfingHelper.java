package com.teamderpy.shouldersurfing.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.google.common.base.Predicate;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShoulderSurfingHelper
{
	private static final Vec3 ZERO = new Vec3(0, 0, 0);
	private static final Predicate<Entity> NOT_SPECTATING_COLLIDERS = entity ->
	{
		return (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) && entity != null && entity.canBeCollidedWith();
	};
	
	@Nullable
	public static Vec2f project2D(Vec3 position)
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
		
		if(GLU.gluProject((float) position.xCoord, (float) position.yCoord, (float) position.zCoord, modelview, projection, viewport, screen))
		{
			return new Vec2f(screen.get(0), screen.get(1));
		}
		
		return null;
	}
	
	public static double cameraDistance(World world, double distance)
	{
		Vec3 view = Minecraft.getMinecraft().getRenderViewEntity().getPositionEyes(Minecraft.getMinecraft().timer.renderPartialTicks);
		Vec3 cameraOffset = ShoulderSurfingHelper.cameraOffset(distance);
		
		for(int i = 0; i < 8; i++)
		{
			Vec3 offset = new Vec3((i & 1) * 2, (i >> 1 & 1) * 2, (i >> 2 & 1) * 2).subtract(1, 1, 1);
			offset = new Vec3(offset.xCoord * 0.1D, offset.yCoord * 0.1D, offset.zCoord * 0.1D);
			Vec3 head = view.add(offset);
			Vec3 camera = head.add(cameraOffset);
			
			MovingObjectPosition result = world.rayTraceBlocks(head, camera, false, true, false);
			
			if(result != null)
			{
				double newDistance = result.hitVec.distanceTo(view);
				
				if(newDistance < distance)
				{
					distance = newDistance;
				}
			}
		}
		
		return distance;
	}
	
	public static MovingObjectPosition traceFromEyes(Entity renderView, PlayerControllerMP playerController, double playerReachOverride, final float partialTicks)
	{
		double blockReach = Math.max(playerController.getBlockReachDistance(), playerReachOverride);
		MovingObjectPosition rayTrace = renderView.rayTrace(blockReach, partialTicks);
		Vec3 eyes = renderView.getPositionEyes(partialTicks);
		double entityReach = blockReach;
		
		if(playerController.extendedReach())
		{
			entityReach = Math.max(6.0D, playerReachOverride);
			blockReach = entityReach;
		}
		
		if(rayTrace != null)
		{
			entityReach = rayTrace.hitVec.distanceTo(eyes);
		}
		
		Vec3 look = renderView.getLook(1.0F);
		Vec3 end = eyes.addVector(look.xCoord * blockReach, look.yCoord * blockReach, look.zCoord * blockReach);
		
		Vec3 entityHitVec = null;
		List<Entity> list = Minecraft.getMinecraft().theWorld.getEntitiesInAABBexcluding(renderView, renderView.getEntityBoundingBox().expand(look.xCoord * blockReach, look.yCoord * blockReach, look.zCoord * blockReach).expand(1.0D, 1.0D, 1.0D), NOT_SPECTATING_COLLIDERS);
		Entity pointedEntity = null;
		double minEntityReach = entityReach;
		
		for(Entity entity : list)
		{
			AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(entity.getCollisionBorderSize(), entity.getCollisionBorderSize(), entity.getCollisionBorderSize());
			MovingObjectPosition MovingObjectPosition = axisalignedbb.calculateIntercept(eyes, end);
			
			if(axisalignedbb.isVecInside(eyes))
			{
				if(minEntityReach >= 0.0D)
				{
					pointedEntity = entity;
					entityHitVec = MovingObjectPosition == null ? eyes : MovingObjectPosition.hitVec;
					minEntityReach = 0.0D;
				}
			}
			else if(MovingObjectPosition != null)
			{
				double distanceSq = eyes.distanceTo(MovingObjectPosition.hitVec);
				
				if(distanceSq < minEntityReach || minEntityReach == 0.0D)
				{
					if(entity == renderView.ridingEntity && !entity.canRiderInteract())
					{
						if(minEntityReach == 0.0D)
						{
							pointedEntity = entity;
							entityHitVec = MovingObjectPosition.hitVec;
						}
					}
					else
					{
						pointedEntity = entity;
						entityHitVec = MovingObjectPosition.hitVec;
						minEntityReach = distanceSq;
					}
				}
			}
		}
		
		if(pointedEntity != null && (minEntityReach < entityReach || rayTrace == null))
		{
			return new MovingObjectPosition(pointedEntity, entityHitVec);
		}
		
		return rayTrace;
	}
	
	public static Entry<Vec3, Vec3> shoulderSurfingLook(Entity entity, float partialTicks, double distanceSq)
	{
		Vec3 cameraOffset = ShoulderSurfingHelper.cameraOffset(ShoulderState.getCameraDistance());
		Vec3 offset = ShoulderSurfingHelper.rayTraceHeadOffset(cameraOffset);
		Vec3 start = entity.getPositionEyes(partialTicks).add(cameraOffset);
		Vec3 look = entity.getLook(partialTicks);
		double length = offset.lengthVector();
		length = length * length;
		
		if(Config.CLIENT.limitPlayerReach() && length < distanceSq)
		{
			distanceSq -= length;
		}
		
		double distance = Math.sqrt(distanceSq) + cameraOffset.distanceTo(offset);
		look = new Vec3(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
		Vec3 end = start.add(look);
		
		return new SimpleEntry<Vec3, Vec3>(start, end);
	}
	
	public static Vec3 cameraOffset(double distance)
	{
		Vec3 result = new Vec3(Config.CLIENT.getOffsetX(), Config.CLIENT.getOffsetY(), -Config.CLIENT.getOffsetZ())
			.rotatePitch((float) Math.toRadians(-Minecraft.getMinecraft().getRenderViewEntity().rotationPitch))
			.rotateYaw((float) Math.toRadians(-Minecraft.getMinecraft().getRenderViewEntity().rotationYaw))
			.normalize();
		return new Vec3(result.xCoord * distance, result.yCoord * distance, result.zCoord * distance);
	}
	
	public static Vec3 rayTraceHeadOffset(Vec3 cameraOffset)
	{
		Vec3 view = Minecraft.getMinecraft().getRenderViewEntity().getLookVec();
		return ShoulderSurfingHelper.lineIntersection(ZERO, view, cameraOffset, view);
	}
	
	public static Vec3 lineIntersection(Vec3 planePoint, Vec3 planeNormal, Vec3 linePoint, Vec3 lineNormal)
	{
		double distance = (planeNormal.dotProduct(planePoint) - planeNormal.dotProduct(linePoint)) / planeNormal.dotProduct(lineNormal);
		return linePoint.addVector(lineNormal.xCoord * distance, lineNormal.yCoord * distance, lineNormal.zCoord * distance);
	}
	
	public static boolean isHoldingSpecialItem()
	{
		final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		if(player != null)
		{
			List<String> overrides = Config.CLIENT.getAdaptiveCrosshairItems();
			ItemStack stack = player.getHeldItem();
			
			if(stack != null)
			{
				Item current = stack.getItem();
				
				if(current instanceof ItemPotion && ItemPotion.isSplash(stack.getMetadata()))
				{
					return true;
				}
				else if(overrides.contains(Item.itemRegistry.getNameForObject(current).toString()))
				{
					return true;
				}
			}
			
			ItemStack item = player.getItemInUse();
			
			if(item != null)
			{
				Item current = stack.getItem();
				
				if(current instanceof ItemBow)
				{
					return true;
				}
				else if(overrides.contains(current.getRegistryName().toString()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void setPerspective(Perspective perspective)
	{
		Minecraft.getMinecraft().gameSettings.thirdPersonView = perspective.getPointOfView();
		ShoulderState.setEnabled(Perspective.SHOULDER_SURFING.equals(perspective));
	}
	
	public static float getShadersResmul()
	{
		switch(ShoulderState.getShaderType())
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
