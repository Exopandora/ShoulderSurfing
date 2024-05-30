package com.github.exopandora.shouldersurfing.integration;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ObjectPicker;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.api.IPickerAccessor;
import mcp.mobius.waila.api.IPickerResults;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;

@SuppressWarnings("deprecation")
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
			ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
			
			if(instance.isShoulderSurfing() && minecraft.player != null)
			{
				PickContext pickContext = new PickContext.Builder(camera)
					.withFluidContext(config.getBoolean(WailaConstants.CONFIG_SHOW_FLUID) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE)
					.build();
				ObjectPicker objectPicker = instance.getObjectPicker();
				HitResult hitResult;
				
				if(config.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY))
				{
					hitResult = objectPicker.pick(pickContext, accessor.getMaxDistance(), accessor.getFrameDelta(), minecraft.player);
				}
				else
				{
					hitResult = objectPicker.pickBlocks(pickContext, accessor.getMaxDistance(), accessor.getFrameDelta());
				}
				
				results.add(hitResult, 0);
			}
			else
			{
				mcp.mobius.waila.plugin.core.pick.ObjectPicker.INSTANCE.pick(accessor, results, config);
			}
		}
	}
}
