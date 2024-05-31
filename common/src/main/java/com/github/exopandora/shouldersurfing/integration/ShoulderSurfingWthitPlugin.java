package com.github.exopandora.shouldersurfing.integration;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ObjectPicker;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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
		public HitResult pick(Minecraft minecraft, double maxDistance, float partialTick, IPluginConfig config)
		{
			ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
			
			if(instance.isShoulderSurfing() && minecraft.gameMode != null)
			{
				Camera camera = minecraft.gameRenderer.getMainCamera();
				PickContext pickContext = new PickContext.Builder(camera)
					.withFluidContext(config.getBoolean(WailaConstants.CONFIG_SHOW_FLUID) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE)
					.build();
				ObjectPicker objectPicker = instance.getObjectPicker();
				
				if(config.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY))
				{
					return objectPicker.pick(pickContext, maxDistance, partialTick, minecraft.gameMode);
				}
				
				return objectPicker.pickBlocks(pickContext, maxDistance, partialTick);
			}
			
			return mcp.mobius.waila.plugin.core.pick.ObjectPicker.INSTANCE.pick(minecraft, maxDistance, partialTick, config);
		}
	}
}
