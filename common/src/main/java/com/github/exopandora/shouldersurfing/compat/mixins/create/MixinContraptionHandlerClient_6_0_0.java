package com.github.exopandora.shouldersurfing.compat.mixins.create;

import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContraptionHandlerClient.class)
public class MixinContraptionHandlerClient_6_0_0
{
	@Inject
	(
		method = "getRayInputs",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	private static void getRayInputs(LocalPlayer player, CallbackInfoReturnable<Couple<Vec3>> cir)
	{
		if(ShoulderSurfing.getInstance().isShoulderSurfing())
		{
			Minecraft minecraft = Minecraft.getInstance();
			Camera camera = minecraft.gameRenderer.getMainCamera();
			float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(true);
			double interactionRange = IObjectPicker.maxInteractionRange(player);
			var blockTrace = new PickContext.Builder(camera)
				.withEntity(player)
				.build()
				.blockTrace(interactionRange, partialTick);
			cir.setReturnValue(Couple.create(blockTrace.left(), blockTrace.right()));
		}
	}
}
