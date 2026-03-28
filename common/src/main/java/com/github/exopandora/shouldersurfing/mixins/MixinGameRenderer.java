package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.ViewBobbingMode;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.CameraType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.OptionsRenderState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements GameRendererAccessor
{
	@Shadow
	private @Final GameRenderState gameRenderState;
	
	@Inject
	(
		method = "render",
		at = @At("TAIL")
	)
	public void render(CallbackInfo ci)
	{
		ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().setCameraEntityRenderState(null);
	}
	
	@Redirect
	(
		method = "renderLevel",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		),
		slice = @Slice
		(
			from = @At
			(
				value = "FIELD",
				target = "net/minecraft/client/gui/components/debug/DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR:Lnet/minecraft/resources/Identifier;",
				opcode = Opcodes.GETSTATIC
			)
		)
	)
	private boolean doRenderCrosshair(CameraType cameraType)
	{
		return ShoulderSurfingImpl.getInstance().getCrosshairRenderer().doRenderCrosshair();
	}
	
	@Inject
	(
		method = "extractOptions",
		at = @At("TAIL")
	)
	public void extractOptions(CallbackInfo ci)
	{
		if(ShoulderSurfingImpl.getInstance().isShoulderSurfing())
		{
			OptionsRenderState optionsRenderState = this.gameRenderState.optionsRenderState;
			optionsRenderState.bobView = switch(Config.CLIENT.getViewBobbingMode())
			{
				case INHERIT -> optionsRenderState.bobView;
				case ON -> true;
				case OFF -> false;
			};
		}
	}
	
	@Inject
	(
		method = "bobView",
		at = @At("HEAD"),
		cancellable = true
	)
	public void bobView(CallbackInfo ci)
	{
		if(ShoulderSurfingImpl.getInstance().isShoulderSurfing() && Config.CLIENT.getViewBobbingMode() == ViewBobbingMode.OFF)
		{
			ci.cancel();
		}
	}
}
