package com.github.exopandora.shouldersurfing.plugin;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.callback.ICameraEntityTransparencyCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITickableCallback;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.compat.Mods;
import com.github.exopandora.shouldersurfing.compat.plugin.CreateModTargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ShoulderSurfingPlugin implements IShoulderSurfingPlugin
{
	@Override
	public void register(IShoulderSurfingRegistrar registrar)
	{
		registrar.registerAdaptiveItemCallback(ShoulderSurfingPlugin::isHoldingAdaptiveItem);
		registrar.registerCameraEntityTransparencyCallback(ShoulderSurfingPlugin::getCameraEntityAlpha);
		registrar.registerCameraEntityTransparencyCallback(new CameraEntityTransparencyCallbackWhenAiming());
		registerCompatibilityCallback(Mods.CREATE, () -> registrar.registerTargetCameraOffsetCallback(new CreateModTargetCameraOffsetCallback()));
	}
	
	private static void registerCompatibilityCallback(Mods mod, Runnable runnable)
	{
		if(mod.isLoaded())
		{
			String modName = StringUtils.capitalize(mod.name());
			
			try
			{
				ShoulderSurfingCommon.LOGGER.info("Registering compatibility callback for {}", modName);
				runnable.run();
			}
			catch(Throwable t)
			{
				ShoulderSurfingCommon.LOGGER.error("Failed to load compatibility callback for {}", modName, t);
			}
		}
	}
	
	private static boolean isHoldingAdaptiveItem(Minecraft minecraft, LivingEntity entity)
	{
		ItemStack useStack = entity.getUseItem();
		List<? extends String> useItems = Config.CLIENT.getAdaptiveCrosshairUseItems();
		List<? extends String> useItemProperties = Config.CLIENT.getAdaptiveCrosshairUseItemProperties();
		
		if(isAdaptiveItemStack(useStack, useItems, useItemProperties))
		{
			return true;
		}
		
		List<? extends String> holdItems = Config.CLIENT.getAdaptiveCrosshairHoldItems();
		List<? extends String> holdItemProperties = Config.CLIENT.getAdaptiveCrosshairHoldItemProperties();
		ItemStack[] handItems = {entity.getMainHandItem(), entity.getOffhandItem()};
		
		for(ItemStack handStack : handItems)
		{
			if(isAdaptiveItemStack(handStack, holdItems, holdItemProperties))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean isAdaptiveItemStack(ItemStack stack, List<? extends String> expressions, List<? extends String> itemProperties)
	{
		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		
		if(expressions.stream().map(ShoulderSurfingPlugin::expressionToMatchPredicate).anyMatch(pattern -> pattern.test(itemId)))
		{
			return true;
		}
		
		for(String itemProperty : itemProperties)
		{
			if(ItemProperties.getProperty(stack, ResourceLocation.parse(itemProperty)) != null)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static Predicate<String> expressionToMatchPredicate(String expression)
	{
		try
		{
			return Pattern.compile(expression).asMatchPredicate();
		}
		catch(Exception e)
		{
			return expression::equals;
		}
	}
	
	private static final float MIN_CAMERA_ENTITY_ALPHA = 0.15F;
	
	private static float getCameraEntityAlpha(IShoulderSurfing instance, Entity entity, float partialTick)
	{
		if(shouldRenderCameraEntityTransparent(instance, entity))
		{
			Vec3 renderOffset = instance.getCamera().getRenderOffset();
			float xAlpha = (float) Mth.clamp(Math.abs(renderOffset.x()) / (entity.getBbWidth() / 2.0D), 0, 1.0F);
			float yAlpha = 0;
			
			if(renderOffset.y() > 0)
			{
				yAlpha = (float) Mth.clamp(renderOffset.y() / (entity.getBbHeight() - entity.getEyeHeight()), 0, 1.0F);
			}
			else if(renderOffset.y() < 0)
			{
				yAlpha = (float) Mth.clamp(-renderOffset.y() / -entity.getEyeHeight(), 0, 1.0F);
			}
			
			return Mth.clamp((float) Math.sqrt(xAlpha * xAlpha + yAlpha * yAlpha), MIN_CAMERA_ENTITY_ALPHA, 1.0F);
		}
		
		return 1.0F;
	}
	
	private static boolean shouldRenderCameraEntityTransparent(IShoulderSurfing instance, Entity entity)
	{
		Vec3 renderOffset = instance.getCamera().getRenderOffset();
		return !entity.isSpectator() && (Math.abs(renderOffset.x()) < (entity.getBbWidth() / 2.0D) &&
			(renderOffset.y() >= 0 && renderOffset.y() < entity.getBbHeight() - entity.getEyeHeight() ||
				renderOffset.y() <= 0 && -renderOffset.y() < entity.getEyeHeight()));
	}
	
	private static class CameraEntityTransparencyCallbackWhenAiming implements ICameraEntityTransparencyCallback, ITickableCallback
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
				return MIN_CAMERA_ENTITY_ALPHA + (1F - MIN_CAMERA_ENTITY_ALPHA) * f;
			}
			
			return 1.0F;
		}
	}
}
