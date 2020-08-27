package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.event.ClientEventHandler;

import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.settings.PointOfView;

@Mixin(IngameGui.class)
public class MixinIngameGui
{
	@Redirect
	(
		method = "func_238456_d_",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/settings/PointOfView.func_243192_a()Z"
		)
	)
	private boolean doRenderCrosshair(PointOfView view)
	{
		return Config.CLIENT.getCrosshairVisibility(Perspective.of(view, ClientEventHandler.doShoulderSurfing())).doRender();
	}
}
