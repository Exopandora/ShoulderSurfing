package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.callback.IPlayerStateCallback;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

import java.util.List;

class PlayerStateHelper
{
	protected static boolean isUsingItem(LivingEntity cameraEntity, Minecraft minecraft)
	{
		for(final IPlayerStateCallback callback : getPlayerStateCallbacks())
		{
			IPlayerStateCallback.Result result = callback.isUsingItem(new IPlayerStateCallback.IsUsingContext(minecraft, cameraEntity));
			
			if(result == IPlayerStateCallback.Result.TRUE)
			{
				return true;
			}
			else if(result == IPlayerStateCallback.Result.FALSE)
			{
				return false;
			}
		}
		
		return cameraEntity.isUsingItem() && !cameraEntity.getUseItem().has(DataComponents.FOOD) || cameraEntity instanceof Player player && player.isScoping();
	}
	
	protected static boolean isInteracting(LivingEntity cameraEntity, Minecraft minecraft)
	{
		for(final IPlayerStateCallback callback : getPlayerStateCallbacks())
		{
			IPlayerStateCallback.Result result = callback.isInteracting(new IPlayerStateCallback.IsInteractingContext(minecraft, cameraEntity));
			
			if(result == IPlayerStateCallback.Result.TRUE)
			{
				return true;
			}
			else if(result == IPlayerStateCallback.Result.FALSE)
			{
				return false;
			}
		}
		
		return minecraft.options.keyUse.isDown() && !cameraEntity.isUsingItem();
	}
	
	protected static boolean isAttacking(Minecraft minecraft)
	{
		for(final IPlayerStateCallback callback : getPlayerStateCallbacks())
		{
			IPlayerStateCallback.Result result = callback.isAttacking(new IPlayerStateCallback.IsAttackingContext(minecraft));
			
			if(result == IPlayerStateCallback.Result.TRUE)
			{
				return true;
			}
			else if(result == IPlayerStateCallback.Result.FALSE)
			{
				return false;
			}
		}
		
		return minecraft.options.keyAttack.isDown();
	}
	
	protected static boolean isPicking(Minecraft minecraft)
	{
		for(final IPlayerStateCallback callback : getPlayerStateCallbacks())
		{
			IPlayerStateCallback.Result result = callback.isPicking(new IPlayerStateCallback.IsPickingContext(minecraft));
			
			if(result == IPlayerStateCallback.Result.TRUE)
			{
				return true;
			}
			else if(result == IPlayerStateCallback.Result.FALSE)
			{
				return false;
			}
		}
		
		return minecraft.options.keyPickItem.isDown();
	}
	
	protected static boolean isRidingBoat(Minecraft minecraft, Entity entity)
	{
		if(!(entity instanceof LivingEntity))
		{
			return false;
		}
		
		Entity vehicle = entity.getVehicle();
		
		if(vehicle == null)
		{
			return false;
		}
		
		for(final IPlayerStateCallback callback : ShoulderSurfingRegistrar.getInstance().getPlayerStateCallbacks())
		{
			IPlayerStateCallback.Result result = callback.isRidingBoat(new IPlayerStateCallback.IsRidingBoatContext(minecraft, entity, vehicle));
			
			if(result == IPlayerStateCallback.Result.TRUE)
			{
				return true;
			}
			else if(result == IPlayerStateCallback.Result.FALSE)
			{
				return false;
			}
		}
		
		return vehicle instanceof AbstractBoat;
	}
	
	protected static boolean isHoldingAdaptiveItem(Minecraft minecraft, Entity entity)
	{
		if(entity instanceof LivingEntity living)
		{
			return ShoulderSurfingRegistrar.getInstance().getAdaptiveItemCallbacks().stream().anyMatch(callback -> callback.isHoldingAdaptiveItem(minecraft, living));
		}
		
		return false;
	}
	
	private static List<IPlayerStateCallback> getPlayerStateCallbacks()
	{
		return ShoulderSurfingRegistrar.getInstance().getPlayerStateCallbacks();
	}
}
