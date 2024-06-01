package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IngameGui.class)
public class MixinIngameGui
{
	@Shadow
	private @Final Minecraft minecraft;
	
	@Redirect
	(
		method = "renderCrosshair",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/settings/PointOfView.isFirstPerson()Z"
		)
	)
	private boolean doRenderCrosshair(PointOfView cameraType)
	{
		return Config.CLIENT.getCrosshairVisibility(Perspective.current()).doRender(this.minecraft.hitResult, ShoulderSurfingImpl.getInstance().isAiming());
	}
}
