package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.client.SoundHelper;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISpawnWorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World
{
	@Shadow
	private @Final Minecraft minecraft;
	
	protected MixinClientWorld(ISpawnWorldInfo levelData, RegistryKey<World> dimension, DimensionType dimensionTypeRegistration, Supplier<IProfiler> profiler, boolean isClientSide, boolean isDebug, long seed)
	{
		super(levelData, dimension, dimensionTypeRegistration, profiler, isClientSide, isDebug, seed);
	}
	
	@Override
	@Overwrite
	public void playLocalSound(double x, double y, double z, SoundEvent soundEvent, SoundCategory soundSource, float volume, float pitch, boolean isDelayed)
	{
		PlayerEntity player = Minecraft.getInstance().player;
		
		if(player != null && ShoulderSurfingImpl.getInstance().isShoulderSurfing() && Config.CLIENT.doCenterPlayerSounds() && (x == player.getX() && y == player.getY() && z == player.getZ() || round(player.xo) == x && round(player.yo) == y && round(player.zo) == z))
		{
			Vector3d pos = SoundHelper.calcCameraCentricSoundPosition(player);
			x = pos.x();
			y = pos.y();
			z = pos.z();
		}
		
		double distanceSq = this.minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(x, y, z);
		SimpleSound simpleSound = new SimpleSound(soundEvent, soundSource, volume, pitch, x, y, z);
		
		if(isDelayed && distanceSq > 100.0D)
		{
			double distance = Math.sqrt(distanceSq) / 40.0D;
			this.minecraft.getSoundManager().playDelayed(simpleSound, (int) (distance * 20.0D));
		}
		else
		{
			this.minecraft.getSoundManager().play(simpleSound);
		}
	}
	
	@Unique
	private static double round(double d)
	{
		return ((int) (d * 8.0)) / 8.0F;
	}
}
