package com.teamderpy.shouldersurfing.mixins;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderHelper.ShoulderLook;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Item.class)
public class MixinItem
{
	@Redirect
	(
		method = "getPlayerPOVHitResult",
		at = @At
		(
			value = "NEW",
			target = "Lnet/minecraft/world/level/ClipContext;"
		)
	)
	private static ClipContext initClipContext(Vec3 start, Vec3 end, Block block, Fluid fluid, @NotNull Entity entity)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			Minecraft minecraft = Minecraft.getInstance();
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(minecraft.gameRenderer.getMainCamera(), entity, 1.0F, start.distanceToSqr(end));
			Vec3 eyePosition = entity.getEyePosition(1.0F);
			Vec3 from = eyePosition.add(look.headOffset());
			Vec3 to = look.traceEndPos();
			return new ClipContext(from, to, block, fluid, entity);
		}
		
		return new ClipContext(start, end, block, fluid, entity);
	}
}
