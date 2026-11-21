package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.renderer.rendertype.ShoulderSurfingRenderTypes;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EquipmentLayerRenderer.class)
class EquipmentLayerRendererMixin {
	@Unique
	private static final String RENDER_LAYERS = "renderLayers("
		+ "Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;"
		+ "Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;"
		+ "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;"
		+ "ILnet/minecraft/resources/Identifier;II)V";
	
	@Redirect(
		method = RENDER_LAYERS,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;armorCutoutNoCull(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"
		)
	)
	private RenderType armorCutoutNoCull(Identifier texture) {
		return useTranslucentArmor()
			? ShoulderSurfingRenderTypes.armorTranslucentNoCull(texture)
			: RenderTypes.armorCutoutNoCull(texture);
	}
	
	@Redirect(
		method = RENDER_LAYERS,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;armorCutoutNoCullGlint(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"
		)
	)
	private RenderType armorCutoutNoCullGlint(Identifier texture) {
		return useTranslucentArmor()
			? ShoulderSurfingRenderTypes.armorTranslucentNoCullGlint(texture)
			: RenderTypes.armorCutoutNoCullGlint(texture);
	}
	
	@Redirect(
		method = RENDER_LAYERS,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;armorTrim(Lnet/minecraft/resources/Identifier;Z)Lnet/minecraft/client/renderer/rendertype/RenderType;"
		)
	)
	private RenderType armorTrim(Identifier texture, boolean decal) {
		return useTranslucentArmor()
			? ShoulderSurfingRenderTypes.armorTrimTranslucent(texture, decal)
			: RenderTypes.armorTrim(texture, decal);
	}
	
	@Redirect(
		method = RENDER_LAYERS,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;trimmedArmorGlint()Lnet/minecraft/client/renderer/rendertype/RenderType;"
		)
	)
	private RenderType trimmedArmorGlint() {
		return useTranslucentArmor()
			? ShoulderSurfingRenderTypes.trimmedArmorGlint()
			: RenderTypes.trimmedArmorGlint();
	}
	
	@Unique
	private static boolean useTranslucentArmor() {
		var renderer = ShoulderSurfing.getInstance().getCameraEntityRenderer();
		return renderer.isRenderingCameraEntity() && renderer.getCameraEntityAlphaAsInt() < 255;
	}
}
