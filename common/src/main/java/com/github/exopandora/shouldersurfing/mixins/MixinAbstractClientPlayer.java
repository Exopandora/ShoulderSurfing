package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.config.Perspective;
import com.github.exopandora.shouldersurfing.mixinducks.AbstractClientPlayerDuck;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.CameraType;
import net.minecraft.client.player.AbstractClientPlayer;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends Player implements AbstractClientPlayerDuck
{
	@Unique
	private Vec3 deltaMovementOnPreviousTick = Vec3.ZERO;
	
	public MixinAbstractClientPlayer(Level level, BlockPos pos, float yRot, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey)
	{
		super(level, pos, yRot, gameProfile, profilePublicKey);
	}
	
	@Override
	public void tick()
	{
		this.deltaMovementOnPreviousTick = this.getDeltaMovement();
		super.tick();
	}
	
	@Override
	public Vec3 shouldersurfing$getDeltaMovementLerped(float partialTick)
	{
		return this.deltaMovementOnPreviousTick.lerp(this.getDeltaMovement(), partialTick);
	}
	
	@Redirect
	(
		method = "getFieldOfViewModifier",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		)
	)
	private boolean isFirstPerson(CameraType cameraType)
	{
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING.equals(Perspective.current());
	}
}
