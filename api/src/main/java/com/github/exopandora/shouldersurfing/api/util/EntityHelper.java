package com.github.exopandora.shouldersurfing.api.util;

import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class EntityHelper {
	public static void lookAtTarget(LocalPlayer player, Vec3 target) {
		var yHeadRot = player.yHeadRot;
		var yHeadRotO = player.yHeadRotO;
		var yBodyRot = player.yBodyRot;
		var yBodyRotO = player.yBodyRotO;
		var xRotO = player.xRotO;
		var yRotO = player.yRotO;
		player.lookAt(EntityAnchorArgument.Anchor.EYES, target);
		player.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), player.onGround()));
		player.yHeadRot = yHeadRot;
		player.yHeadRotO = yHeadRotO;
		player.yBodyRot = yBodyRot;
		player.yBodyRotO = yBodyRotO;
		player.xRotO = xRotO;
		player.yRotO = yRotO;
	}
	
	public static float getLerpedXRot(Entity entity, float partialTick) {
		return partialTick == 1.0F ? entity.getXRot() : Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
	}
	
	public static float getLerpedYRot(Entity entity, float partialTick) {
		return partialTick == 1.0F ? entity.getYRot() : Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
	}
	
	public static boolean isPlayerSpectatingEntity() {
		var minecraft = Minecraft.getInstance();
		var player = minecraft.player;
		return player != null && player.isSpectator() && minecraft.getCameraEntity() != player;
	}
	
	public static float getScale(Entity entity) {
		return entity instanceof LivingEntity living ? living.getScale() : 1.0F;
	}
	
	public static float getMaxScale(Entity cameraEntity) {
		var entity = cameraEntity;
		var scale = getScale(entity);
		while (entity.getVehicle() != null) {
			entity = entity.getVehicle();
			scale = Math.max(scale, getScale(entity));
		}
		return scale;
	}
	
	public static Vec2f applyPassengerRotationConstraints(Player player, Vec2f cameraRot, Vec2f cameraRotO) {
		var vehicle = player.getVehicle();
		var cameraXRot = cameraRot.x();
		var cameraYRot = cameraRot.y();
		
		if (vehicle != null) {
			var partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
			
			var playerXRot = player.getXRot();
			var playerYRot = player.getYRot();
			var playerXRotO = player.xRotO;
			var playerYRotO = player.yRotO;
			var playerYHeadRot = player.yHeadRot;
			var playerYHeadRotO = player.yHeadRotO;
			var playerYBodyRot = player.yBodyRot;
			var playerYBodyRotO = player.yBodyRotO;
			
			var vehicleXRot = vehicle.getXRot();
			var vehicleYRot = vehicle.getYRot();
			var vehicleXRotO = vehicle.xRotO;
			var vehicleYRotO = vehicle.yRotO;
			
			vehicle.setXRot(Mth.rotLerp(partialTick, vehicleXRotO, vehicleXRot));
			vehicle.setYRot(Mth.rotLerp(partialTick, vehicleYRotO, vehicleYRot));
			
			player.setXRot(cameraXRot);
			player.setYRot(cameraYRot);
			player.xRotO = cameraRotO.x();
			player.yRotO = cameraRotO.y();
			player.yHeadRot = cameraYRot;
			player.yHeadRotO = cameraRotO.y();
			player.yBodyRot = cameraYRot;
			player.yBodyRotO = cameraRotO.y();
			
			vehicle.onPassengerTurned(player);
			
			if (player.getXRot() != cameraXRot) {
				cameraXRot = player.getXRot();
			}
			
			if (player.getYRot() != cameraYRot) {
				cameraYRot = player.getYRot();
			}
			
			player.setXRot(playerXRot);
			player.setYRot(playerYRot);
			player.xRotO = playerXRotO;
			player.yRotO = playerYRotO;
			player.yHeadRot = playerYHeadRot;
			player.yHeadRotO = playerYHeadRotO;
			player.yBodyRot = playerYBodyRot;
			player.yBodyRotO = playerYBodyRotO;
			
			vehicle.setXRot(vehicleXRot);
			vehicle.setYRot(vehicleYRot);
		}
		return new Vec2f(cameraXRot, cameraYRot);
	}
	
	public static Vec3 getDeltaMovementWithoutGravity(Entity entity) {
		if (entity.isNoGravity() || entity instanceof Player player && player.getAbilities().flying) {
			return entity.getDeltaMovement();
		}
		return entity.getDeltaMovement().add(0, entity.getGravity(), 0);
	}
	
	public static boolean isScoping(Entity entity) {
		return entity instanceof Player player && player.isScoping();
	}
}
