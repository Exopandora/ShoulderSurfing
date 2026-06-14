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
		float yHeadRot = player.yHeadRot;
		float yHeadRotO = player.yHeadRotO;
		float yBodyRot = player.yBodyRot;
		float yBodyRotO = player.yBodyRotO;
		float xRotO = player.xRotO;
		float yRotO = player.yRotO;
		player.lookAt(EntityAnchorArgument.Anchor.EYES, target);
		player.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), player.onGround(), player.horizontalCollision));
		player.yHeadRot = yHeadRot;
		player.yHeadRotO = yHeadRotO;
		player.yBodyRot = yBodyRot;
		player.yBodyRotO = yBodyRotO;
		player.xRotO = xRotO;
		player.yRotO = yRotO;
	}
	
	public static boolean isPlayerSpectatingEntity() {
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;
		return player != null && player.isSpectator() && minecraft.getCameraEntity() != player;
	}
	
	public static float getScale(Entity entity) {
		return entity instanceof LivingEntity living ? living.getScale() : 1.0F;
	}
	
	public static float getMaxScale(Entity cameraEntity) {
		Entity entity = cameraEntity;
		float scale = getScale(entity);
		
		while (entity.getVehicle() != null) {
			entity = entity.getVehicle();
			scale = Math.max(scale, getScale(entity));
		}
		
		return scale;
	}
	
	public static Vec2f applyPassengerRotationConstraints(Player player, Vec2f cameraRot, Vec2f cameraRotO) {
		Entity vehicle = player.getVehicle();
		float cameraXRot = cameraRot.x();
		float cameraYRot = cameraRot.y();
		
		if (vehicle != null) {
			float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
			
			float playerXRot = player.getXRot();
			float playerYRot = player.getYRot();
			float playerXRotO = player.xRotO;
			float playerYRotO = player.yRotO;
			float playerYHeadRot = player.yHeadRot;
			float playerYHeadRotO = player.yHeadRotO;
			float playerYBodyRot = player.yBodyRot;
			float playerYBodyRotO = player.yBodyRotO;
			
			float vehicleXRot = vehicle.getXRot();
			float vehicleYRot = vehicle.getYRot();
			float vehicleXRotO = vehicle.xRotO;
			float vehicleYRotO = vehicle.yRotO;
			
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
		return entity.getDeltaMovement().add(0, entity.getGravity(), 0);
	}
	
	public static boolean isScoping(Entity entity) {
		return entity instanceof Player player && player.isScoping();
	}
}
