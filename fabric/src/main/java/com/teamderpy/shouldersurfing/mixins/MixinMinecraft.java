package com.teamderpy.shouldersurfing.mixins;

import com.teamderpy.shouldersurfing.client.ShoulderRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
	@Inject
	(
		method = "<init>",
		at = @At("TAIL")
	)
	private void init(GameConfiguration gameConfig, CallbackInfo ci)
	{
		ShoulderInstance.getInstance().changePerspective(Config.CLIENT.getDefaultPerspective());
		ModConfigEvent.RELOADING.register(Config::onConfigReload);
	}
	
	@Inject
	(
		at = @At("HEAD"),
		method = "tick"
	)
	private void onStartTick(CallbackInfo info)
	{
		ShoulderInstance.getInstance().tick();
		ShoulderRenderer.getInstance().tick();
	}
}
