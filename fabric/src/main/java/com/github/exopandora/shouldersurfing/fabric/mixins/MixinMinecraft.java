package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.client.KeyHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
		ShoulderInstance.getInstance().init();
		ModConfigEvent.RELOADING.register(Config::onConfigReload);
	}
	
	@Inject
	(
		at = @At("HEAD"),
		method = "tick"
	)
	private void onStartTick(CallbackInfo info)
	{
		if(Minecraft.getInstance().level != null)
		{
			ShoulderInstance.getInstance().tick();
			ShoulderRenderer.getInstance().tick();
		}
	}
	
	@Inject
	(
		at = @At("HEAD"),
		method = "handleKeybinds"
	)
	private void handleKeybinds(CallbackInfo info)
	{
		KeyHandler.tick();
	}
}
