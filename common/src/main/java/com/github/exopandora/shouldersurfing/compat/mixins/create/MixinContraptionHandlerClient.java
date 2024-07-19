package com.github.exopandora.shouldersurfing.compat.mixins.create;

import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.Couple;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Pseudo
@Mixin(targets = "com.simibubi.create.content.contraptions.ContraptionHandlerClient")
public class MixinContraptionHandlerClient
{
	@ModifyArgs
	(
		method = "getRayInputs",
		at = @At
		(
			value = "INVOKE",
			target = "com/simibubi/create/foundation/utility/Couple.create(Ljava/lang/Object;Ljava/lang/Object;)Lcom/simibubi/create/foundation/utility/Couple;",
			remap = false
		),
		remap = false
	)
	private static void getRayInputs(Args args, LocalPlayer player)
	{
		if(ShoulderSurfing.getInstance().isShoulderSurfing())
		{
			Minecraft minecraft = Minecraft.getInstance();
			Camera camera = minecraft.gameRenderer.getMainCamera();
			float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(true);
			double interactionRange = IObjectPicker.maxInteractionRange(player);
			Couple<Vec3> blockTrace = new PickContext.Builder(camera)
				.build()
				.blockTrace(interactionRange, partialTick);
			args.set(0, blockTrace.left());
			args.set(1, blockTrace.right());
		}
	}
}
