package com.github.exopandora.shouldersurfing.client;

import com.github.exopandora.shouldersurfing.api.client.ICrosshairRenderer;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import com.github.exopandora.shouldersurfing.mixins.IngameGuiAccessor;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.GameType;
import org.jetbrains.annotations.Nullable;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;
import static com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import static com.mojang.blaze3d.platform.GlStateManager.SourceFactor;

public class CrosshairRenderer implements ICrosshairRenderer
{
	private static final ResourceLocation OBSTRUCTION_INDICATOR_SPRITE = new ResourceLocation(MOD_ID, "textures/gui/sprites/hud/obstruction_indicator.png");
	private static final ResourceLocation OBSTRUCTED_CROSSHAIR_SPRITE = new ResourceLocation(MOD_ID, "textures/gui/sprites/hud/obstructed_crosshair.png");
	private static final ResourceLocation OBSTRUCTED_CROSSHAIR_OPAQUE_SPRITE = new ResourceLocation(MOD_ID, "textures/gui/sprites/hud/obstructed_crosshair_opaque.png");
	
	private final ShoulderSurfingImpl instance;
	private Vec2f crosshairOffset;
	
	public CrosshairRenderer(ShoulderSurfingImpl instance)
	{
		this.instance = instance;
		this.init();
	}
	
	private void init()
	{
		this.crosshairOffset = null;
	}
	
	public void preRenderCrosshair(MatrixStack poseStack)
	{
		boolean isDynamic = this.isCrosshairDynamic(Minecraft.getInstance().getCameraEntity());
		
		if(isDynamic || this.doRenderObstructionCrosshair())
		{
			this.setupPoseStack(poseStack);
		}
	}
	
	public void postRenderCrosshair(MatrixStack poseStack)
	{
		boolean doRenderObstructionCrosshair = this.doRenderObstructionCrosshair();
		boolean isDynamic = this.isCrosshairDynamic(Minecraft.getInstance().getCameraEntity());
		
		if(isDynamic || doRenderObstructionCrosshair)
		{
			this.resetPoseStack(poseStack);
		}
		
		if(doRenderObstructionCrosshair)
		{
			this.renderObstructionCrosshair(poseStack);
		}
		else if(this.doRenderObstructionIndicator())
		{
			this.setupPoseStack(poseStack);
			this.renderObstructionIndicator(poseStack);
			this.resetPoseStack(poseStack);
		}
	}
	
	private void setupPoseStack(MatrixStack poseStack)
	{
		if(this.crosshairOffset != null)
		{
			poseStack.pushPose();
			poseStack.last().pose().translate(new Vector3f(this.crosshairOffset.x(), -this.crosshairOffset.y(), 0F));
		}
	}
	
	private void resetPoseStack(MatrixStack poseStack)
	{
		if(this.crosshairOffset != null)
		{
			poseStack.popPose();
		}
	}
	
	@Override
	public boolean doRenderCrosshair()
	{
		return Config.CLIENT.getCrosshairVisibility(Perspective.current()).doRender(Minecraft.getInstance().hitResult, this.instance.isAiming()) &&
			(this.crosshairOffset != null || !this.isCrosshairDynamic(Minecraft.getInstance().getCameraEntity()));
	}
	
	@Override
	public boolean doRenderObstructionCrosshair()
	{
		return this.doRenderObstructionIndicator() && this.instance.isAiming();
	}
	
	@Override
	public boolean doRenderObstructionIndicator()
	{
		int minDistanceToCrosshair = Config.CLIENT.getObstructionIndicatorMinDistanceToCrosshair();
		return this.crosshairOffset != null && this.instance.isShoulderSurfing() && Config.CLIENT.getShowObstructionCrosshair() &&
			(this.instance.isAiming() || !Config.CLIENT.showObstructionIndicatorWhenAiming()) &&
			!this.isCrosshairDynamic(Minecraft.getInstance().getCameraEntity()) &&
			this.crosshairOffset.lengthSquared() >= minDistanceToCrosshair * minDistanceToCrosshair;
	}
	
