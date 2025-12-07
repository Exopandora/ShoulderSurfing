package com.github.exopandora.shouldersurfing.compat.plugin;

import com.cobblemon.mod.common.OrientationControllable;
import com.cobblemon.mod.common.api.orientation.OrientationController;
import com.github.exopandora.shouldersurfing.api.callback.ICameraRotationSetupCallback;
import com.github.exopandora.shouldersurfing.compat.CobblemonCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class CobblemonCameraRotationSetupCallback implements ICameraRotationSetupCallback
{
	@Override
	public void pre(CameraRotationSetupContext context, CameraRotationSetupResult result)
	{
		if(context.player().getVehicle() instanceof OrientationControllable controllableVehicle)
		{
			OrientationController vehicleController = controllableVehicle.getOrientationController();
			
			if(vehicleController.isActive())
			{
				result.setXRot(vehicleController.getPitch());
				result.setYRot(vehicleController.getYaw());
			}
		}
	}
	
	@Override
	public void post(CameraRotationSetupContext context, CameraRotationSetupResult result)
	{
		if(CobblemonCompat.hasActiveBoatBehaviour(context.player().getVehicle()))
		{
			Entity vehicle = context.player().getVehicle();
			float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
			float yRotLerped = Mth.rotLerp(partialTick, vehicle.yRotO, vehicle.getYRot());
			float delta = Mth.wrapDegrees(result.getYRot() - yRotLerped);
			float clamped = Mth.clamp(delta, -105.0F, 105.0F);
			result.setYRot(result.getYRot() + clamped - delta);
		}
	}
}
