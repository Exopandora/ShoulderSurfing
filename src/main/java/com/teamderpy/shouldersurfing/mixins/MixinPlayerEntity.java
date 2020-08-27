package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.event.ClientEventHandler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends Entity
{
	public MixinPlayerEntity(EntityType<?> entityTypeIn, World worldIn)
	{
		super(entityTypeIn, worldIn);
	}
	
	public RayTraceResult pick(double distance, float partialTicks, boolean stopOnFluid)
	{
		if(ClientEventHandler.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			final float yaw = (float) Math.toRadians(this.rotationYaw);
			
			double yawXZlength = MathHelper.sin((float) Math.toRadians(Config.CLIENT.getShoulderRotationYaw())) * ClientEventHandler.cameraDistance;
			double yawX = MathHelper.cos(yaw) * yawXZlength;
			double yawZ = MathHelper.sin(yaw) * yawXZlength;
			
			Vector3d start = this.getEyePosition(partialTicks).add(yawX, 0, yawZ);
			Vector3d look = this.getLook(partialTicks);
			Vector3d end = start.add(look.x * distance, look.y * distance, look.z * distance);
			
			return this.world.rayTraceBlocks(new RayTraceContext(start, end, RayTraceContext.BlockMode.OUTLINE, stopOnFluid ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, this));
		}
		
		return super.pick(distance, partialTicks, stopOnFluid);
	}
}
