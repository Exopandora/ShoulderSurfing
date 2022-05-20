package com.teamderpy.shouldersurfing.event;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;
import com.teamderpy.shouldersurfing.util.ShoulderState;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler
{
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent event)
	{
		if(event.phase.equals(Phase.START))
		{
			if(!Perspective.FIRST_PERSON.equals(Perspective.current()))
			{
				ShoulderState.setSwitchPerspective(false);
			}
			
			ShoulderState.setAiming(ShoulderSurfingHelper.isHoldingSpecialItem());
			
			if(ShoulderState.isAiming() && Config.CLIENT.getCrosshairType().doSwitchPerspective() && ShoulderState.doShoulderSurfing())
			{
				ShoulderSurfingHelper.setPerspective(Perspective.FIRST_PERSON);
				ShoulderState.setSwitchPerspective(true);
			}
			else if(!ShoulderState.isAiming() && Perspective.FIRST_PERSON.equals(Perspective.current()) && ShoulderState.doSwitchPerspective())
			{
				ShoulderSurfingHelper.setPerspective(Perspective.SHOULDER_SURFING);
			}
		}
	}
	
	@SubscribeEvent
	public static void preRenderPlayerEvent(RenderPlayerEvent.Pre event)
	{
		if(event.isCancelable() && event.getPlayer().equals(Minecraft.getInstance().player) && Minecraft.getInstance().screen == null)
		{
			if(ShoulderState.getCameraDistance() < 0.80 && Config.CLIENT.keepCameraOutOfHead() && ShoulderState.doShoulderSurfing())
			{
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void preRenderGameOverlayEvent(RenderGameOverlayEvent.PreLayer event)
	{
		if(ForgeIngameGui.CROSSHAIR_ELEMENT.equals(event.getOverlay()))
		{
			if(ShoulderState.getProjected() != null)
			{
				Vec2f scaledWindow = new Vec2f(event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
				Vec2f window = new Vec2f(event.getWindow().getScreenWidth(), event.getWindow().getScreenHeight());
				Vec2f scale = scaledWindow.divide(window);
				Vec2f center = window.divide(2); // In actual monitor pixels
				Vec2f projectedOffset = ShoulderState.getProjected().subtract(center).scale(scale);
				Vec2f lastTranslation = ShoulderState.getLastTranslation();
				Vec2f interpolated = projectedOffset.subtract(lastTranslation).scale(event.getPartialTicks());
				
				ShoulderState.setTranslation(ShoulderState.getLastTranslation().add(interpolated));
			}
			
			if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderState.doShoulderSurfing())
			{
				event.getMatrixStack().pushPose();
				event.getMatrixStack().last().pose().translate(new Vector3f(ShoulderState.getTranslation().getX(), -ShoulderState.getTranslation().getY(), 0F));
				ShoulderState.setLastTranslation(ShoulderState.getTranslation());
			}
			else
			{
				ShoulderState.setLastTranslation(Vec2f.ZERO);
			}
		}
		//Using BOSS_HEALTH_ELEMENT to pop matrix because when CROSSHAIR_ELEMENT is cancelled it will not fire RenderGameOverlayEvent#PreLayer and cause a stack overflow
		else if(ForgeIngameGui.BOSS_HEALTH_ELEMENT.equals(event.getOverlay()) && Config.CLIENT.getCrosshairType().isDynamic() && ShoulderState.doShoulderSurfing())
		{
			event.getMatrixStack().popPose();
		}
	}
	
	@SubscribeEvent
	public static void cameraSetupEvent(CameraSetup event)
	{
		final Level level = Minecraft.getInstance().level;
		
		if(ShoulderState.doShoulderSurfing() && level != null)
		{
			final Camera camera = event.getCamera();
			
			double x = Mth.lerp(event.getPartialTicks(), camera.getEntity().xo, camera.getEntity().getX());
			double y = Mth.lerp(event.getPartialTicks(), camera.getEntity().yo, camera.getEntity().getY()) + Mth.lerp(event.getPartialTicks(), camera.eyeHeightOld, camera.eyeHeight);
			double z = Mth.lerp(event.getPartialTicks(), camera.getEntity().zo, camera.getEntity().getZ());
			
			camera.setPosition(x, y, z);
			
			Vec3 offset = new Vec3(-Config.CLIENT.getOffsetZ(), Config.CLIENT.getOffsetY(), Config.CLIENT.getOffsetX());
			double distance = ShoulderSurfingHelper.cameraDistance(camera, level, camera.getMaxZoom(offset.length()));
			Vec3 scaled = offset.normalize().scale(distance);
			
			ShoulderState.setCameraDistance(distance);
			
			camera.move(scaled.x, scaled.y, scaled.z);
		}
	}
	
	@SubscribeEvent
	public static void renderLevelLastEvent(RenderLevelLastEvent event)
	{
		final Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		final MultiPlayerGameMode controller = Minecraft.getInstance().gameMode;
		
		if(ShoulderState.doShoulderSurfing())
		{
			double playerReach = Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
			HitResult result = ShoulderSurfingHelper.traceFromEyes(camera.getEntity(), controller, playerReach, event.getPartialTick());
			Vec3 position = result.getLocation().subtract(camera.getPosition());
			Matrix4f modelView = event.getPoseStack().last().pose();
			Matrix4f projection = event.getProjectionMatrix();
			
			ShoulderState.setProjected(ShoulderSurfingHelper.project2D(position, modelView, projection));
		}
	}
}
