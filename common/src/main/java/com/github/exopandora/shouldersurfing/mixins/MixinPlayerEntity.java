package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.client.ShoulderRayTracer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends Entity
{
	protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, World level)
	{
		super(type, level);
	}
	
	@Override
	public @NotNull RayTraceResult pick(double distance, float partialTicks, boolean stopOnFluid)
	{
		Minecraft minecraft = Minecraft.getInstance();
		ActiveRenderInfo camera = minecraft.getEntityRenderDispatcher().camera;
		
		if(ShoulderInstance.getInstance().doShoulderSurfing() && camera != null)
		{
			RayTraceContext.FluidMode fluidContext = stopOnFluid ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE;
			boolean isCrosshairDynamic = ShoulderInstance.getInstance().isCrosshairDynamic(camera.getEntity());
			return ShoulderRayTracer.traceBlocks(camera, this, fluidContext, distance, partialTicks, !isCrosshairDynamic);
		}
		
		return super.pick(distance, partialTicks, stopOnFluid);
	}
}
