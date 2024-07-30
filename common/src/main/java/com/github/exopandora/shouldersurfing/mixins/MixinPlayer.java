package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class MixinPlayer extends Entity
{
	protected MixinPlayer(EntityType<? extends LivingEntity> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	public @NotNull HitResult pick(double interactionRange, float partialTick, boolean stopOnFluid)
	{
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing() && this.level().isClientSide)
		{
			PickContext pickContext = new PickContext.Builder(camera)
				.withFluidContext(stopOnFluid ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE)
				.withEntity(this)
				.build();
			return instance.getObjectPicker().pickBlocks(pickContext, interactionRange, partialTick);
		}
		
		return super.pick(interactionRange, partialTick, stopOnFluid);
	}
}
