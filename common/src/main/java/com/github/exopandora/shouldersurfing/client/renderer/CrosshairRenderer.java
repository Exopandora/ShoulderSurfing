package com.github.exopandora.shouldersurfing.client.renderer;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.api.client.renderer.ICrosshairRenderer;
import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.mixin.GuiAccessor;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class CrosshairRenderer implements ICrosshairRenderer {
	private static final Identifier OBSTRUCTION_INDICATOR_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "hud/obstruction_indicator");
	private static final Identifier OBSTRUCTED_CROSSHAIR_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "hud/obstructed_crosshair");
	private static final Identifier OBSTRUCTED_CROSSHAIR_CROSS_SPRITE = Identifier.fromNamespaceAndPath(MOD_ID, "hud/obstructed_crosshair_cross");
	
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
	
	public void renderTick(Camera camera, Matrix4fc modelViewMatrix, Matrix4f projectionMatrix, float partialTick) {
		var cameraEntity = Minecraft.getInstance().getCameraEntity();
		this.isCrosshairDynamic = computeIsCrosshairDynamic(this.instance, cameraEntity);
		this.crosshairOffset = computeCrosshairOffset(
			this.instance, camera, modelViewMatrix, projectionMatrix, this.isCrosshairDynamic, partialTick
		);
		this.isCrosshairVisible = computeIsCrosshairVisible(this.instance, this.crosshairOffset, this.isCrosshairDynamic);
		this.isObstructionIndicatorVisible = computeIsObstructionIndicatorVisible(
			this.instance, this.crosshairOffset, this.isCrosshairDynamic
		);
		this.isObstructionCrosshairVisible = computeIsObstructionCrosshairVisible(this.instance, this.isObstructionIndicatorVisible);
	}
	
	public void preRenderCrosshair(GuiGraphicsExtractor guiGraphics) {
		if (this.isCrosshairDynamic || this.isObstructionCrosshairVisible) {
			this.setupPoseStack(guiGraphics.pose());
		}
	}
	
	public void postRenderCrosshair(GuiGraphicsExtractor guiGraphics) {
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
	
	private void setupPoseStack(Matrix3x2fStack poseStack) {
		if (this.crosshairOffset != null) {
			poseStack.pushMatrix();
			poseStack.translate(this.crosshairOffset.x(), -this.crosshairOffset.y());
		}
	}
	
	private void resetPoseStack(Matrix3x2fStack poseStack) {
		if (this.crosshairOffset != null) {
			poseStack.popMatrix();
		}
	}
	
	private void renderObstructionCrosshair(GuiGraphicsExtractor guiGraphics) {
		this.renderCustomCrosshair(guiGraphics, OBSTRUCTED_CROSSHAIR_SPRITE, RenderPipelines.CROSSHAIR);
		this.renderCustomCrosshair(guiGraphics, OBSTRUCTED_CROSSHAIR_CROSS_SPRITE, RenderPipelines.GUI_TEXTURED);
	}
	
	private void renderObstructionIndicator(GuiGraphicsExtractor guiGraphics) {
		this.renderCustomCrosshair(guiGraphics, OBSTRUCTION_INDICATOR_SPRITE, RenderPipelines.CROSSHAIR);
	}
	
	private void renderCustomCrosshair(GuiGraphicsExtractor guiGraphics, Identifier sprite, RenderPipeline renderPipeline) {
		var minecraft = Minecraft.getInstance();
		if (minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || ((GuiAccessor) minecraft.gui).invokeCanRenderCrosshairForSpectator(minecraft.hitResult)) {
			guiGraphics.blitSprite(renderPipeline, sprite, (guiGraphics.guiWidth() - 15) / 2, (guiGraphics.guiHeight() - 15) / 2, 15, 15);
		}
	}
	
	private static Vec2f computeCrosshairOffset(
		IShoulderSurfing instance,
		Camera camera,
		Matrix4fc modelViewMatrix,
		Matrix4f projectionMatrix,
		boolean isCrosshairDynamic,
		float partialTick
	) {
		var player = Minecraft.getInstance().player;
		if (!instance.isShoulderSurfing() || player == null) {
			return null;
		}
		var objectPickerConfig = Config.CLIENT.getObjectPickerConfig();
		var interactionRangeOverride = objectPickerConfig.isCustomRaytraceDistanceEnabled()
			? objectPickerConfig.getCustomRaytraceDistance()
			: 0;
		// Trace primary crosshair
		var pickContextBuilder = new PickContext.Builder(camera);
		if (isCrosshairDynamic) {
			pickContextBuilder.dynamicTrace();
		}
		var pickContext = pickContextBuilder.build();
		var hitResult = instance.getObjectPicker().pick(pickContext, interactionRangeOverride, partialTick, player);
		var position = hitResult.getLocation();
		// Trace obstruction crosshair
		if (!isCrosshairDynamic) {
			pickContext = pickContextBuilder.obstructionTrace(position).build();
			hitResult = instance.getObjectPicker().pick(pickContext, interactionRangeOverride, partialTick, player);
			position = hitResult.getLocation();
		}
		var projected = project2D(position.subtract(camera.position()), modelViewMatrix, projectionMatrix);
		Vec2f crosshairOffset = null;
		if (projected != null) {
			var window = Minecraft.getInstance().getWindow();
			var screenSize = new Vec2f(window.getScreenWidth(), window.getScreenHeight());
			var center = screenSize.divide(2);
			var crosshairConfig = Config.CLIENT.getCrosshairConfig();
			var maxDistanceToObstruction = crosshairConfig.getObstructionIndicatorMaxDistanceToObstruction();
			if (
				isCrosshairDynamic
					|| !crosshairConfig.isObstructionIndicatorEnabled()
					|| maxDistanceToObstruction <= 0
					|| position.distanceToSqr(player.getEyePosition()) <= maxDistanceToObstruction * maxDistanceToObstruction
			) {
				crosshairOffset = projected.subtract(center).divide((float) window.getGuiScale());
			}
		}
		return crosshairOffset;
	}
	
	public void resetState() {
		this.init();
	}
	
	private static boolean computeIsCrosshairDynamic(IShoulderSurfing instance, @Nullable Entity cameraEntity) {
		if (!instance.isShoulderSurfing()) {
			return false;
		}
		return switch (Config.CLIENT.getCrosshairConfig().getCrosshairType()) {
			case ADAPTIVE -> instance.isAiming();
			case DYNAMIC,
			     DYNAMIC_WITH_1PP -> cameraEntity instanceof Player player && !player.isScoping();
			default -> false;
		};
	}
	
	private static boolean computeIsCrosshairVisible(
		IShoulderSurfing instance,
		@Nullable Vec2f crosshairOffset,
		boolean isCrosshairDynamic
	) {
		if (crosshairOffset == null && isCrosshairDynamic) {
			return false;
		}
		var hitResult = Minecraft.getInstance().hitResult;
		return switch (Config.CLIENT.getCrosshairConfig().getCrosshairVisibility(Perspective.current())) {
			case NEVER -> false;
			case WHEN_AIMING -> instance.isAiming();
			case WHEN_IN_RANGE -> hitResult != null && hitResult.getType() != HitResult.Type.MISS;
			case WHEN_AIMING_OR_IN_RANGE -> instance.isAiming() || hitResult != null && hitResult.getType() != HitResult.Type.MISS;
			default -> true;
		};
	}
	
	private static boolean computeIsObstructionIndicatorVisible(
		IShoulderSurfing instance,
		@Nullable Vec2f crosshairOffset,
		boolean isCrosshairDynamic
	) {
		if (!instance.isShoulderSurfing()) {
			return false;
		}
		if (crosshairOffset == null || !Config.CLIENT.getCrosshairConfig().isObstructionIndicatorEnabled()) {
			return false;
		}
		if (isCrosshairDynamic) {
			return false;
		}
		if (!instance.isAiming() && Config.CLIENT.getCrosshairConfig().isObstructionIndicatorOnlyShownWhenAiming()) {
			return false;
		}
		var minDistanceToCrosshair = Config.CLIENT.getCrosshairConfig().getObstructionIndicatorMinDistanceToCrosshair();
		return crosshairOffset.lengthSquared() >= minDistanceToCrosshair * minDistanceToCrosshair;
	}
	
	private static boolean computeIsObstructionCrosshairVisible(IShoulderSurfing instance, boolean isObstructionIndicatorVisible) {
		if (!instance.isShoulderSurfing()) {
			return false;
		}
		if (Minecraft.getInstance().debugEntries.isCurrentlyEnabled(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR)) {
			return false;
		}
		return instance.isAiming() && isObstructionIndicatorVisible;
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
	
	private static @Nullable Vec2f project2D(Vec3 position, Matrix4fc modelView, Matrix4f projection) {
		var window = Minecraft.getInstance().getWindow();
		var screenWidth = window.getScreenWidth();
		var screenHeight = window.getScreenHeight();
		if (screenWidth == 0 || screenHeight == 0) {
			return null;
		}
		var vec = new Vector4f((float) position.x(), (float) position.y(), (float) position.z(), 1.0F);
		vec.mul(modelView);
		vec.mul(projection);
		if (vec.w() == 0.0F) {
			return null;
		}
		var w = (1.0F / vec.w()) * 0.5F;
		var x = (vec.x() * w + 0.5F) * screenWidth;
		var y = (vec.y() * w + 0.5F) * screenHeight;
		var z = vec.z() * w + 0.5F;
		vec.set(x, y, z, w);
		if (Float.isInfinite(x) || Float.isInfinite(y) || Float.isNaN(x) || Float.isNaN(y) || w < 0.0F) {
			return null;
		}
		return new Vec2f(x, y);
	}
}
