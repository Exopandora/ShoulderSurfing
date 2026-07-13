package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
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
abstract class PlayerMixin extends Entity {
	private PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}
	
	@Override
	public @NotNull HitResult pick(double interactionRange, float partialTick, boolean stopOnFluid) {
		var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		var instance = IShoulderSurfing.getInstance();
		if (instance.isShoulderSurfing() && this.level().isClientSide()) {
			var pickContext = new PickContext.Builder(camera)
				.withFluidContext(stopOnFluid ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE)
				.withEntity(this)
				.build();
			return instance.getObjectPicker().pickBlocks(pickContext, interactionRange, partialTick);
		}
		return super.pick(interactionRange, partialTick, stopOnFluid);
	}
}
