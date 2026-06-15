package com.github.exopandora.shouldersurfing.client.renderer;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.api.client.renderer.ICrosshairRenderer;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.config.CrosshairConfig;
import com.github.exopandora.shouldersurfing.config.ObjectPickerConfig;
import com.github.exopandora.shouldersurfing.mixin.GuiAccessor;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class CrosshairRenderer implements ICrosshairRenderer {
	private static final ResourceLocation OBSTRUCTION_INDICATOR_SPRITE = new ResourceLocation(MOD_ID, "hud/obstruction_indicator");
	private static final ResourceLocation OBSTRUCTED_CROSSHAIR_SPRITE = new ResourceLocation(MOD_ID, "hud/obstructed_crosshair");
	private static final ResourceLocation OBSTRUCTED_CROSSHAIR_CROSS_SPRITE = new ResourceLocation(MOD_ID, "hud/obstructed_crosshair_cross");
	
	private final ShoulderSurfing instance;
	private Vec2f crosshairOffset;
	private boolean isCrosshairDynamic;
	private boolean isCrosshairVisible;
	private boolean isObstructionCrosshairVisible;
	private boolean isObstructionIndicatorVisible;
	
	public CrosshairRenderer(ShoulderSurfing instance) {
		this.instance = instance;
		this.init();
	}
	
	private void init() {
		this.crosshairOffset = null;
		this.isCrosshairDynamic = false;
		this.isCrosshairVisible = true;
		this.isObstructionCrosshairVisible = false;
		this.isObstructionIndicatorVisible = false;
	}
	
	public void renderTick(Camera camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick) {
		if (this.instance.isShoulderSurfing()) {
			Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
			this.isCrosshairDynamic = computeIsCrosshairDynamic(cameraEntity, this.instance.isAiming());
			if (Minecraft.getInstance().player != null) {
				this.updateDynamicRaytrace(camera, modelViewMatrix, projectionMatrix, partialTick);
			}
		}
		this.isCrosshairVisible = computeIsCrosshairVisible(this.crosshairOffset, this.isCrosshairDynamic, this.instance.isAiming());
		if (this.instance.isShoulderSurfing()) {
			this.isObstructionIndicatorVisible = computeIsObstructionIndicatorVisible(
				this.crosshairOffset, this.isCrosshairDynamic, this.instance.isAiming()
			);
			this.isObstructionCrosshairVisible = computeIsObstructionCrosshairVisible(
				this.instance.isAiming(), this.isObstructionIndicatorVisible
			);
		}
	}
	
	public void preRenderCrosshair(GuiGraphics guiGraphics) {
		if (this.isCrosshairDynamic || this.isObstructionCrosshairVisible) {
			this.setupPoseStack(guiGraphics.pose());
		}
	}
	
	public void postRenderCrosshair(GuiGraphics guiGraphics) {
		if (this.isCrosshairDynamic || this.isObstructionCrosshairVisible) {
			this.resetPoseStack(guiGraphics.pose());
		}
		if (this.isObstructionCrosshairVisible) {
			this.renderObstructionCrosshair(guiGraphics);
		} else if (this.isObstructionIndicatorVisible) {
			this.setupPoseStack(guiGraphics.pose());
			this.renderObstructionIndicator(guiGraphics);
			this.resetPoseStack(guiGraphics.pose());
		}
	}
	
	private void setupPoseStack(PoseStack poseStack) {
		if (this.crosshairOffset != null) {
			poseStack.pushPose();
			poseStack.last().pose().translate(this.crosshairOffset.x(), -this.crosshairOffset.y(), 0F);
		}
	}
	
	private void resetPoseStack(PoseStack poseStack) {
		if (this.crosshairOffset != null) {
			poseStack.popPose();
		}
	}
	
	private void renderObstructionCrosshair(GuiGraphics guiGraphics) {
		RenderSystem.blendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
		this.renderCustomCrosshair(guiGraphics, OBSTRUCTED_CROSSHAIR_SPRITE);
		RenderSystem.defaultBlendFunc();
		this.renderCustomCrosshair(guiGraphics, OBSTRUCTED_CROSSHAIR_CROSS_SPRITE);
	}
	
	private void renderObstructionIndicator(GuiGraphics guiGraphics) {
		RenderSystem.blendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
		this.renderCustomCrosshair(guiGraphics, OBSTRUCTION_INDICATOR_SPRITE);
		RenderSystem.defaultBlendFunc();
	}
	
	private void renderCustomCrosshair(GuiGraphics guiGraphics, ResourceLocation sprite) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || ((GuiAccessor) minecraft.gui).invokeCanRenderCrosshairForSpectator(minecraft.hitResult)) {
			guiGraphics.blit(sprite, (guiGraphics.guiWidth() - 15) / 2, (guiGraphics.guiHeight() - 15) / 2, 0, 0, 15, 15, 15, 15);
		}
	}
	
	private void updateDynamicRaytrace(Camera camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick) {
		ObjectPickerConfig objectPickerConfig = Config.CLIENT.getObjectPickerConfig();
		double interactionRangeOverride = objectPickerConfig.isCustomRaytraceDistanceEnabled()
			? objectPickerConfig.getCustomRaytraceDistance()
			: 0;
		MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
		Player player = Minecraft.getInstance().player;
		// Trace primary crosshair
		PickContext.Builder pickContextBuilder = new PickContext.Builder(camera);
		if (this.isCrosshairDynamic) {
			pickContextBuilder.dynamicTrace();
		}
		PickContext pickContext = pickContextBuilder.build();
		HitResult hitResult = this.instance.getObjectPicker().pick(pickContext, interactionRangeOverride, partialTick, gameMode);
		Vec3 position = hitResult.getLocation();
		// Trace obstruction crosshair
		if (!this.isCrosshairDynamic) {
			pickContext = pickContextBuilder.obstructionTrace(position).build();
			hitResult = this.instance.getObjectPicker().pick(pickContext, interactionRangeOverride, partialTick, gameMode);
			position = hitResult.getLocation();
		}
		Vec2f projected = project2D(position.subtract(camera.getPosition()), modelViewMatrix, projectionMatrix);
		Vec2f crosshairOffset = null;
		if (projected != null) {
			Window window = Minecraft.getInstance().getWindow();
			Vec2f screenSize = new Vec2f(window.getScreenWidth(), window.getScreenHeight());
			Vec2f center = screenSize.divide(2);
			CrosshairConfig crosshairConfig = Config.CLIENT.getCrosshairConfig();
			double maxDistanceToObstruction = crosshairConfig.getObstructionIndicatorMaxDistanceToObstruction();
			if (this.isCrosshairDynamic || !crosshairConfig.isObstructionIndicatorEnabled() || maxDistanceToObstruction <= 0 || position.distanceToSqr(player.getEyePosition()) <= maxDistanceToObstruction * maxDistanceToObstruction) {
				crosshairOffset = projected.subtract(center).divide((float) window.getGuiScale());
			}
		}
		this.crosshairOffset = crosshairOffset;
	}
	
	public void resetState() {
		this.init();
	}
	
	private static boolean computeIsCrosshairDynamic(@Nullable Entity cameraEntity, boolean isAiming) {
		return switch (Config.CLIENT.getCrosshairConfig().getCrosshairType()) {
			case ADAPTIVE -> isAiming;
			case DYNAMIC,
			     DYNAMIC_WITH_1PP -> cameraEntity instanceof Player player && !player.isScoping();
			default -> false;
		};
	}
	
	private static boolean computeIsCrosshairVisible(@Nullable Vec2f crosshairOffset, boolean isCrosshairDynamic, boolean isAiming) {
		if (crosshairOffset == null && isCrosshairDynamic) {
			return false;
		}
		HitResult hitResult = Minecraft.getInstance().hitResult;
		return switch (Config.CLIENT.getCrosshairConfig().getCrosshairVisibility(Perspective.current())) {
			case NEVER -> false;
			case WHEN_AIMING -> isAiming;
			case WHEN_IN_RANGE -> hitResult != null && hitResult.getType() != HitResult.Type.MISS;
			case WHEN_AIMING_OR_IN_RANGE -> isAiming || hitResult != null && hitResult.getType() != HitResult.Type.MISS;
			default -> true;
		};
	}
	
	private static boolean computeIsObstructionIndicatorVisible(@Nullable Vec2f crosshairOffset, boolean isCrosshairDynamic, boolean isAiming) {
		if (crosshairOffset == null || !Config.CLIENT.getCrosshairConfig().isObstructionIndicatorEnabled()) {
			return false;
		}
		if (isCrosshairDynamic) {
			return false;
		}
		if (!isAiming && Config.CLIENT.getCrosshairConfig().isObstructionIndicatorOnlyShownWhenAiming()) {
			return false;
		}
		int minDistanceToCrosshair = Config.CLIENT.getCrosshairConfig().getObstructionIndicatorMinDistanceToCrosshair();
		return crosshairOffset.lengthSquared() >= minDistanceToCrosshair * minDistanceToCrosshair;
	}
	
	private static boolean computeIsObstructionCrosshairVisible(boolean isAiming, boolean isObstructionIndicatorVisible) {
		return isAiming && isObstructionIndicatorVisible;
	}
	
	@Override
	public boolean isCrosshairDynamic() {
		return this.isCrosshairDynamic;
	}
	
	@Override
	public boolean isCrosshairVisible() {
		return this.isCrosshairVisible;
	}
	
	@Override
	public boolean isObstructionCrosshairVisible() {
		return this.isObstructionCrosshairVisible;
	}
	
	@Override
	public boolean isObstructionIndicatorVisible() {
		return this.isObstructionIndicatorVisible;
	}
	
	private static @Nullable Vec2f project2D(Vec3 position, Matrix4f modelView, Matrix4f projection) {
		Window window = Minecraft.getInstance().getWindow();
		int screenWidth = window.getScreenWidth();
		int screenHeight = window.getScreenHeight();
		if (screenWidth == 0 || screenHeight == 0) {
			return null;
		}
		Vector4f vec = new Vector4f((float) position.x(), (float) position.y(), (float) position.z(), 1.0F);
		vec.mul(modelView);
		vec.mul(projection);
		if (vec.w() == 0.0F) {
			return null;
		}
		float w = (1.0F / vec.w()) * 0.5F;
		float x = (vec.x() * w + 0.5F) * screenWidth;
		float y = (vec.y() * w + 0.5F) * screenHeight;
		float z = vec.z() * w + 0.5F;
		vec.set(x, y, z, w);
		if (Float.isInfinite(x) || Float.isInfinite(y) || Float.isNaN(x) || Float.isNaN(y) || w < 0.0F) {
			return null;
		}
		return new Vec2f(x, y);
	}
}
