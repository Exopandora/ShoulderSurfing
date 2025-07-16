package com.github.exopandora.shouldersurfing.plugin.callbacks;

import com.github.exopandora.shouldersurfing.api.callback.ICameraEntityTransparencyCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITickableCallback;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class CameraEntityTransparencyCallbackWhenAiming implements ICameraEntityTransparencyCallback, ITickableCallback
{
	private static final int TRANSITION_TICK_COUNT = 5;
	private int aimingTicks;
	private int aimingTicksO;
	
	@Override
	public void tick()
	{
		if(Config.CLIENT.turnPlayerTransparentWhenAiming())
		{
			this.aimingTicksO = aimingTicks;
			
			if(ShoulderSurfingImpl.getInstance().isAiming())
			{
				if(this.aimingTicks < TRANSITION_TICK_COUNT)
				{
					this.aimingTicks++;
				}
			}
			else if(this.aimingTicks > 0)
			{
				this.aimingTicks--;
			}
		}
	}
	
	@Override
	public float getCameraEntityAlpha(IShoulderSurfing instance, Entity entity, float partialTick)
	{
		if(Config.CLIENT.turnPlayerTransparentWhenAiming())
		{
			float f = (TRANSITION_TICK_COUNT - Mth.lerp(partialTick, this.aimingTicksO, this.aimingTicks)) / TRANSITION_TICK_COUNT;
			return CameraEntityTransparencyCallback.MIN_CAMERA_ENTITY_ALPHA + (1F - CameraEntityTransparencyCallback.MIN_CAMERA_ENTITY_ALPHA) * f;
		}
		
		return 1.0F;
	}
}
