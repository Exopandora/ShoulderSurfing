package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(RenderTypes.class)
public class MixinRenderTypes
{
	@Shadow
	private static @Final Function<Identifier, RenderType> ARMOR_TRANSLUCENT;
	
	@Unique
	private static final RenderType ARMOR_ENTITY_GLINT_ITEM_ENTITY = RenderTypeAccessor.invokeCreate(
		"armor_entity_glint_item_entity",
		RenderSetup.builder(RenderPipelines.GLINT)
			.withTexture("Sampler0", ItemRenderer.ENCHANTED_GLINT_ARMOR)
			.setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
			.createRenderSetup()
	);
	
	@Unique
	private static final Function<Identifier, RenderType> ARMOR_ITEM_ENTITY = Util.memoize(texture ->
		{
			RenderSetup state = RenderSetup.builder(RenderPipelines.ARMOR_TRANSLUCENT)
				.withTexture("Sampler0", texture)
				.useLightmap()
				.useOverlay()
				.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
				.affectsCrumbling()
				.sortOnUpload()
				.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
				.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
				.createRenderSetup();
			return RenderTypeAccessor.invokeCreate("armor_item_entity", state);
		}
	);
	
	@Inject
	(
		at = @At("HEAD"),
		method = "armorCutoutNoCull",
		cancellable = true
	)
	private static void armorCutoutNoCull(Identifier identifier, CallbackInfoReturnable<RenderType> cir)
	{
		if(Config.CLIENT.isPlayerTransparencyEnabled())
		{
			if(Minecraft.getInstance().options.improvedTransparency().get())
			{
				cir.setReturnValue(ARMOR_ITEM_ENTITY.apply(identifier));
			}
			else
			{
				cir.setReturnValue(ARMOR_TRANSLUCENT.apply(identifier));
			}
		}
	}
	
	@Inject
	(
		at = @At("HEAD"),
		method = "armorEntityGlint",
		cancellable = true
	)
	private static void armorEntityGlint(CallbackInfoReturnable<RenderType> cir)
	{
		if(Config.CLIENT.isPlayerTransparencyEnabled() && Minecraft.getInstance().options.improvedTransparency().get())
		{
			cir.setReturnValue(ARMOR_ENTITY_GLINT_ITEM_ENTITY);
		}
	}
}
