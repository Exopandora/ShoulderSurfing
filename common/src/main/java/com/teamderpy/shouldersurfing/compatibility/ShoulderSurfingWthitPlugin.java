package com.teamderpy.shouldersurfing.compatibility;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;

import mcp.mobius.waila.api.IObjectPicker;
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
		public HitResult pick(Minecraft mc, double maxDistance, float partialTick, IPluginConfig config)
		{
			if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
			{
				Camera camera = mc.gameRenderer.getMainCamera();
				MultiPlayerGameMode gameMode = mc.gameMode;
				ClipContext.Fluid fluidContext = config.getBoolean(WailaConstants.CONFIG_SHOW_FLUID) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE;
				boolean traceEntities = config.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY);
				return ShoulderHelper.traceBlocksAndEntities(camera, gameMode, maxDistance, fluidContext, partialTick, traceEntities, true);
			}
			
			return ObjectPicker.INSTANCE.pick(mc, maxDistance, partialTick, config);
		}
	}
}
