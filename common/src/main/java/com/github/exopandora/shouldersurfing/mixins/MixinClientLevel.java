package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.client.SoundHelper;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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

@Mixin(ClientLevel.class)
public abstract class MixinClientLevel extends Level
{
	protected MixinClientLevel(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, boolean isClientSide, boolean isDebug, long seed, int maxChainedNeighborUpdates)
	{
		super(levelData, dimension, registryAccess, dimensionTypeRegistration, isClientSide, isDebug, seed, maxChainedNeighborUpdates);
	}
	
	@Shadow
	abstract void playSound(double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, boolean isDelayed, long seed);
	
	@Inject
	(
		method = "playSeededSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/multiplayer/ClientLevel.playSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZJ)V",
			shift = Shift.BEFORE
		),
		cancellable = true
	)
	private void playSeededSound(@Nullable Entity entity, double x, double y, double z, Holder<SoundEvent> soundEvent, SoundSource soundSource, float volume, float pitch, long seed, CallbackInfo ci)
	{
		if(entity != null && entity == Minecraft.getInstance().player && ShoulderSurfingImpl.getInstance().isShoulderSurfing() && Config.CLIENT.doCenterPlayerSounds() && round(entity.xo) == x && round(entity.yo) == y && round(entity.zo) == z)
		{
			Vec3 pos = SoundHelper.calcCameraCentricSoundPosition(entity);
			this.playSound(pos.x(), pos.y(), pos.z(), soundEvent.value(), soundSource, volume, pitch, false, seed);
			ci.cancel();
		}
	}
	
	@Inject
	(
		method = "playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void playLocalSound(double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, boolean isDelayed, CallbackInfo ci)
	{
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
		
		if(cameraEntity != null && ShoulderSurfingImpl.getInstance().isShoulderSurfing() && Config.CLIENT.doCenterPlayerSounds() && cameraEntity.getX() == x && cameraEntity.getY() == y && cameraEntity.getZ() == z)
		{
			Vec3 pos = SoundHelper.calcCameraCentricSoundPosition(cameraEntity);
			this.playSound(pos.x(), pos.y(), pos.z(), soundEvent, soundSource, volume, pitch, isDelayed, this.random.nextLong());
			ci.cancel();
		}
	}
	
	@Unique
	private static double round(double d)
	{
		return ((int) (d * 8.0)) / 8.0F;
	}
}
