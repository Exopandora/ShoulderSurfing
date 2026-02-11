package com.github.exopandora.shouldersurfing.plugin.callbacks;

import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;

public class CameraCouplingCallback implements ICameraCouplingCallback
{
	@Override
	public boolean isForcingCameraCoupling(Minecraft minecraft)
	{
		return minecraft.player != null && minecraft.player.isPassenger() && minecraft.player.getVehicle() instanceof AbstractMinecart;
	}
}
