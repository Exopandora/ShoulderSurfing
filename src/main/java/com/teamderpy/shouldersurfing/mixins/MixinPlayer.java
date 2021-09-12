package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.datafixers.util.Pair;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.util.ShoulderState;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@Mixin(Player.class)
public abstract class MixinPlayer extends Entity
{
	protected MixinPlayer(EntityType<? extends LivingEntity> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	public HitResult pick(double distance, float partialTicks, boolean stopOnFluid)
	{
		final Camera camera = Minecraft.getInstance().getEntityRenderDispatcher().camera;
		
		if(ShoulderState.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic() && camera != null)
		{
			Pair<Vec3, Vec3> look = ShoulderSurfingHelper.shoulderSurfingLook(camera, this, partialTicks, distance * distance);
			ClipContext.Fluid fluidMode = stopOnFluid ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
			ClipContext context = new ClipContext(look.getFirst(), look.getSecond(), ClipContext.Block.OUTLINE, fluidMode, this);
			
			return this.level.clip(context);
		}
		
		return super.pick(distance, partialTicks, stopOnFluid);
	}
}
