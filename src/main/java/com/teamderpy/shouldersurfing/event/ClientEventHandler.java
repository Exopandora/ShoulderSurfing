package com.teamderpy.shouldersurfing.event;


import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;
import com.teamderpy.shouldersurfing.util.ShoulderState;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
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
	public static void preRenderGameOverlayEvent(RenderGameOverlayEvent.Pre event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			if(ShoulderState.getProjected() != null)
			{
				final MainWindow mainWindow = Minecraft.getInstance().getWindow();
				float scale = mainWindow.calculateScale(Minecraft.getInstance().options.guiScale, Minecraft.getInstance().isEnforceUnicode()) * ShoulderSurfingHelper.getShadersResMul();
				
				Vec2f window = new Vec2f(mainWindow.getGuiScaledWidth(), mainWindow.getGuiScaledHeight());
				Vec2f center = window.scale(scale).divide(2); // In actual monitor pixels
				Vec2f projectedOffset = ShoulderState.getProjected().subtract(center).divide(scale);
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
	}
	
	@SubscribeEvent
	public static void postRenderGameOverlayEvent(RenderGameOverlayEvent.Post event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS) && Config.CLIENT.getCrosshairType().isDynamic() && ShoulderState.doShoulderSurfing())
		{
			event.getMatrixStack().popPose();
		}
	}
	
	@SubscribeEvent
	public static void cameraSetup(CameraSetup event)
	{
		final World world = Minecraft.getInstance().level;
		
		if(ShoulderState.doShoulderSurfing() && world != null)
		{
			final ActiveRenderInfo info = event.getInfo();
			
			double x = MathHelper.lerp(event.getRenderPartialTicks(), info.getEntity().xo, info.getEntity().getX());
			double y = MathHelper.lerp(event.getRenderPartialTicks(), info.getEntity().yo, info.getEntity().getY()) + MathHelper.lerp(event.getRenderPartialTicks(), info.eyeHeightOld, info.eyeHeight);
			double z = MathHelper.lerp(event.getRenderPartialTicks(), info.getEntity().zo, info.getEntity().getZ());
			
			info.setPosition(x, y, z);
			
			Vector3d offset = new Vector3d(-Config.CLIENT.getOffsetZ(), Config.CLIENT.getOffsetY(), Config.CLIENT.getOffsetX());
			double distance = ShoulderSurfingHelper.cameraDistance(info, world, info.getMaxZoom(offset.length()));
			Vector3d scaled = offset.normalize().scale(distance);
			
			ShoulderState.setCameraDistance(distance);
			
			info.move(scaled.x, scaled.y, scaled.z);
		}
	}
	
	@SubscribeEvent
	public static void renderWorldLast(RenderWorldLastEvent event)
	{
		final ActiveRenderInfo info = Minecraft.getInstance().gameRenderer.getMainCamera();
		final PlayerController controller = Minecraft.getInstance().gameMode;
		
		if(ShoulderState.doShoulderSurfing())
		{
			double playerReach = Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
			RayTraceResult result = ShoulderSurfingHelper.traceFromEyes(info.getEntity(), controller, playerReach, event.getPartialTicks());
			Vector3d position = result.getLocation().subtract(info.getPosition());
			Matrix4f modelView = event.getMatrixStack().last().pose();
			Matrix4f projection = event.getProjectionMatrix();
			
			ShoulderState.setProjected(ShoulderSurfingHelper.project2D(position, modelView, projection));
		}
	}
}
