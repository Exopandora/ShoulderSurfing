package com.github.exopandora.shouldersurfing.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.vector.Vector3d;

public class EntityHelper
{
	public static void lookAtTarget(ClientPlayerEntity player, Vector3d target)
	{
		float yHeadRot = player.yHeadRot;
		float yHeadRotO = player.yHeadRotO;
		float yBodyRot = player.yBodyRot;
		float yBodyRotO = player.yBodyRotO;
		float xRotO = player.xRotO;
		float yRotO = player.yRotO;
		player.lookAt(EntityAnchorArgument.Type.EYES, target);
		player.connection.send(new CPlayerPacket.RotationPacket(player.yRot, player.xRot, player.isOnGround()));
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
		ClientPlayerEntity player = minecraft.player;
		return player != null && player.isSpectator() && minecraft.getCameraEntity() != player;
	}
}
