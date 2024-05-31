package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.mixinducks.AbstractClientPlayerDuck;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.CameraType;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends Player implements AbstractClientPlayerDuck
{
	@Unique
	private Vec3 deltaMovementOnPreviousTick = Vec3.ZERO;
	
	public MixinAbstractClientPlayer(Level level, BlockPos pos, float yRot, GameProfile gameProfile)
	{
		super(level, pos, yRot, gameProfile);
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
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING == Perspective.current();
	}
}
