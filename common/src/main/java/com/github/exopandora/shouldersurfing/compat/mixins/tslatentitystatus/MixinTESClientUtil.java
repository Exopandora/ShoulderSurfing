package com.github.exopandora.shouldersurfing.compat.mixins.tslatentitystatus;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "net.tslat.tes.api.util.TESClientUtil")
public class MixinTESClientUtil
{
	@Redirect
	(
		method = "getClosestEntityPosition",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/world/entity/player/Player.getLookAngle()Lnet/minecraft/world/phys/Vec3;",
			remap = true
		),
		remap = false
	)
	private static Vec3 getLookAngle(Player player)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing())
		{
			float realXRot = instance.getCamera().getXRot() * ((float) Math.PI / 180F);
			float realYRot = -instance.getCamera().getYRot() * ((float) Math.PI / 180F);
			float yCos = Mth.cos(realYRot);
			float ySin = Mth.sin(realYRot);
			float xCos = Mth.cos(realXRot);
			float xSin = Mth.sin(realXRot);
			return new Vec3(ySin * xCos, -xSin, yCos * xCos);
		}
		
		return player.getLookAngle();
	}
}
