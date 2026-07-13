package com.github.exopandora.shouldersurfing.mixin;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(value = LocalPlayer.class, priority = 1500 /* apply after essential client, so turn method gets overwritten */)
abstract class LocalPlayerMixin extends AbstractClientPlayer {
	private LocalPlayerMixin(ClientLevel level, GameProfile gameProfile) {
		super(level, gameProfile);
	}
	
	@Redirect(
		method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;",
		at = @At(
			value = "INVOKE",
			target = "net/minecraft/world/entity/projectile/ProjectileUtil.getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"
		)
	)
	private static EntityHitResult getEntityHitResult(
		Entity shooter,
		Vec3 startPos,
		Vec3 endPos,
		AABB boundingBox,
		Predicate<Entity> filter,
		double interactionRangeSq
	) {
		var instance = IShoulderSurfing.getInstance();
		if (instance.isShoulderSurfing()) {
			var pickContext = new PickContext.Builder(Minecraft.getInstance().gameRenderer.mainCamera())
				.withEntity(shooter)
				.build();
			var interactionRange = Math.sqrt(interactionRangeSq);
			var partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
			return instance.getObjectPicker().pickEntities(pickContext, interactionRange, partialTick);
		}
		return ProjectileUtil.getEntityHitResult(shooter, startPos, endPos, boundingBox, filter, interactionRangeSq);
	}
	
	@Override
	public void turn(double yRot, double xRot) {
		if (!ShoulderSurfing.getInstance().getCamera().turn((LocalPlayer) (Object) this, yRot, xRot)) {
			super.turn(yRot, xRot);
		}
	}
}
