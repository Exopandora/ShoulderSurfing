package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.gui.MapOverlayAnchor;
import com.github.exopandora.shouldersurfing.api.config.IGuiConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

public class GuiConfig implements IGuiConfig {
	private final BooleanValue isMapOverlayEnabled;
	private final EnumValue<MapOverlayAnchor> mapOverlayAnchor;
	private final IntValue mapOverlayScale;
	private final IntValue mapOverlayInsetTop;
	private final IntValue mapOverlayInsetRight;
	private final IntValue mapOverlayInsetBottom;
	private final IntValue mapOverlayInsetLeft;
	
	protected GuiConfig(ModConfigSpec.Builder builder) {
		builder.push("gui");
		builder.push("maps");
		
		this.isMapOverlayEnabled = builder
			.comment("Whether to render map previews in the hud when a map item is held.")
			.translation(MOD_ID + ".configuration.gui.maps.map_item_overlay_enabled")
			.define("map_item_overlay_enabled", true);
		
		this.mapOverlayAnchor = builder
			.comment("The directional anchor to use for map item overlay rendering.")
			.translation(MOD_ID + ".configuration.gui.maps.map_item_overlay_anchor")
			.defineEnum("map_item_overlay_anchor", MapOverlayAnchor.BOTTOM, MapOverlayAnchor.values());
		
		this.mapOverlayScale = builder
			.comment("The scale to use for map item overlay rendering.")
			.translation(MOD_ID + ".configuration.gui.maps.map_item_overlay_scale")
			.defineInRange("map_item_overlay_scale", 40, 5, 100);
		
		builder.push("insets");
		
		this.mapOverlayInsetTop = builder
			.comment("The inset from the top of the screen.")
			.translation(MOD_ID + ".configuration.gui.maps.map_item_overlay.insets.top")
			.defineInRange("top", 1, 0, 500);
		
		this.mapOverlayInsetRight = builder
			.comment("The inset from the right of the screen.")
			.translation(MOD_ID + ".configuration.gui.maps.map_item_overlay.insets.right")
			.defineInRange("right", 1, 0, 500);
		
		this.mapOverlayInsetBottom = builder
			.comment("The inset from the bottom of the screen.")
			.translation(MOD_ID + ".configuration.gui.maps.map_item_overlay.insets.bottom")
			.defineInRange("bottom", 1, 0, 500);
		
		this.mapOverlayInsetLeft = builder
			.comment("The inset from the left of the screen.")
			.translation(MOD_ID + ".configuration.gui.maps.map_item_overlay.insets.left")
			.defineInRange("left", 1, 0, 500);
		
		builder.pop();
		builder.pop();
		builder.pop();
	}
	
	@Override
	public boolean isMapOverlayEnabled() {
		return this.isMapOverlayEnabled.get();
	}
	
	@Override
	public MapOverlayAnchor getMapOverlayAnchor() {
		return this.mapOverlayAnchor.get();
	}
	
	@Override
	public int getMapOverlayScale() {
		return this.mapOverlayScale.get();
	}
	
	@Override
	public int getMapOverlayInsetTop() {
		return this.mapOverlayInsetTop.get();
	}
	
	@Override
	public int getMapOverlayInsetRight() {
		return this.mapOverlayInsetRight.get();
	}
	
	@Override
	public int getMapOverlayInsetBottom() {
		return this.mapOverlayInsetBottom.get();
	}
	
	@Override
	public int getMapOverlayInsetLeft() {
		return this.mapOverlayInsetLeft.get();
	}
}
