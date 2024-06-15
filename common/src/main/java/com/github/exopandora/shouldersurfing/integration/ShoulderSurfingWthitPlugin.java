package com.github.exopandora.shouldersurfing.integration;

import com.github.exopandora.shouldersurfing.api.model.Couple;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRayCastVectorProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("UnstableApiUsage")
public class ShoulderSurfingWthitPlugin implements IWailaPlugin
{
	@Override
	public void register(IRegistrar registrar)
	{
		registrar.addRayCastVector(new ShoulderSurfingObjectPicker());
	}
	
	private static class ShoulderSurfingObjectPicker implements IRayCastVectorProvider
	{
		@Override
		public boolean isEnabled(IPluginConfig config)
		{
			return ShoulderSurfingImpl.getInstance().isShoulderSurfing() && Minecraft.getInstance().player != null;
		}
		
		@Override
		public Vec3 getOrigin(float delta)
		{
			Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
			boolean showFluid = PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_FLUID);
			PickContext pickContext = new PickContext.Builder(camera)
				.withFluidContext(showFluid ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE)
				.build();
			return pickContext.entityTrace(1.0F, delta).left();
		}
		
		@Override
		public Vec3 getDirection(float delta)
		{
			Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
			boolean showFluid = PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_FLUID);
			PickContext pickContext = new PickContext.Builder(camera)
				.withFluidContext(showFluid ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE)
				.build();
			Couple<Vec3> blockTrace = pickContext.entityTrace(1.0F, delta);
			return blockTrace.right().subtract(blockTrace.left()).normalize();
		}
	}
}
