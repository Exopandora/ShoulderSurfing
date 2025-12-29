package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
import com.github.exopandora.shouldersurfing.api.model.ViewBobbingMode;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.CameraType;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.spongepowered.asm.mixin.injection.At.Shift;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements GameRendererAccessor
{
	@ModifyVariable
	(
		method = "getFov",
		at = @At
		(
			value = "FIELD",
			target = "net/minecraft/client/renderer/GameRenderer.oldFovModifier:F",
			shift = Shift.BY,
			by = -3
		),
		ordinal = 1
	)
	private float getFov(float fov)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		IClientConfig config = instance.getClientConfig();
		
		if(instance.isShoulderSurfing() && config.isFovOverrideEnabled())
		{
			return config.getFovOverride();
		}
		
		return fov;
	}
	
	@Inject
	(
		method = "render",
		at = @At("HEAD")
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
	
	@Redirect
	(
		method = "renderLevel",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/OptionInstance.get()Ljava/lang/Object;"
		),
		slice = @Slice
		(
			from = @At
			(
				value = "INVOKE",
				target = "net/minecraft/client/Options.bobView()Lnet/minecraft/client/OptionInstance;"
			),
			to = @At
			(
				value = "INVOKE",
				target = "net/minecraft/client/renderer/GameRenderer.bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"
			)
		)
	)
	public Object bobView(OptionInstance<Boolean> instance)
	{
		if(ShoulderSurfingImpl.getInstance().isShoulderSurfing())
		{
			return switch(Config.CLIENT.getViewBobbingMode())
			{
				case INHERIT -> instance.get();
				case ON -> true;
				case OFF -> false;
			};
		}
		
		return instance.get();
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
