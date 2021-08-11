package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.datafixers.util.Pair;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.util.ShoulderState;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends Entity
{
	protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, World worldIn)
	{
		super(type, worldIn);
	}
	
	@Override
	public RayTraceResult pick(double distance, float partialTicks, boolean stopOnFluid)
	{
		final ActiveRenderInfo info = Minecraft.getInstance().getEntityRenderDispatcher().camera;
		
		if(ShoulderState.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic() && info != null)
		{
			Pair<Vector3d, Vector3d> look = ShoulderSurfingHelper.shoulderSurfingLook(info, this, partialTicks, distance * distance);
			FluidMode fluidMode = stopOnFluid ? FluidMode.ANY : FluidMode.NONE;
			RayTraceContext context = new RayTraceContext(look.getFirst(), look.getSecond(), RayTraceContext.BlockMode.OUTLINE, fluidMode, this);
			
			return this.level.clip(context);
		}
		
		return super.pick(distance, partialTicks, stopOnFluid);
	}
}
