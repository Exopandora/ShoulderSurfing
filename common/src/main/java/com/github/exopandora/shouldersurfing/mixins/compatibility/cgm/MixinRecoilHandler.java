package com.github.exopandora.shouldersurfing.mixins.compatibility.cgm;

import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.mrcrayfish.guns.client.handler.RecoilHandler")
public class MixinRecoilHandler
{
	@Redirect
	(
		method = "onRenderTick",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/player/LocalPlayer.getXRot()F"
		)
	)
	private float getXRot(LocalPlayer player)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing())
		{
			return ShoulderRenderer.getInstance().getCameraXRot();
		}
		
		return player.getXRot();
	}
	
	@Redirect
	(
		method = "onRenderTick",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/player/LocalPlayer.setXRot(F)V"
		)
	)
	private void setXRot(LocalPlayer player, float xRot)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing())
		{
			ShoulderRenderer.getInstance().setCameraXRot(xRot);
		}
		else
		{
			player.setXRot(xRot);
		}
	}
}
