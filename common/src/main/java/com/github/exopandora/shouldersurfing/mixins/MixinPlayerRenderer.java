package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer extends LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>
{
	public MixinPlayerRenderer(EntityRendererProvider.Context context, EntityModel<LivingEntity> model, float shadow)
	{
		super(context, model, shadow);
	}
	
	@Nullable
	protected RenderType getRenderType(LivingEntity entity, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		boolean forceTransparentOverride = instance.isShoulderSurfing() && entity == Minecraft.getInstance().player;
		return super.getRenderType(entity, isBodyVisible, forceTransparentOverride, appearGlowing);
	}
}
