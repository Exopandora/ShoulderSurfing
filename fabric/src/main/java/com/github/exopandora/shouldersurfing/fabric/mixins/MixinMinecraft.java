package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.neoforged.fml.config.ModConfig;
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
	private void init(GameConfig gameConfig, CallbackInfo ci)
	{
		ShoulderSurfingImpl.getInstance().init();
		ModConfigEvents.reloading(ShoulderSurfingCommon.MOD_ID).register(config ->
		{
			if(ModConfig.Type.CLIENT == config.getType())
			{
				Config.onConfigReload();
			}
		});
	}
	
	@Inject
	(
		at = @At("HEAD"),
		method = "tick"
	)
	private void onStartTick(CallbackInfo info)
	{
		if(Minecraft.getInstance().level != null && !Minecraft.getInstance().isPaused())
		{
			ShoulderSurfingImpl.getInstance().tick();
		}
	}
}
