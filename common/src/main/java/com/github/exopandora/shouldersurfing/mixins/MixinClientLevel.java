package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.client.SoundHelper;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevel extends Level
{
	@Shadow
	private @Final Minecraft minecraft;
	
	protected MixinClientLevel(WritableLevelData levelData, ResourceKey<Level> dimension, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long seed)
	{
		super(levelData, dimension, dimensionTypeRegistration, profiler, isClientSide, isDebug, seed);
	}
	
	@Override
	@Overwrite
	public void playLocalSound(double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, boolean isDelayed)
	{
		Player player = Minecraft.getInstance().player;
		
		if(player != null && ShoulderSurfingImpl.getInstance().isShoulderSurfing() && Config.CLIENT.doCenterPlayerSounds() && (x == player.getX() && y == player.getY() && z == player.getZ() || round(player.xo) == x && round(player.yo) == y && round(player.zo) == z))
		{
			Vec3 pos = SoundHelper.calcCameraCentricSoundPosition(player);
			x = pos.x();
			y = pos.y();
			z = pos.z();
		}
		
		double distanceSq = this.minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(x, y, z);
		SimpleSoundInstance simplesoundinstance = new SimpleSoundInstance(soundEvent, soundSource, volume, pitch, x, y, z);
		
		if(isDelayed && distanceSq > 100.0D)
		{
			double distance = Math.sqrt(distanceSq) / 40.0D;
			this.minecraft.getSoundManager().playDelayed(simplesoundinstance, (int) (distance * 20.0D));
		}
		else
		{
			this.minecraft.getSoundManager().play(simplesoundinstance);
		}
	}
	
	@Unique
	private static double round(double d)
	{
		return ((int) (d * 8.0)) / 8.0F;
	}
}
