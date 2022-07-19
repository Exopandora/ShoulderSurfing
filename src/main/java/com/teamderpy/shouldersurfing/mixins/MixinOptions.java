package com.teamderpy.shouldersurfing.mixins;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.teamderpy.shouldersurfing.client.ShoulderInstance;

import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(Options.class)
public abstract class MixinOptions
{
	@Shadow
	private CameraType cameraType;
	
	/**
	 * @author Exopandora
	 * @reason Disable shoulder surfing perspective when other mods change the perspective
	 */
	@Overwrite
	public void setCameraType(CameraType cameraType)
	{
		if(cameraType != this.cameraType)
		{
			ShoulderInstance.getInstance().setShoulderSurfing(false);
		}
		
		this.cameraType = cameraType;
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void setup(Minecraft minecraft, File file, CallbackInfo ci) {
		ShoulderSurfing.INSTANCE.setupPerspective(((Options) (Object) this));
	}
}
