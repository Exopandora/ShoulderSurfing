package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderHelper.ShoulderLook;
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
	@SuppressWarnings("resource")
	public HitResult pick(double distance, float partialTicks, boolean stopOnFluid)
	{
		final Camera camera = Minecraft.getInstance().getEntityRenderDispatcher().camera;
		
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic() && camera != null)
		{
			ShoulderLook look = ShoulderHelper.shoulderSurfingLook(camera, this, partialTicks, distance * distance);
			ClipContext.Fluid fluidMode = stopOnFluid ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
			ClipContext context = new ClipContext(look.cameraPos(), look.traceEndPos(), ClipContext.Block.OUTLINE, fluidMode, this);
			return this.level.clip(context);
		}
		
		return super.pick(distance, partialTicks, stopOnFluid);
	}
}
