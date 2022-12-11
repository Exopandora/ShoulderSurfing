package com.teamderpy.shouldersurfing.compatibility;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderHelper.ShoulderLook;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;

import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.core.pick.ObjectPicker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ShoulderSurfingWailaPlugin implements IWailaPlugin
{
	@Override
	public void register(IRegistrar registrar)
	{
		registrar.replacePicker(new ShoulderSurfingObjectPicker());
	}
	
	private static class ShoulderSurfingObjectPicker implements IObjectPicker
	{
		@Override
		public HitResult pick(Minecraft mc, double maxDistance, float partialTick, IPluginConfig config)
		{
			if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
			{
				Entity camera = mc.getCameraEntity();
				
				if(camera == null)
				{
					return IObjectPicker.MISS;
				}
				
				ShoulderLook look = ShoulderHelper.shoulderSurfingLook(mc.gameRenderer.getMainCamera(), camera, partialTick, maxDistance * maxDistance);
				ClipContext.Fluid fluidContext = config.getBoolean(WailaConstants.CONFIG_SHOW_FLUID) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE;
				BlockHitResult blockHit = camera.level.clip(new ClipContext(look.cameraPos(), look.traceEndPos(), ClipContext.Block.OUTLINE, fluidContext, camera));
				
				if(config.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY))
				{
					EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(camera, look.cameraPos(), look.traceEndPos(), new AABB(look.cameraPos(), look.traceEndPos()), EntitySelector.ENTITY_STILL_ALIVE, 0f);
					
					if(entityHit != null)
					{
						if(HitResult.Type.MISS.equals(blockHit.getType()))
						{
							return entityHit;
						}
						
						double blockDistance = blockHit.getLocation().distanceToSqr(look.cameraPos());
						double entityDistance = entityHit.getLocation().distanceToSqr(look.cameraPos());
						
						if(entityDistance < blockDistance)
						{
							return entityHit;
						}
					}
				}
				
				return blockHit;
			}
			
			return ObjectPicker.INSTANCE.pick(mc, maxDistance, partialTick, config);
		}
	}
}
