package com.teamderpy.shouldersurfing.asm;

import java.util.function.Predicate;

import com.mojang.datafixers.util.Pair;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.util.ShoulderState;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;

public class InjectionDelegation
{
	public static EntityHitResult getEntityHitResult(Entity shooter, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter, double distanceSq)
	{
		if(ShoulderState.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			Pair<Vec3, Vec3> look = ShoulderSurfingHelper.shoulderSurfingLook(Minecraft.getInstance().gameRenderer.getMainCamera(), shooter, Minecraft.getInstance().getFrameTime(), distanceSq);
			return ProjectileUtil.getEntityHitResult(shooter, look.getSecond(), look.getFirst(), boundingBox, filter, distanceSq);
		}
		
		return ProjectileUtil.getEntityHitResult(shooter, startVec, endVec, boundingBox, filter, distanceSq);
	}
	
	public static void setCameraType(CameraType cameraType)
	{
		if(cameraType != null && !cameraType.equals(Minecraft.getInstance().options.getCameraType()))
		{
			ShoulderState.setEnabled(false);
		}
	}
	
	public static boolean doRenderCrosshair(CameraType cameraType)
	{
		return Config.CLIENT.getCrosshairVisibility(Perspective.current()).doRender(ShoulderState.isAiming());
	}
	
	public static HitResult pick(Entity player, HitResult result, double distance, float partialTicks, boolean stopOnFluid)
	{
		final Camera camera = Minecraft.getInstance().getEntityRenderDispatcher().camera;
		
		if(ShoulderState.doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic() && camera != null)
		{
			Pair<Vec3, Vec3> look = ShoulderSurfingHelper.shoulderSurfingLook(camera, player, partialTicks, distance * distance);
			ClipContext.Fluid fluidMode = stopOnFluid ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
			ClipContext context = new ClipContext(look.getFirst(), look.getSecond(), ClipContext.Block.OUTLINE, fluidMode, player);
			
			return player.level.clip(context);
		}
		
		return result;
	}
	
	// Fix OptiFine shaders
	public static void updateActiveRenderInfo(Camera camera, Minecraft mc, float partialTicks)
	{
		camera.setup(mc.level, mc.getCameraEntity() == null ? mc.player : mc.getCameraEntity(), !mc.options.getCameraType().isFirstPerson(), mc.options.getCameraType().isMirrored(), partialTicks);
		EntityViewRenderEvent.CameraSetup cameraSetup = ForgeHooksClient.onCameraSetup(mc.gameRenderer, camera, partialTicks);
		camera.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
	}
}