	public void updateDynamicRaytrace(ActiveRenderInfo camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick)
	{
		if(this.instance.isShoulderSurfing() && Minecraft.getInstance().player != null)
		{
			boolean isDynamic = this.isCrosshairDynamic(Minecraft.getInstance().getCameraEntity());
			double interactionRangeOverride = Config.CLIENT.useCustomRaytraceDistance() ? Config.CLIENT.getCustomRaytraceDistance() : 0;
			PlayerController gameMode = Minecraft.getInstance().gameMode;
			PlayerEntity player = Minecraft.getInstance().player;
			// Trace primary crosshair
			PickContext.Builder pickContextBuilder = new PickContext.Builder(camera);
			
			if(isDynamic)
			{
				pickContextBuilder.dynamicTrace();
			}
			
			PickContext pickContext = pickContextBuilder.build();
			RayTraceResult hitResult = this.instance.getObjectPicker().pick(pickContext, interactionRangeOverride, partialTick, gameMode);
			Vector3d position = hitResult.getLocation();
			
			// Trace obstruction crosshair
			if(!isDynamic)
			{
				pickContext = pickContextBuilder.obstructionTrace(position).build();
				hitResult = this.instance.getObjectPicker().pick(pickContext, interactionRangeOverride, partialTick, gameMode);
				position = hitResult.getLocation();
			}
			
			Vec2f projected = project2D(position.subtract(camera.getPosition()), modelViewMatrix, projectionMatrix);
			Vec2f crosshairOffset = null;
			
			if(projected != null)
			{
				MainWindow window = Minecraft.getInstance().getWindow();
				Vec2f screenSize = new Vec2f(window.getScreenWidth(), window.getScreenHeight());
				Vec2f center = screenSize.divide(2);
				double maxDistanceToObstruction = Config.CLIENT.getObstructionIndicatorMaxDistanceToObstruction();
				
				if(isDynamic || !Config.CLIENT.getShowObstructionCrosshair() || maxDistanceToObstruction <= 0 || position.distanceToSqr(player.getEyePosition(partialTick)) <= maxDistanceToObstruction * maxDistanceToObstruction)
				{
					crosshairOffset = projected.subtract(center).divide((float) window.getGuiScale());
				}
			}
			
			this.crosshairOffset = crosshairOffset;
		}
	}
	
	@Override
	public boolean isCrosshairDynamic(Entity entity)
	{
		return this.instance.isShoulderSurfing() && Config.CLIENT.getCrosshairType().isDynamic(entity, this.instance.isAiming());
	}
	
	private void renderObstructionCrosshair(MatrixStack poseStack)
	{
		RenderSystem.blendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
		this.renderCustomCrosshair(poseStack, OBSTRUCTED_CROSSHAIR_SPRITE);
		RenderSystem.defaultBlendFunc();
		this.renderCustomCrosshair(poseStack, OBSTRUCTED_CROSSHAIR_OPAQUE_SPRITE);
	}
	
	private void renderObstructionIndicator(MatrixStack poseStack)
	{
		RenderSystem.blendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
		this.renderCustomCrosshair(poseStack, OBSTRUCTION_INDICATOR_SPRITE);
		RenderSystem.defaultBlendFunc();
	}
	
	private void renderCustomCrosshair(MatrixStack poseStack, ResourceLocation sprite)
	{
		Minecraft minecraft = Minecraft.getInstance();
		
		if(minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || ((IngameGuiAccessor) minecraft.gui).invokeCanRenderCrosshairForSpectator(minecraft.hitResult))
		{
			minecraft.getTextureManager().bind(sprite);
			AbstractGui.blit(poseStack, (minecraft.getWindow().getGuiScaledWidth() - 15) / 2, (minecraft.getWindow().getGuiScaledHeight() - 15) / 2, 0, 0, 15, 15, 15, 15);
		}
	}
	
	public void resetState()
	{
		this.init();
	}
	
	private static @Nullable Vec2f project2D(Vector3d position, Matrix4f modelView, Matrix4f projection)
	{
		MainWindow window = Minecraft.getInstance().getWindow();
		int screenWidth = window.getScreenWidth();
		int screenHeight = window.getScreenHeight();
		
		if(screenWidth == 0 || screenHeight == 0)
		{
			return null;
		}
		
		Vector4f vec = new Vector4f((float) position.x(), (float) position.y(), (float) position.z(), 1.0F);
		vec.transform(modelView);
		vec.transform(projection);
		
		if(vec.w() == 0.0F)
		{
			return null;
		}
		
		float w = (1.0F / vec.w()) * 0.5F;
		float x = (vec.x() * w + 0.5F) * screenWidth;
		float y = (vec.y() * w + 0.5F) * screenHeight;
		float z = vec.z() * w + 0.5F;
		vec.set(x, y, z, w);
		
		if(Float.isInfinite(x) || Float.isInfinite(y) || Float.isNaN(x) || Float.isNaN(y) || w < 0.0F)
		{
			return null;
		}
		
		return new Vec2f(x, y);
	}
}
