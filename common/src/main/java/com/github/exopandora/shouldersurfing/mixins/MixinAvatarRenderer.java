package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AvatarRenderer.class)
public abstract class MixinAvatarRenderer extends LivingEntityRenderer<LivingEntity, LivingEntityRenderState, EntityModel<LivingEntityRenderState>>
{
	public MixinAvatarRenderer(EntityRendererProvider.Context context, EntityModel<LivingEntityRenderState> model, float shadow)
	{
		super(context, model, shadow);
	}
	
	@Nullable
	protected RenderType getRenderType(LivingEntityRenderState state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		boolean forceTransparentOverride = instance.isShoulderSurfing() && state == instance.getCameraEntityRenderer().getCameraEntityRenderState();
		return super.getRenderType(state, isBodyVisible, forceTransparentOverride, appearGlowing);
	}
}
