package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity, S extends EntityRenderState>
{
	@Inject
	(
		method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;",
		at = @At("TAIL"),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void createRenderState(T entity, float partialTick, CallbackInfoReturnable<S> ci, S renderState)
	{
		if(entity == Minecraft.getInstance().getCameraEntity())
		{
			ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().setCameraEntityRenderState(renderState);
		}
	}
}
