package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.github.exopandora.shouldersurfing.api.config.IClientConfig;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfing;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemUseAnimation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class Config {
	public static final ModConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;
	
	static {
		var pair = new ModConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = pair.getRight();
		CLIENT = pair.getLeft();
	}
	
	public static class ClientConfig implements IClientConfig {
		private final CameraConfig cameraConfig;
		private final PerspectiveConfig perspectiveConfig;
		private final PlayerConfig playerConfig;
		private final ObjectPickerConfig objectPickerConfig;
		private final CrosshairConfig crosshairConfig;
		private final AudioConfig audioConfig;
		private final IntegrationsConfig integrationsConfig;
		private boolean requiresSaving = false;
		
		public ClientConfig(ModConfigSpec.Builder builder) {
			this.audioConfig = new AudioConfig(builder);
			this.cameraConfig = new CameraConfig(builder);
			this.crosshairConfig = new CrosshairConfig(builder);
			this.integrationsConfig = new IntegrationsConfig(builder);
			this.objectPickerConfig = new ObjectPickerConfig(builder);
			this.perspectiveConfig = new PerspectiveConfig(builder);
			this.playerConfig = new PlayerConfig(builder);
		}
		
		@Override
		public CameraConfig getCameraConfig() {
			return this.cameraConfig;
		}
		
		@Override
		public PerspectiveConfig getPerspectiveConfig() {
			return this.perspectiveConfig;
		}
		
		@Override
		public PlayerConfig getPlayerConfig() {
			return this.playerConfig;
		}
		
		@Override
		public ObjectPickerConfig getObjectPickerConfig() {
			return this.objectPickerConfig;
		}
		
		@Override
		public CrosshairConfig getCrosshairConfig() {
			return this.crosshairConfig;
		}
		
		@Override
		public AudioConfig getAudioConfig() {
			return this.audioConfig;
		}
		
		@Override
		public IntegrationsConfig getIntegrationsConfig() {
			return this.integrationsConfig;
		}
		
		public boolean requiresSaving() {
			return this.requiresSaving;
		}
		
		public void save() {
			try {
				Config.CLIENT_SPEC.save();
				this.requiresSaving = false;
			} catch (Exception e) {
				// ignore
			}
		}
		
		protected <T> void set(ConfigValue<T> configValue, T value) {
			if (value != null && !value.equals(configValue.get())) {
				configValue.set(value);
				this.requiresSaving = true;
			}
		}
		
		protected static boolean isValidItemUseAnimation(Object id) {
			if (id == null) {
				return false;
			}
			for (var itemUseAnimation : ItemUseAnimation.values()) {
				if (itemUseAnimation.getSerializedName().equals(id)) {
					return true;
				}
			}
			return false;
		}
		
		protected static boolean isValidDataComponentId(Object id) {
			if (id == null) {
				return false;
			}
			var location = Identifier.tryParse(id.toString());
			if (location == null) {
				return false;
			}
			return BuiltInRegistries.DATA_COMPONENT_TYPE.containsKey(location);
		}
		
		protected static boolean isValidDouble(Object number) {
			if (number != null) {
				try {
					Double.parseDouble(number.toString());
				} catch (NumberFormatException e) {
					return false;
				}
			}
			return true;
		}
		
		protected static boolean isValidItemWithSlot(Object id) {
			if (id == null) {
				return false;
			}
			var split = id.toString().split("@", 2);
			if (split.length < 2) {
				return false;
			}
			return Identifier.isValidNamespace(split[0]) && split[1] != null;
		}
		
		protected static boolean isValidDataComponentIdWithSlot(Object id) {
			if (id == null) {
				return false;
			}
			var split = id.toString().split("@", 2);
			if (split.length < 2) {
				return false;
			}
			return Identifier.isValidNamespace(split[0]) && isValidDataComponentId(split[1]);
		}
	}
	
	public static void onConfigReload() {
		var currentPerspective = Perspective.current();
		var perspectiveConfig = Config.CLIENT.getPerspectiveConfig();
		var instance = ShoulderSurfing.getInstance();
		if (!currentPerspective.isEnabled(perspectiveConfig) && (currentPerspective != Perspective.FIRST_PERSON || !instance.isTemporaryFirstPerson())) {
			instance.changePerspective(currentPerspective.next(perspectiveConfig));
		}
		if (perspectiveConfig.isPerspectivePersistent()) {
			perspectiveConfig.setDefaultPerspective(Perspective.current());
		}
	}
}
