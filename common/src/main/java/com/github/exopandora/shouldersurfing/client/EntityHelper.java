package com.github.exopandora.shouldersurfing.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
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
		player.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), player.onGround()));
		player.yHeadRot = yHeadRot;
		player.yHeadRotO = yHeadRotO;
		player.yBodyRot = yBodyRot;
		player.yBodyRotO = yBodyRotO;
		player.xRotO = xRotO;
		player.yRotO = yRotO;
	}
}
