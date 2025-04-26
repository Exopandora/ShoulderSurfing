package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.model.BeeStingerModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(BeeStingerModel.class)
public class MixinBeeStingerModel
{
	@ModifyArg
	(
		method = "<init>",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/model/Model.<init>(Lnet/minecraft/client/model/geom/ModelPart;Ljava/util/function/Function;)V"
		),
		index = 1
	)
	private static Function<ResourceLocation, RenderType> init(Function<ResourceLocation, RenderType> renderType)
	{
		return Config.CLIENT.isPlayerTransparencyEnabled() ? RenderType::armorTranslucent : renderType;
	}
}
