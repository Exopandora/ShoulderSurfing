package com.github.exopandora.shouldersurfing.integration;

import com.github.exopandora.shouldersurfing.client.ShoulderHelper;
import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.config.Config;
import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;

public abstract class ShoulderSurfingWthitPlugin implements IWailaPlugin
{
	@Override
	public void register(IRegistrar registrar)
	{
		registrar.replacePicker(new ShoulderSurfingObjectPicker(this.defaultObjectPickerInstance()));
	}
	
	protected abstract IObjectPicker defaultObjectPickerInstance();
	
	private static class ShoulderSurfingObjectPicker implements IObjectPicker
	{
		private IObjectPicker defaultObjectPicker;
		
		public ShoulderSurfingObjectPicker(IObjectPicker defaultObjectPicker)
		{
			this.defaultObjectPicker = defaultObjectPicker;
		}
		
		@Override
		public HitResult pick(Minecraft mc, double maxDistance, float partialTick, IPluginConfig config)
		{
			if(ShoulderInstance.getInstance().doShoulderSurfing())
			{
				Camera camera = mc.gameRenderer.getMainCamera();
				MultiPlayerGameMode gameMode = mc.gameMode;
				ClipContext.Fluid fluidContext = config.getBoolean(WailaConstants.CONFIG_SHOW_FLUID) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE;
				boolean traceEntities = config.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY);
				boolean isCrosshairDynamic = ShoulderInstance.getInstance().isCrosshairDynamic(camera.getEntity());
				return ShoulderHelper.traceBlocksAndEntities(camera, gameMode, maxDistance, fluidContext, partialTick, traceEntities, !isCrosshairDynamic);
			}
			
			return this.defaultObjectPicker.pick(mc, maxDistance, partialTick, config);
		}
	}
}
