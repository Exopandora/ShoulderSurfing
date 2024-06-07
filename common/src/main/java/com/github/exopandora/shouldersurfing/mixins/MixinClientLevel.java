package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.client.SoundHelper;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevel extends Level
{
	protected MixinClientLevel(WritableLevelData levelData, ResourceKey<Level> dimension, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long seed, int maxChainedNeighborUpdates)
	{
		super(levelData, dimension, dimensionTypeRegistration, profiler, isClientSide, isDebug, seed, maxChainedNeighborUpdates);
	}
	
	@Shadow
	abstract void playSound(double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, boolean isGlobal, long seed);
	
	@Inject
	(
		method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFJ)V",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/multiplayer/ClientLevel.playSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZJ)V",
			shift = Shift.BEFORE
		),
		cancellable = true
	)
	private void playSeededSound(@Nullable Player player, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, long seed, CallbackInfo ci)
	{
		if(player != null && ShoulderSurfingImpl.getInstance().isShoulderSurfing() && Config.CLIENT.doCenterPlayerSounds() && round(player.xo) == x && round(player.yo) == y && round(player.zo) == z)
		{
			Vec3 pos = SoundHelper.calcCameraCentricSoundPosition(player);
			this.playSound(pos.x(), pos.y(), pos.z(), soundEvent, soundSource, volume, pitch, false, seed);
			ci.cancel();
		}
	}
	
	@Inject
	(
		method = "playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void playLocalSound(double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, boolean isGlobal, CallbackInfo ci)
	{
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		
		if(cameraEntity != null && ShoulderSurfingImpl.getInstance().isShoulderSurfing() && Config.CLIENT.doCenterPlayerSounds() && cameraEntity.getX() == x && cameraEntity.getY() == y && cameraEntity.getZ() == z)
		{
			Vec3 pos = SoundHelper.calcCameraCentricSoundPosition(cameraEntity);
			this.playSound(pos.x(), pos.y(), pos.z(), soundEvent, soundSource, volume, pitch, isGlobal, this.random.nextLong());
			ci.cancel();
		}
	}
	
	@Unique
	private static double round(double d)
	{
		return ((int) (d * 8.0)) / 8.0F;
	}
}
