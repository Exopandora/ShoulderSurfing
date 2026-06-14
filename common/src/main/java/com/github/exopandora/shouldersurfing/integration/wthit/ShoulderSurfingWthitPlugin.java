package com.github.exopandora.shouldersurfing.integration.wthit;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickOrigin;
import com.github.exopandora.shouldersurfing.api.util.Couple;
import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRayCastVectorProvider;
import mcp.mobius.waila.api.IWailaClientPlugin;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("UnstableApiUsage")
public class ShoulderSurfingWthitPlugin implements IWailaClientPlugin {
	@Override
	public void register(IClientRegistrar registrar) {
		registrar.rayCastVector(new ShoulderSurfingObjectPicker());
	}
	
	private static class ShoulderSurfingObjectPicker implements IRayCastVectorProvider {
		@Override
		public boolean isEnabled(@NonNull IPluginConfig config) {
			return IShoulderSurfing.getInstance().isShoulderSurfing() && Minecraft.getInstance().player != null;
		}
		
		@Override
		public @NonNull Vec3 getOrigin(float delta) {
			Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
			boolean showFluid = PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_FLUID);
			PickContext pickContext = new PickContext.Builder(camera)
				.withBlockPickOrigin(PickOrigin.PLAYER)
				.withEntityPickOrigin(PickOrigin.PLAYER)
				.withFluidContext(showFluid ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE)
				.build();
			return pickContext.entityTrace(1.0F, delta).left();
		}
		
		@Override
		public @NonNull Vec3 getDirection(float delta) {
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
