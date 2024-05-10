package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderRenderer;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ClientPlayerEntity.class, priority = 500 /* apply before essential client */)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity
{
	public MixinClientPlayerEntity(ClientWorld level, GameProfile gameProfile)
	{
		super(level, gameProfile);
	}
	
	@Override
	public void turn(double yRot, double xRot)
	{
		if(!ShoulderRenderer.getInstance().turn(this, yRot, xRot))
		{
			super.turn(yRot, xRot);
		}
	}
}
