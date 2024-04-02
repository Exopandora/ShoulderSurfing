package com.teamderpy.shouldersurfing.mixins;

import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHelper.class)
public class MixinMouseHelper
{
	@Redirect
	(
		method = "turnPlayer",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/entity/player/ClientPlayerEntity.turn(DD)V"
		)
	)
	private void turn(ClientPlayerEntity player, double yRot, double xRot)
	{
		if(!ShoulderRenderer.getInstance().turn(player, yRot, xRot))
		{
			player.turn(yRot, xRot);
		}
	}
}
