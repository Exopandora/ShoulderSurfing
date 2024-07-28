package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ClientPlayerEntity.class, priority = 1500 /* apply after essential client, so turn method gets overwritten */)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity
{
	public MixinClientPlayerEntity(ClientWorld level, GameProfile gameProfile)
	{
		super(level, gameProfile);
	}
	
	@Override
	public void turn(double yRot, double xRot)
	{
		if(!ShoulderSurfingImpl.getInstance().getCamera().turn((ClientPlayerEntity) (Object) this, yRot, xRot))
		{
			super.turn(yRot, xRot);
		}
	}
}
