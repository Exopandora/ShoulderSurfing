package com.github.exopandora.shouldersurfing.integration;

import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
public class ShoulderSurfingJadePlugin implements IWailaPlugin
{
	@Override
	public void registerClient(IWailaClientRegistration registration)
	{
		registration.addRayTraceCallback(new ShoulderSurfingRayTraceCallback(registration));
	}
	
	private static class ShoulderSurfingRayTraceCallback implements JadeRayTraceCallback
	{
		private final IWailaClientRegistration registration;
		
		public ShoulderSurfingRayTraceCallback(IWailaClientRegistration registration)
		{
			this.registration = registration;
		}
		
		@Override
		public @Nullable Accessor<?> onRayTrace(HitResult hitResult, @Nullable Accessor<?> accessor, @Nullable Accessor<?> originalAccessor)
		{
			ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
			Minecraft minecraft = Minecraft.getInstance();
			
			if(instance.isShoulderSurfing() && minecraft.player != null && minecraft.level != null)
			{
				Player player = minecraft.player;
				Camera camera = minecraft.gameRenderer.getMainCamera();
				ClipContext.Fluid fluidContext = IWailaConfig.get().general().getDisplayFluids().ctx;
				double interactionRangeOverride = IObjectPicker.maxInteractionRange(player) + IWailaConfig.get().general().getExtendedReach();
				float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
				PickContext pickContext = new PickContext.Builder(camera)
					.withFluidContext(fluidContext)
					.build();
				HitResult target = instance.getObjectPicker().pick(pickContext, interactionRangeOverride, partialTick, player);
				Level level = minecraft.level;
				
				if(Type.MISS.equals(target.getType()))
				{
					return null;
				}
				
				if(target instanceof BlockHitResult blockTarget)
				{
					BlockState state = level.getBlockState(blockTarget.getBlockPos());
					BlockEntity tileEntity = level.getBlockEntity(blockTarget.getBlockPos());
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
				}
				else if(target instanceof EntityHitResult entityTarget)
				{
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
