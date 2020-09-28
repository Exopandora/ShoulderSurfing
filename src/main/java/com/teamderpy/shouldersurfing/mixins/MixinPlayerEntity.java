package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.event.ClientEventHandler;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends Entity
{
	public MixinPlayerEntity(EntityType<?> entityTypeIn, World worldIn)
	{
		super(entityTypeIn, worldIn);
	}
	
	@Override
	public RayTraceResult pick(double distance, float partialTicks, boolean stopOnFluid)
	{
		final ActiveRenderInfo info = Minecraft.getInstance().getRenderManager().info;
		
		if(ShoulderSurfingHelper.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic() && info != null)
		{
			Vector3d cameraOffset = ShoulderSurfingHelper.calcCameraOffset(info, ClientEventHandler.cameraDistance);
			Vector3d offset = ShoulderSurfingHelper.calcRayTraceHeadOffset(info, cameraOffset);
			Vector3d start = this.getEyePosition(partialTicks).add(cameraOffset);
			Vector3d look = this.getLook(partialTicks);
			
			if(Config.CLIENT.limitPlayerReach() && offset.length() < distance)
			{
				distance = Math.sqrt((distance * distance) - offset.lengthSquared());
			}
			
			Vector3d end = start.add(look.scale(distance + cameraOffset.distanceTo(offset)));
			
			return this.world.rayTraceBlocks(new RayTraceContext(start, end, RayTraceContext.BlockMode.OUTLINE, stopOnFluid ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, this));
		}
		
		return super.pick(distance, partialTicks, stopOnFluid);
	}
}
