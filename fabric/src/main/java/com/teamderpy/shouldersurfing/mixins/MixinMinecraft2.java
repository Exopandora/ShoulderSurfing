package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;

import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;

@Mixin(Minecraft.class)
public class MixinMinecraft2
{
	@Inject
	(
		method = "<init>",
		at = @At("TAIL")
	)
	private void init(GameConfig gameConfig, CallbackInfo ci)
	{
		ShoulderInstance.getInstance().changePerspective(Config.CLIENT.getDefaultPerspective());
		ModConfigEvents.reloading(ShoulderSurfing.MODID).register(Config::onConfigReload);
	}
	
	@Inject
	(
		at = @At("HEAD"),
		method = "tick"
	)
	private void onStartTick(CallbackInfo info)
	{
		ShoulderInstance.getInstance().tick();
	}
}
