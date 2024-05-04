package com.github.exopandora.shouldersurfing.fabric.mixins;

import com.github.exopandora.shouldersurfing.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.KeyHandler;
import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import com.github.exopandora.shouldersurfing.config.Config;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeModConfigEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraftforge.fml.config.ModConfig;
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
		ShoulderInstance.getInstance().init();
		ForgeModConfigEvents.reloading(ShoulderSurfing.MODID).register(config ->
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
