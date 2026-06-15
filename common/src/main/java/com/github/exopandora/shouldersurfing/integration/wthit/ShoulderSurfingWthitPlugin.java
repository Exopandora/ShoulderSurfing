package com.github.exopandora.shouldersurfing.integration.wthit;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.world.phys.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
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

public class ShoulderSurfingWthitPlugin implements IWailaPlugin {
	@Override
	public void register(IRegistrar registrar) {
		registrar.replacePicker(new ShoulderSurfingObjectPicker());
	}
	
	private static class ShoulderSurfingObjectPicker implements mcp.mobius.waila.api.IObjectPicker {
		public void pick(IPickerAccessor accessor, IPickerResults results, IPluginConfig config) {
			Minecraft minecraft = accessor.getClient();
			Camera camera = minecraft.gameRenderer.getMainCamera();
			IShoulderSurfing instance = IShoulderSurfing.getInstance();
			if (instance.isShoulderSurfing() && minecraft.gameMode != null) {
				PickContext pickContext = new PickContext.Builder(camera)
					.withFluidContext(config.getBoolean(WailaConstants.CONFIG_SHOW_FLUID) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE)
					.build();
				IObjectPicker objectPicker = instance.getObjectPicker();
				HitResult hitResult;
				if (config.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY)) {
					hitResult = objectPicker.pick(pickContext, accessor.getMaxDistance(), accessor.getFrameDelta(), minecraft.gameMode);
				} else {
					hitResult = objectPicker.pickBlocks(pickContext, accessor.getMaxDistance(), accessor.getFrameDelta());
				}
				results.add(hitResult, 0);
			} else {
				mcp.mobius.waila.plugin.core.pick.ObjectPicker.INSTANCE.pick(accessor, results, config);
			}
		}
	}
}
