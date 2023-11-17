package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends Entity
{
	protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, World level)
	{
		super(type, level);
	}
	
	@Override
	public RayTraceResult pick(double distance, float partialTicks, boolean stopOnFluid)
	{
		Minecraft minecraft = Minecraft.getInstance();
		ActiveRenderInfo camera = minecraft.getEntityRenderDispatcher().camera;
		
		if(ShoulderInstance.getInstance().doShoulderSurfing() && camera != null)
		{
			RayTraceContext.FluidMode fluidContext = stopOnFluid ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE;
			return ShoulderHelper.traceBlocks(camera, this, fluidContext, distance, partialTicks, !Config.CLIENT.getCrosshairType().isDynamic());
		}
		
		return super.pick(distance, partialTicks, stopOnFluid);
	}
}
