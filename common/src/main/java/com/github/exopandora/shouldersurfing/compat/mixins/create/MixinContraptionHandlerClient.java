package com.github.exopandora.shouldersurfing.compat.mixins.create;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.Couple;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.vector.Vector3d;
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
	private static void getRayInputs(Args args, ClientPlayerEntity player)
	{
		if(ShoulderSurfing.getInstance().isShoulderSurfing())
		{
			Minecraft minecraft = Minecraft.getInstance();
			ActiveRenderInfo camera = minecraft.gameRenderer.getMainCamera();
			float partialTick = minecraft.getDeltaFrameTime();
			double interactionRange = minecraft.gameMode.getPickRange();
			Couple<Vector3d> blockTrace = new PickContext.Builder(camera)
				.build()
				.blockTrace(interactionRange, partialTick);
			args.set(0, blockTrace.left());
			args.set(1, blockTrace.right());
		}
	}
}
