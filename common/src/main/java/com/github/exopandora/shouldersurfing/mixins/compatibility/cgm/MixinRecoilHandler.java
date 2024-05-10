package com.github.exopandora.shouldersurfing.mixins.compatibility.cgm;

import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.mrcrayfish.guns.client.handler.RecoilHandler")
public class MixinRecoilHandler
{
	@Unique
	private float xRot;
	
	@Inject
	(
		method = "onRenderTick",
		at = @At
		(
			value = "RETURN",
			ordinal = 2,
			shift = Shift.AFTER
		)
	)
	private void preOnRenderTick(CallbackInfo ci)
	{
		this.xRot = Minecraft.getInstance().player.xRot;
	}
	
	@Inject
	(
		method = "onRenderTick",
		at = @At("TAIL")
	)
	private void postOnRenderTick(CallbackInfo ci)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing())
		{
			ClientPlayerEntity player = Minecraft.getInstance().player;
			float delta = player.xRot - this.xRot;
			ShoulderRenderer.getInstance().setCameraXRot(ShoulderRenderer.getInstance().getCameraXRot() + delta);
			player.xRot = this.xRot;
		}
	}
}
