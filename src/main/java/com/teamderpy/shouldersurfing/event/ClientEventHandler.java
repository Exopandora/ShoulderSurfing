package com.teamderpy.shouldersurfing.event;


import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.math.Vec2f;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.MathHelper;
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
	private static boolean switchPerspective;
	private static Vec2f lastTranslation = Vec2f.ZERO;
	private static Vec2f translation = Vec2f.ZERO;
	private static Vec2f projected = null;
	
	public static boolean isAiming;
	public static double cameraDistance;
	
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent event)
	{
		if(event.phase.equals(Phase.START) && Minecraft.getInstance().player != null)
		{
			if(!ClientEventHandler.isAiming && ShoulderSurfingHelper.isHoldingSpecialItem())
			{
				if(Config.CLIENT.getCrosshairType().doSwitchPerspective() && ShoulderSurfingHelper.doShoulderSurfing())
				{
					ShoulderSurfingHelper.setPerspective(Perspective.FIRST_PERSON);
					ClientEventHandler.switchPerspective = true;
				}
				
				ClientEventHandler.isAiming = true;
			}
			else if(ClientEventHandler.isAiming && !ShoulderSurfingHelper.isHoldingSpecialItem())
			{
				if(!Config.CLIENT.getCrosshairType().doSwitchPerspective() && ShoulderSurfingHelper.doShoulderSurfing() && ClientEventHandler.switchPerspective)
				{
					ShoulderSurfingHelper.setPerspective(Perspective.SHOULDER_SURFING);
					ClientEventHandler.switchPerspective = false;
				}
				
				ClientEventHandler.isAiming = false;
			}
		}
	}
	
	@SubscribeEvent
	public static void preRenderPlayerEvent(RenderPlayerEvent.Pre event)
	{
		if(event.isCancelable() && event.getPlayer().equals(Minecraft.getInstance().player) && Minecraft.getInstance().currentScreen == null)
		{
			if(ClientEventHandler.cameraDistance < 0.80 && Config.CLIENT.keepCameraOutOfHead() && ShoulderSurfingHelper.doShoulderSurfing())
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
			final MainWindow mainWindow = Minecraft.getInstance().getMainWindow();
			float scale = mainWindow.calcGuiScale(Minecraft.getInstance().gameSettings.guiScale, Minecraft.getInstance().getForceUnicodeFont()) * ShoulderSurfing.getShadersResMul();
			
			Vec2f window = new Vec2f(mainWindow.getScaledWidth(), mainWindow.getScaledHeight());
			Vec2f center = window.scale(scale).divide(2); // In actual monitor pixels
			
			if(ClientEventHandler.projected != null)
			{
				Vec2f projectedOffset = ClientEventHandler.projected.subtract(center).divide(scale);
				ClientEventHandler.translation = ClientEventHandler.lastTranslation.add(projectedOffset.subtract(ClientEventHandler.lastTranslation).scale(event.getPartialTicks()));
			}
			
			if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderSurfingHelper.doShoulderSurfing())
			{
				event.getMatrixStack().getLast().getMatrix().translate(new Vector3f(ClientEventHandler.translation.getX(), -ClientEventHandler.translation.getY(), 0F));
				ClientEventHandler.lastTranslation = ClientEventHandler.translation;
			}
			else
			{
				ClientEventHandler.lastTranslation = Vec2f.ZERO;
			}
		}
	}
	
	@SubscribeEvent
	public static void postRenderGameOverlayEvent(RenderGameOverlayEvent.Post event)
	{
		if(event.getType().equals(RenderGameOverlayEvent.ElementType.CROSSHAIRS))
		{
			if(Config.CLIENT.getCrosshairType().isDynamic() && ShoulderSurfingHelper.doShoulderSurfing())
			{
				event.getMatrixStack().getLast().getMatrix().translate(new Vector3f(-ClientEventHandler.translation.getX(), ClientEventHandler.translation.getY(), 0F));
			}
		}
	}
	
	@SubscribeEvent
	public static void cameraSetup(CameraSetup event)
	{
		final World world = Minecraft.getInstance().world;
		
		if(ShoulderSurfingHelper.doShoulderSurfing() && world != null)
		{
			final ActiveRenderInfo info = event.getInfo();
			
			double x = MathHelper.lerp(event.getRenderPartialTicks(), info.getRenderViewEntity().prevPosX, info.getRenderViewEntity().getPosX());
			double y = MathHelper.lerp(event.getRenderPartialTicks(), info.getRenderViewEntity().prevPosY, info.getRenderViewEntity().getPosY()) + MathHelper.lerp(event.getRenderPartialTicks(), info.previousHeight, info.height);
			double z = MathHelper.lerp(event.getRenderPartialTicks(), info.getRenderViewEntity().prevPosZ, info.getRenderViewEntity().getPosZ());
			
			info.setPosition(x, y, z);
			
			Vector3d offset = new Vector3d(-Config.CLIENT.getOffsetZ(), Config.CLIENT.getOffsetY(), Config.CLIENT.getOffsetX());
			ClientEventHandler.cameraDistance = ShoulderSurfingHelper.calcCameraDistance(info, world, info.calcCameraDistance(offset.length()));
			Vector3d scaled = offset.normalize().scale(ClientEventHandler.cameraDistance);
			
			info.movePosition(scaled.x, scaled.y, scaled.z);
		}
	}
	
	@SubscribeEvent
	public static void renderWorldLast(RenderWorldLastEvent event)
	{
		final ActiveRenderInfo info = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
		
		if(info != null)
		{
			Vector3d rayTrace = ShoulderSurfingHelper.traceFromEyes(info.getRenderViewEntity(), Minecraft.getInstance().playerController, event.getPartialTicks());
			
			if(rayTrace != null)
			{
				Vector3d position = rayTrace.subtract(info.getProjectedView());
				Matrix4f modelView = event.getMatrixStack().getLast().getMatrix();
				Matrix4f projection = event.getProjectionMatrix();
				
				ClientEventHandler.projected = ShoulderSurfingHelper.project2D(position, modelView, projection);
			}
		}
	}
}
