package com.github.exopandora.shouldersurfing.integration.jade;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.world.phys.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.callback.JadeRayTraceCallback;
import snownee.jade.api.config.IWailaConfig;

@WailaPlugin
public class ShoulderSurfingJadePlugin implements IWailaPlugin {
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.addRayTraceCallback(new ShoulderSurfingRayTraceCallback(registration));
	}
	
	private static class ShoulderSurfingRayTraceCallback implements JadeRayTraceCallback {
		private final IWailaClientRegistration registration;
		
		public ShoulderSurfingRayTraceCallback(IWailaClientRegistration registration) {
			this.registration = registration;
		}
		
		@Override
		public @Nullable Accessor<?> onRayTrace(HitResult hitResult, @Nullable Accessor<?> accessor, @Nullable Accessor<?> originalAccessor) {
			var instance = IShoulderSurfing.getInstance();
			var minecraft = Minecraft.getInstance();
			if (instance.isShoulderSurfing() && minecraft.player != null && minecraft.level != null) {
				var player = minecraft.player;
				var camera = minecraft.gameRenderer.getMainCamera();
				var fluidContext = IWailaConfig.get().getGeneral().getDisplayFluids().ctx;
				var interactionRangeOverride = IObjectPicker.maxInteractionRange(player) + IWailaConfig.get().getGeneral().getExtendedReach();
				var partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(true);
				var pickContext = new PickContext.Builder(camera)
					.withFluidContext(fluidContext)
					.build();
				var target = instance.getObjectPicker().pick(pickContext, interactionRangeOverride, partialTick, player);
				var level = minecraft.level;
				if (target.getType() == Type.MISS) {
					return null;
				}
				if (target instanceof BlockHitResult blockTarget) {
					var state = level.getBlockState(blockTarget.getBlockPos());
					var tileEntity = level.getBlockEntity(blockTarget.getBlockPos());
					return this.registration.blockAccessor()
						.blockState(state)
						.blockEntity(tileEntity)
						.level(level)
						.player(player)
						.serverData(this.registration.getServerData())
						.serverConnected(this.registration.isServerConnected())
						.hit(blockTarget)
						.fakeBlock(this.registration.getBlockCamouflage(level, blockTarget.getBlockPos()))
						.build();
				} else if (target instanceof EntityHitResult entityTarget) {
					return this.registration.entityAccessor()
						.level(level)
						.player(player)
						.serverData(this.registration.getServerData())
						.serverConnected(this.registration.isServerConnected())
						.hit(entityTarget)
						.entity(entityTarget.getEntity())
						.build();
				}
			}
			return accessor;
		}
	}
}
