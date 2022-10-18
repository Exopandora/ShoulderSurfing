package com.teamderpy.shouldersurfing.client;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.google.common.base.Predicate;
import com.teamderpy.shouldersurfing.compatibility.EnumShaderCompatibility;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.math.Vec2f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShoulderRenderer
{
	private static final ShoulderRenderer INSTANCE = new ShoulderRenderer();
	private static final Predicate<Entity> NOT_SPECTATING_COLLIDERS = entity ->
	{
		return (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) && entity != null && entity.canBeCollidedWith();
	};
	private double cameraDistance;
	private Vec2f lastTranslation = Vec2f.ZERO;
	private Vec2f translation = Vec2f.ZERO;
	private Vec2f projected;
	private EnumShaderCompatibility shaders = EnumShaderCompatibility.NONE;
	
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
	
	public void offsetCamera(float x, float y, float z)
	{
		final World world = Minecraft.getMinecraft().theWorld;
		
		if(ShoulderInstance.getInstance().doShoulderSurfing() && world != null)
		{
			Vec3 offset = new Vec3(Config.CLIENT.getOffsetX(), -Config.CLIENT.getOffsetY(), -Config.CLIENT.getOffsetZ());
			this.cameraDistance = this.calcCameraDistance(world, offset.lengthVector());
			Vec3 scaled = offset.normalize();
			scaled = new Vec3(scaled.xCoord * this.cameraDistance, scaled.yCoord * this.cameraDistance, scaled.zCoord * this.cameraDistance);
			GlStateManager.translate(scaled.xCoord, scaled.yCoord, scaled.zCoord);
		}
		else
		{
			GlStateManager.translate(x, y, z);
		}
	}
	
	private double calcCameraDistance(World world, double distance)
	{
		Vec3 cameraPos = Minecraft.getMinecraft().getRenderViewEntity().getPositionEyes(Minecraft.getMinecraft().timer.renderPartialTicks);
		Vec3 cameraOffset = ShoulderHelper.calcCameraOffset(distance);
		
		for(int i = 0; i < 8; i++)
		{
			Vec3 offset = new Vec3((i & 1) * 2, (i >> 1 & 1) * 2, (i >> 2 & 1) * 2).subtract(new Vec3(1, 1, 1));
			offset = new Vec3(offset.xCoord * 0.1D, offset.yCoord * 0.1D, offset.zCoord * 0.1D);
			Vec3 from = cameraPos.addVector(offset.xCoord, offset.yCoord, offset.zCoord);
			Vec3 to = from.addVector(cameraOffset.xCoord, cameraOffset.yCoord, cameraOffset.zCoord);
			MovingObjectPosition hitResult = world.rayTraceBlocks(from, to, false, true, false);
			
			if(hitResult != null)
			{
				double newDistance = hitResult.hitVec.distanceTo(cameraPos);
				
				if(newDistance < distance)
				{
					distance = newDistance;
				}
			}
		}
		
		return distance;
	}
	
	public void updateDynamicRaytrace(float partialTick)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing())
		{
			Entity cameraEntity = Minecraft.getMinecraft().getRenderViewEntity();
			PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
			MovingObjectPosition hitResult = this.rayTraceFromEyes(cameraEntity, controller, this.getPlayerReach(), partialTick);
			Vec3 position = hitResult.hitVec.subtract(cameraEntity.getPositionEyes(Minecraft.getMinecraft().timer.renderPartialTicks).subtract(0, cameraEntity.getEyeHeight(), 0));
			this.projected = this.project2D(position);
		}
	}
	
	private MovingObjectPosition rayTraceFromEyes(Entity cameraEntity, PlayerControllerMP gameMode, double playerReachOverride, final float partialTick)
	{
		double playerReach = Math.max(gameMode.getBlockReachDistance(), playerReachOverride);
		MovingObjectPosition blockTrace = cameraEntity.rayTrace(playerReach, partialTick);
		Vec3 eyePosition = cameraEntity.getPositionEyes(partialTick);
		
		if(gameMode.extendedReach())
		{
			playerReach = Math.max(playerReach, gameMode.isInCreativeMode() ? 6.0D : 3.0D);
		}
		
		double playerReachSqr = playerReach * playerReach;
		
		if(blockTrace != null)
		{
			playerReachSqr = blockTrace.hitVec.squareDistanceTo(eyePosition);
		}
		
		Vec3 viewVector = cameraEntity.getLook(1.0F);
		viewVector = new Vec3(viewVector.xCoord * playerReach, viewVector.yCoord * playerReach, viewVector.zCoord * playerReach);
		Vec3 traceEnd = eyePosition.addVector(viewVector.xCoord, viewVector.yCoord, viewVector.zCoord);
		AxisAlignedBB aabb = cameraEntity.getEntityBoundingBox().addCoord(viewVector.xCoord, viewVector.yCoord, viewVector.zCoord).expand(1.0D, 1.0D, 1.0D);
		List<Entity> list = Minecraft.getMinecraft().theWorld.getEntitiesInAABBexcluding(cameraEntity, aabb, NOT_SPECTATING_COLLIDERS);
		Vec3 entityHitVec = null;
		Entity pointedEntity = null;
		double minEntityReachSqr = playerReachSqr;
		
		for(Entity entity : list)
		{
			AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(entity.getCollisionBorderSize(), entity.getCollisionBorderSize(), entity.getCollisionBorderSize());
			MovingObjectPosition raytraceresult = axisalignedbb.calculateIntercept(eyePosition, traceEnd);
			
			if(axisalignedbb.isVecInside(eyePosition))
			{
				if(minEntityReachSqr >= 0.0D)
				{
					pointedEntity = entity;
					entityHitVec = raytraceresult == null ? eyePosition : raytraceresult.hitVec;
					minEntityReachSqr = 0.0D;
				}
			}
			else if(raytraceresult != null)
			{
				double distanceSq = eyePosition.squareDistanceTo(raytraceresult.hitVec);
				
				if(distanceSq < minEntityReachSqr || minEntityReachSqr == 0.0D)
				{
					if(entity == cameraEntity.ridingEntity && !entity.canRiderInteract())
					{
						if(minEntityReachSqr == 0.0D)
						{
							pointedEntity = entity;
							entityHitVec = raytraceresult.hitVec;
						}
					}
					else
					{
						pointedEntity = entity;
						entityHitVec = raytraceresult.hitVec;
						minEntityReachSqr = distanceSq;
					}
				}
			}
		}
		
		if(pointedEntity != null && (minEntityReachSqr < playerReachSqr || blockTrace == null))
		{
			return new MovingObjectPosition(pointedEntity, entityHitVec);
		}
		
		return blockTrace;
	}
	
	@Nullable
	private Vec2f project2D(Vec3 position)
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
			return new Vec2f(screen.get(0), screen.get(1)).divide(this.getShadersResmul());
		}
		
		return null;
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
	
	public void setShaderType(EnumShaderCompatibility shaders)
	{
		this.shaders = shaders;
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
