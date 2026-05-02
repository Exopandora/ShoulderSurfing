package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingRenderTypes;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
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
		LocalPlayer player = Minecraft.getInstance().player;
		
		if(!entity.isInvisibleTo(player) && instance.isShoulderSurfing() && Config.CLIENT.isPlayerTransparencyEnabled() && entity == player)
		{
			return ShoulderSurfingRenderTypes.entityTranslucentItemTarget(this.getTextureLocation(entity));
		}
		
		return super.getRenderType(entity, isBodyVisible, forceTransparent, appearGlowing);
	}
}
