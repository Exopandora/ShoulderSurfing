package com.github.exopandora.shouldersurfing.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class EntityHelper
{
	public static void lookAtTarget(LocalPlayer player, Vec3 target)
	{
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
	
	public static boolean isPlayerSpectatingEntity()
	{
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;
		return player != null && player.isSpectator() && minecraft.getCameraEntity() != player;
	}
	
	public static float getScale(Entity entity)
	{
		return entity instanceof LivingEntity living ? living.getScale() : 1.0F;
	}
}
