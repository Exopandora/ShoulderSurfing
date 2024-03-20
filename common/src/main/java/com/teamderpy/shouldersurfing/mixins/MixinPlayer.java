package com.teamderpy.shouldersurfing.mixins;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

@Mixin(Player.class)
public abstract class MixinPlayer extends Entity
{
	protected MixinPlayer(EntityType<? extends LivingEntity> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	public @NotNull HitResult pick(double distance, float partialTicks, boolean stopOnFluid)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Camera camera = minecraft.getEntityRenderDispatcher().camera;
		
		if(ShoulderInstance.getInstance().doShoulderSurfing() && camera != null)
		{
			ClipContext.Fluid fluidContext = stopOnFluid ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
			return ShoulderHelper.traceBlocks(camera, this, fluidContext, distance, partialTicks, !Config.CLIENT.getCrosshairType().isDynamic());
		}
		
		return super.pick(distance, partialTicks, stopOnFluid);
	}
}
