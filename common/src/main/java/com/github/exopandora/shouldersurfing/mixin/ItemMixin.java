package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
class ItemMixin {
	@Inject(
		method = "getPlayerPOVHitResult",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void getPlayerPOVHitResult(
		Level level,
		Player player,
		ClipContext.Fluid fluid,
		CallbackInfoReturnable<BlockHitResult> cir
	) {
		var instance = IShoulderSurfing.getInstance();
		if (instance.isShoulderSurfing() && player == Minecraft.getInstance().player && player.level().isClientSide()) {
			var minecraft = Minecraft.getInstance();
			var camera = minecraft.gameRenderer.getMainCamera();
			var pickContext = new PickContext.Builder(camera)
				.withFluidContext(fluid)
				.withEntity(player)
				.build();
			var interactionRange = player.blockInteractionRange();
			var result = instance.getObjectPicker().pickBlocks(pickContext, interactionRange, 1.0F);
			ShoulderSurfing.getInstance().lookAtPosition(result.getLocation());
			cir.setReturnValue(result);
		}
	}
}
