package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.settings.PointOfView;

@Mixin(IngameGui.class)
public class MixinIngameGui
{
	@Redirect
	(
		method = "renderCrosshair",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/settings/PointOfView.isFirstPerson()Z"
		)
	)
	private boolean doRenderCrosshair(PointOfView pointOfView)
	{
		return Config.CLIENT.getCrosshairVisibility(Perspective.of(pointOfView, ShoulderSurfingHelper.doShoulderSurfing())).doRender();
	}
}
