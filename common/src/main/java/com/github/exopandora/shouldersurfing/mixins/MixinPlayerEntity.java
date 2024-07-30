package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
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
	public @NotNull RayTraceResult pick(double interactionRange, float partialTick, boolean stopOnFluid)
	{
		ActiveRenderInfo camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing() && this.level.isClientSide)
		{
			PickContext pickContext = new PickContext.Builder(camera)
				.withFluidContext(stopOnFluid ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE)
				.withEntity(this)
				.build();
			return instance.getObjectPicker().pickBlocks(pickContext, interactionRange, partialTick);
		}
		
		return super.pick(interactionRange, partialTick, stopOnFluid);
	}
}
