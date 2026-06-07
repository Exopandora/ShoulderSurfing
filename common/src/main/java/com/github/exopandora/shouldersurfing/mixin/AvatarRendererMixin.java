package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingRenderTypes;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin extends LivingEntityRenderer<LivingEntity, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> {
	public AvatarRendererMixin(EntityRendererProvider.Context context, EntityModel<LivingEntityRenderState> model, float shadow) {
		super(context, model, shadow);
	}
	
	@Nullable
	protected RenderType getRenderType(@NonNull LivingEntityRenderState state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing) {
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		if (instance.getCameraEntityRenderer().isEntityTransparentPlayer(state)) {
			return ShoulderSurfingRenderTypes.entityTranslucentItemTarget(this.getTextureLocation(state));
		}
		return super.getRenderType(state, isBodyVisible, forceTransparent, appearGlowing);
	}
}
