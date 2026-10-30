package com.github.exopandora.shouldersurfing.client.gui;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.gui.MapOverlayAnchor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;

public class MapOverlayRenderer {
	public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(ShoulderSurfingCommon.MOD_ID, "map_overlay");
	
	private static final Identifier MAP_BACKGROUND_CHECKERBOARD = Identifier.withDefaultNamespace("textures/map/map_background_checkerboard.png");
	private static final float MAP_SIZE = 142F;
	private static final float BACKGROUND_SCALE = MAP_SIZE / 128F;
	
	public static void extractMapOverlay(GuiGraphics graphics, DeltaTracker deltaTracker) {
		var instance = IShoulderSurfing.getInstance();
		if (!instance.isShoulderSurfing()) {
			return;
		}
		var guiConfig = instance.getClientConfig().getGuiConfig();
		if (!guiConfig.isMapOverlayEnabled()) {
			return;
		}
		var minecraft = Minecraft.getInstance();
		var cameraEntity = minecraft.getCameraEntity();
		if (!minecraft.options.hideGui && cameraEntity instanceof LivingEntity living) {
			var leftStack = living.getMainArm() == HumanoidArm.RIGHT ? living.getOffhandItem() : living.getMainHandItem();
			var rightStack = living.getMainArm() == HumanoidArm.RIGHT ? living.getMainHandItem() : living.getOffhandItem();
			var size = Mth.ceil(graphics.guiHeight() * (guiConfig.getMapOverlayScale() / 100F));
			var anchor = guiConfig.getMapOverlayAnchor();
			var y = switch (anchor) {
				case TOP -> Math.min(guiConfig.getMapOverlayInsetTop(), graphics.guiHeight() - size);
				case BOTTOM -> graphics.guiHeight() - Math.min(guiConfig.getMapOverlayInsetBottom() + size, graphics.guiHeight());
			};
			if (leftStack.has(DataComponents.MAP_ID)) {
				var mapId = leftStack.get(DataComponents.MAP_ID);
				var x = Math.min(guiConfig.getMapOverlayInsetLeft(), graphics.guiWidth() - size);
				extractMap(graphics, mapId, cameraEntity.level(), x, y, size);
			}
			if (rightStack.has(DataComponents.MAP_ID)) {
				var mapId = rightStack.get(DataComponents.MAP_ID);
				if (minecraft.isDemo()) {
					y += 15;
				}
				if (anchor == MapOverlayAnchor.TOP && minecraft.player != null) {
					var activeEffects = minecraft.player.getActiveEffects();
					for (var activeEffect : activeEffects) {
						if (activeEffect.getEffect().value().isBeneficial()) {
							y += 26;
							break;
						}
					}
					for (var activeEffect : activeEffects) {
						if (!activeEffect.getEffect().value().isBeneficial()) {
							y += 26;
							break;
						}
					}
				}
				if (y + size > graphics.guiHeight()) {
					size = graphics.guiHeight() - y;
					size -= switch (anchor) {
						case TOP -> guiConfig.getMapOverlayInsetTop();
						case BOTTOM -> guiConfig.getMapOverlayInsetBottom();
					};
				}
				var x = graphics.guiWidth() - Math.min(guiConfig.getMapOverlayInsetRight() + size, graphics.guiWidth());
				extractMap(graphics, mapId, cameraEntity.level(), x, y, size);
			}
		}
	}
	
	private static void extractMap(GuiGraphics graphics, MapId mapId, Level level, int x, int y, int size) {
		var savedData = MapItem.getSavedData(mapId, level);
		if (savedData != null) {
			graphics.pose().pushMatrix();
			graphics.pose().translate(x, y);
			graphics.pose().scale(size / MAP_SIZE, size / MAP_SIZE);
			
			graphics.pose().pushMatrix();
			graphics.pose().scale(BACKGROUND_SCALE, BACKGROUND_SCALE);
			graphics.blit(RenderPipelines.GUI_TEXTURED, MAP_BACKGROUND_CHECKERBOARD, 0, 0, 0, 0, 128, 128, 128, 128);
			graphics.pose().popMatrix();
			
			graphics.pose().translate(7, 7);
			var mapRenderState = new MapRenderState();
			Minecraft.getInstance().getMapRenderer().extractRenderState(mapId, savedData, mapRenderState);
			for (var decoration : mapRenderState.decorations) {
				decoration.renderOnFrame = true;
			}
			graphics.submitMapRenderState(mapRenderState);
			graphics.pose().popMatrix();
		}
	}
}
