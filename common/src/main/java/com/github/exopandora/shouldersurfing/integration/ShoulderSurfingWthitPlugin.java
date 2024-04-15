package com.github.exopandora.shouldersurfing.integration;

import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.client.ShoulderRayTracer;

import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.api.IPickerAccessor;
import mcp.mobius.waila.api.IPickerResults;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.core.pick.ObjectPicker;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;

public class ShoulderSurfingWthitPlugin implements IWailaPlugin
{
	@Override
	public void register(IRegistrar registrar)
	{
		registrar.replacePicker(new ShoulderSurfingObjectPicker());
	}
	
	private static class ShoulderSurfingObjectPicker implements IObjectPicker
	{
		@Override
		public void pick(IPickerAccessor accessor, IPickerResults results, IPluginConfig config)
		{
			Minecraft minecraft = accessor.getClient();
			Camera camera = minecraft.gameRenderer.getMainCamera();
			
			if(ShoulderInstance.getInstance().doShoulderSurfing())
			{
				MultiPlayerGameMode gameMode = minecraft.gameMode;
				ClipContext.Fluid fluidContext = config.getBoolean(WailaConstants.CONFIG_SHOW_FLUID) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE;
				boolean traceEntities = config.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY);
				boolean isCrosshairDynamic = ShoulderInstance.getInstance().isCrosshairDynamic(camera.getEntity());
				HitResult result = ShoulderRayTracer.traceBlocksAndEntities(camera, gameMode, accessor.getMaxDistance(), fluidContext, accessor.getFrameDelta(), traceEntities, !isCrosshairDynamic);
				results.add(result, 0);
			}
			else
			{
				ObjectPicker.INSTANCE.pick(accessor, results, config);
			}
		}
	}
}
