package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.mixinducks.AbstractClientPlayerEntityDuck;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity extends PlayerEntity implements AbstractClientPlayerEntityDuck
{
	@Unique
	private Vector3d deltaMovementOnPreviousTick = Vector3d.ZERO;
	
	public MixinAbstractClientPlayerEntity(World level, BlockPos pos, float yRot, GameProfile gameProfile)
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
	public Vector3d shouldersurfing$getDeltaMovementLerped(float partialTick)
	{
		Vector3d deltaMovement = this.getDeltaMovement();
		double x = MathHelper.lerp(partialTick, deltaMovementOnPreviousTick.x, deltaMovement.x);
		double y = MathHelper.lerp(partialTick, deltaMovementOnPreviousTick.y, deltaMovement.y);
		double z = MathHelper.lerp(partialTick, deltaMovementOnPreviousTick.z, deltaMovement.z);
		return new Vector3d(x, y, z);
	}
}
