package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingRenderTypes;
import com.github.exopandora.shouldersurfing.config.Config;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderType.class)
public abstract class MixinRenderType extends RenderStateShard
{
	public MixinRenderType(String name, Runnable setupState, Runnable clearState)
	{
		super(name, setupState, clearState);
	}
	
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void clinit(CallbackInfo ci)
	{
		ShoulderSurfingRenderTypes.ARMOR_ENTITY_GLINT_ITEM_TARGET = RenderType.create(
			"armor_entity_glint_item_target",
			DefaultVertexFormat.POSITION_TEX,
			VertexFormat.Mode.QUADS,
			256,
			false,
			false,
			RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ENTITY, true, false))
				.setWriteMaskState(COLOR_WRITE)
				.setCullState(NO_CULL)
				.setDepthTestState(EQUAL_DEPTH_TEST)
				.setTransparencyState(GLINT_TRANSPARENCY)
				.setTexturingState(ENTITY_GLINT_TEXTURING)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setOutputState(OutputStateShard.ITEM_ENTITY_TARGET)
				.createCompositeState(false)
		);
		ShoulderSurfingRenderTypes.ARMOR_TRANSLUCENT_ITEM_TARGET = Util.memoize(texture ->
		{
			RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setOutputState(OutputStateShard.ITEM_ENTITY_TARGET)
				.createCompositeState(true);
			return RenderType.create("armor_translucent_item_target", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, state);
		});
		ShoulderSurfingRenderTypes.ARMOR_TRANSLUCENT = Util.memoize(texture ->
		{
			RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setDepthTestState(LEQUAL_DEPTH_TEST)
				.setOutputState(OutputStateShard.ITEM_ENTITY_TARGET)
				.createCompositeState(true);
			return RenderType.create("armor_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, state);
		});
		ShoulderSurfingRenderTypes.ENTITY_TRANSLUCENT_ITEM_TARGET = Util.memoize(texture ->
		{
			RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.createCompositeState(true);
			return RenderType.create("entity_translucent_item_target", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, state);
		});
	}
	
	@Inject
	(
		at = @At("HEAD"),
		method = "armorCutoutNoCull",
		cancellable = true
	)
	private static void armorCutoutNoCull(ResourceLocation identifier, CallbackInfoReturnable<RenderType> cir)
	{
		if(Config.CLIENT.isPlayerTransparencyEnabled())
		{
			if(Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FABULOUS)
			{
				cir.setReturnValue(ShoulderSurfingRenderTypes.armorTranslucentItemTarget(identifier));
			}
			else
			{
				cir.setReturnValue(ShoulderSurfingRenderTypes.armorTranslucent(identifier));
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
		if(Config.CLIENT.isPlayerTransparencyEnabled() && Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FABULOUS)
		{
			cir.setReturnValue(ShoulderSurfingRenderTypes.armorEntityGlintItemTarget());
		}
	}
}
