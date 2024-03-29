package com.github.exopandora.shouldersurfing.compatibility;

import org.jetbrains.annotations.Nullable;

import com.github.exopandora.shouldersurfing.client.ShoulderHelper;
import com.github.exopandora.shouldersurfing.client.ShoulderInstance;
import com.github.exopandora.shouldersurfing.config.Config;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import snownee.jade.api.Accessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.callback.JadeRayTraceCallback;

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
			if(ShoulderInstance.getInstance().doShoulderSurfing())
			{
				Minecraft minecraft = Minecraft.getInstance();
				Camera camera = minecraft.gameRenderer.getMainCamera();
				
				MultiPlayerGameMode gameMode = minecraft.gameMode;
				ClipContext.Fluid fluidContext = registration.getConfig().getGeneral().getDisplayFluids().ctx;
				double maxDistance = gameMode.getPickRange() + registration.getConfig().getGeneral().getReachDistance();
				float partialTick = minecraft.getFrameTime();
				HitResult target = ShoulderHelper.traceBlocksAndEntities(camera, gameMode, maxDistance, fluidContext, partialTick, true, !Config.CLIENT.getCrosshairType().isDynamic());
				Player player = minecraft.player;
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
