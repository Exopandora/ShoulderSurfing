package com.github.exopandora.shouldersurfing.compat.mixin.tslatentitystatus;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "net.tslat.tes.api.util.TESClientUtil")
class TESClientUtilMixin {
	@Redirect(
		method = "getClosestEntityPosition",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/world/entity/player/Player.getLookAngle()Lnet/minecraft/world/phys/Vec3;",
			remap = true
		),
		remap = false
	)
	private static Vec3 getLookAngle(Player player) {
		var instance = IShoulderSurfing.getInstance();
		if (instance.isShoulderSurfing()) {
			var realXRot = instance.getCamera().getXRot() * ((float) Math.PI / 180F);
			var realYRot = -instance.getCamera().getYRot() * ((float) Math.PI / 180F);
			var yCos = Mth.cos(realYRot);
			var ySin = Mth.sin(realYRot);
			var xCos = Mth.cos(realXRot);
			var xSin = Mth.sin(realXRot);
			return new Vec3(ySin * xCos, -xSin, yCos * xCos);
		}
		return player.getLookAngle();
	}
}
