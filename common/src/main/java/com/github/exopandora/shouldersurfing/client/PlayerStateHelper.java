package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.callback.IAdaptiveItemCallback;
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
		final IPlayerStateCallback.IsUsingContext context = new IPlayerStateCallback.IsUsingContext(minecraft, cameraEntity);
		
		for(final IPlayerStateCallback callback : getPlayerStateCallbacks())
		{
			IPlayerStateCallback.Result result = callback.isUsingItem(context);
			
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
		final IPlayerStateCallback.IsInteractingContext context = new IPlayerStateCallback.IsInteractingContext(minecraft, cameraEntity);
		
		for(final IPlayerStateCallback callback : getPlayerStateCallbacks())
		{
			IPlayerStateCallback.Result result = callback.isInteracting(context);
			
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
	
	protected static boolean isAttacking(LivingEntity cameraEntity, Minecraft minecraft)
	{
		final IPlayerStateCallback.IsAttackingContext context = new IPlayerStateCallback.IsAttackingContext(minecraft, cameraEntity);
		
		for(final IPlayerStateCallback callback : getPlayerStateCallbacks())
		{
			IPlayerStateCallback.Result result = callback.isAttacking(context);
			
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
	
	protected static boolean isPicking(LivingEntity cameraEntity, Minecraft minecraft)
	{
		final IPlayerStateCallback.IsPickingContext context = new IPlayerStateCallback.IsPickingContext(minecraft, cameraEntity);
		
		for(final IPlayerStateCallback callback : getPlayerStateCallbacks())
		{
			IPlayerStateCallback.Result result = callback.isPicking(context);
			
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
		
		final IPlayerStateCallback.IsRidingBoatContext context = new IPlayerStateCallback.IsRidingBoatContext(minecraft, entity, vehicle);
		
		for(final IPlayerStateCallback callback : ShoulderSurfingRegistrar.getInstance().getPlayerStateCallbacks())
		{
			IPlayerStateCallback.Result result = callback.isRidingBoat(context);
			
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
			for(IAdaptiveItemCallback callback : ShoulderSurfingRegistrar.getInstance().getAdaptiveItemCallbacks())
			{
				if(callback.isHoldingAdaptiveItem(minecraft, living))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static List<IPlayerStateCallback> getPlayerStateCallbacks()
	{
		return ShoulderSurfingRegistrar.getInstance().getPlayerStateCallbacks();
	}
}
