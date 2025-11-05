package com.github.exopandora.shouldersurfing.compat.mixins.createfly;

import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.zurrtum.create.catnip.data.Couple;
import com.zurrtum.create.content.contraptions.ContraptionHandlerClient;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContraptionHandlerClient.class)
public class MixinContraptionHandlerClient
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
			float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			double interactionRange = IObjectPicker.maxInteractionRange(player);
			var blockTrace = new PickContext.Builder(camera)
				.withEntity(player)
				.build()
				.blockTrace(interactionRange, partialTick);
			cir.setReturnValue(Couple.create(blockTrace.left(), blockTrace.right()));
		}
	}
}
