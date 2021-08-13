package com.teamderpy.shouldersurfing.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.google.common.base.Predicates;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShoulderSurfingHelper
{
	private static final ResourceLocation PULL_PROPERTY = new ResourceLocation("pull");
	private static final ResourceLocation THROWING_PROPERTY = new ResourceLocation("throwing");
	private static final ResourceLocation CHARGED_PROPERTY = new ResourceLocation("charged");
	
	@Nullable
	public static Vec2f project2D(Vec3d position)
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
			return new Vec2f(screen.get(0), screen.get(1));
		}
		
		return null;
	}
	
	public static double cameraDistance(World world, double distance)
	{
		Vec3d view = Minecraft.getMinecraft().getRenderViewEntity().getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks());
		Vec3d cameraOffset = ShoulderSurfingHelper.cameraOffset(distance);
		
		for(int i = 0; i < 8; i++)
		{
			Vec3d offset = new Vec3d(i & 1, i >> 1 & 1, i >> 2 & 1).scale(2).subtract(1, 1, 1).scale(0.1);
			Vec3d head = view.add(offset);
			Vec3d camera = head.add(cameraOffset);
			
			RayTraceResult result = world.rayTraceBlocks(head, camera, false, true, false);
			
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
	
	public static RayTraceResult traceFromEyes(Entity renderView, PlayerControllerMP playerController, double playerReachOverride, final float partialTicks)
	{
		double blockReach = Math.max(playerController.getBlockReachDistance(), playerReachOverride);
		RayTraceResult rayTrace = renderView.rayTrace(blockReach, partialTicks);
		Vec3d eyes = renderView.getPositionEyes(partialTicks);
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
		
		Vec3d look = renderView.getLook(1.0F);
		Vec3d end = eyes.addVector(look.x * blockReach, look.y * blockReach, look.z * blockReach);
		
		Vec3d entityHitVec = null;
		List<Entity> list = Minecraft.getMinecraft().world.getEntitiesInAABBexcluding(renderView, renderView.getEntityBoundingBox().expand(look.x * blockReach, look.y * blockReach, look.z * blockReach).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, e -> e != null && e.canBeCollidedWith()));
		Entity pointedEntity = null;
		double minEntityReach = entityReach;
		
		for(Entity entity : list)
		{
			AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow((double)entity.getCollisionBorderSize());
			RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(eyes, end);
			
			if(axisalignedbb.contains(eyes))
			{
				if(minEntityReach >= 0.0D)
				{
					pointedEntity = entity;
					entityHitVec = raytraceresult == null ? eyes : raytraceresult.hitVec;
					minEntityReach = 0.0D;
				}
			}
			else if(raytraceresult != null)
			{
				double distanceSq = eyes.distanceTo(raytraceresult.hitVec);
				
				if(distanceSq < minEntityReach || minEntityReach == 0.0D)
				{
					if(entity.getLowestRidingEntity() == renderView.getLowestRidingEntity() && !entity.canRiderInteract())
					{
						if(minEntityReach == 0.0D)
						{
							pointedEntity = entity;
							entityHitVec = raytraceresult.hitVec;
						}
					}
					else
					{
						pointedEntity = entity;
						entityHitVec = raytraceresult.hitVec;
						minEntityReach = distanceSq;
					}
				}
			}
		}
		
		if(pointedEntity != null && (minEntityReach < entityReach || rayTrace == null))
		{
			return new RayTraceResult(pointedEntity, entityHitVec);
		}
		
		return rayTrace;
	}
	
	public static Entry<Vec3d, Vec3d> shoulderSurfingLook(Entity entity, float partialTicks, double distanceSq)
	{
		Vec3d cameraOffset = ShoulderSurfingHelper.cameraOffset(ShoulderState.getCameraDistance());
		Vec3d offset = ShoulderSurfingHelper.rayTraceHeadOffset(cameraOffset);
		Vec3d start = entity.getPositionEyes(partialTicks).add(cameraOffset);
		Vec3d look = entity.getLook(partialTicks);
		double length = offset.lengthVector();
		length = length * length;
		
		if(Config.CLIENT.limitPlayerReach() && length < distanceSq)
		{
			distanceSq -= length;
		}
		
		double distance = MathHelper.sqrt(distanceSq) + cameraOffset.distanceTo(offset);
		Vec3d end = start.add(look.scale(distance));
		
		return new SimpleEntry<Vec3d, Vec3d>(start, end);
	}
	
	public static Vec3d cameraOffset(double distance)
	{
		return new Vec3d(Config.CLIENT.getOffsetX(), Config.CLIENT.getOffsetY(), -Config.CLIENT.getOffsetZ())
			.rotatePitch((float) Math.toRadians(-Minecraft.getMinecraft().getRenderViewEntity().rotationPitch))
			.rotateYaw((float) Math.toRadians(-Minecraft.getMinecraft().getRenderViewEntity().rotationYaw))
			.normalize()
			.scale(distance);
	}
	
	public static Vec3d rayTraceHeadOffset(Vec3d cameraOffset)
	{
		Vec3d view = Minecraft.getMinecraft().getRenderViewEntity().getLookVec();
		return ShoulderSurfingHelper.lineIntersection(Vec3d.ZERO, view, cameraOffset, view);
	}
	
	public static Vec3d lineIntersection(Vec3d planePoint, Vec3d planeNormal, Vec3d linePoint, Vec3d lineNormal)
	{
		double distance = (planeNormal.dotProduct(planePoint) - planeNormal.dotProduct(linePoint)) / planeNormal.dotProduct(lineNormal);
		return linePoint.add(lineNormal.scale(distance));
	}
	
	public static boolean isHoldingSpecialItem()
	{
		final EntityPlayerSP player = Minecraft.getMinecraft().player;
		
		if(player != null)
		{
			List<String> overrides = Config.CLIENT.getAdaptiveCrosshairItems();
			ItemStack stack = player.getActiveItemStack();
			
			if(stack != null)
			{
				Item current = stack.getItem();
				
				if(current.getPropertyGetter(PULL_PROPERTY) != null || current.getPropertyGetter(THROWING_PROPERTY) != null)
				{
					return true;
				}
				else if(overrides.contains(current.getRegistryName().toString()))
				{
					return true;
				}
			}
			
			for(ItemStack item : player.getHeldEquipment())
			{
				if(item != null)
				{
					if(item.getItem().getPropertyGetter(CHARGED_PROPERTY) != null)
					{
						return true;
					}
					else if(overrides.contains(item.getItem().getRegistryName().toString()))
					{
						return true;
					}
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
