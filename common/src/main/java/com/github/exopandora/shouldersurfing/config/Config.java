package com.github.exopandora.shouldersurfing.config;

import com.github.exopandora.shouldersurfing.api.client.config.IClientConfig;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemUseAnimation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class Config
{
	public static final ModConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;
	
	static
	{
		Pair<ClientConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = pair.getRight();
		CLIENT = pair.getLeft();
	}
	
	public static class ClientConfig implements IClientConfig
	{
		private final CameraConfig cameraConfig;
		private final PerspectiveConfig perspectiveConfig;
		private final PlayerConfig playerConfig;
		private final ObjectPickerConfig objectPickerConfig;
		private final CrosshairConfig crosshairConfig;
		private final AudioConfig audioConfig;
		private final IntegrationsConfig integrationsConfig;
		private boolean requiresSaving = false;
		
		public ClientConfig(ModConfigSpec.Builder builder)
		{
			this.cameraConfig = new CameraConfig(builder);
			this.perspectiveConfig = new PerspectiveConfig(builder);
			this.playerConfig = new PlayerConfig(builder);
			this.objectPickerConfig = new ObjectPickerConfig(builder);
			this.crosshairConfig = new CrosshairConfig(builder);
			this.audioConfig = new AudioConfig(builder);
			this.integrationsConfig = new IntegrationsConfig(builder);
		}
		
		@Override
		public CameraConfig getCameraConfig()
		{
			return this.cameraConfig;
		}
		
		@Override
		public PerspectiveConfig getPerspectiveConfig()
		{
			return this.perspectiveConfig;
		}
		
		@Override
		public PlayerConfig getPlayerConfig()
		{
			return this.playerConfig;
		}
		
		@Override
		public ObjectPickerConfig getObjectPickerConfig()
		{
			return this.objectPickerConfig;
		}
		
		@Override
		public CrosshairConfig getCrosshairConfig()
		{
			return this.crosshairConfig;
		}
		
		@Override
		public AudioConfig getAudioConfig()
		{
			return this.audioConfig;
		}
		
		@Override
		public IntegrationsConfig getIntegrationsConfig()
		{
			return this.integrationsConfig;
		}
		
		public boolean requiresSaving()
		{
			return this.requiresSaving;
		}
		
		public void save()
		{
			try
			{
				Config.CLIENT_SPEC.save();
				this.requiresSaving = false;
			}
			catch(Exception e)
			{
				// ignore
			}
		}
		
		protected <T> void set(ConfigValue<T> configValue, T value)
		{
			if(value != null && !value.equals(configValue.get()))
			{
				configValue.set(value);
				this.requiresSaving = true;
			}
		}
		
		protected static boolean isValidItemUseAnimation(Object id)
		{
			if(id == null)
			{
				return false;
			}
			
			for(ItemUseAnimation itemUseAnimation : ItemUseAnimation.values())
			{
				if(itemUseAnimation.getSerializedName().equals(id))
				{
					return true;
				}
			}
			
			return false;
		}
		
		protected static boolean isValidDataComponentId(Object id)
		{
			if(id == null)
			{
				return false;
			}
			
			Identifier location = Identifier.tryParse(id.toString());
			
			if(location == null)
			{
				return false;
			}
			
			return BuiltInRegistries.DATA_COMPONENT_TYPE.containsKey(location);
		}
		
		protected static boolean isValidDouble(Object number)
		{
			if(number != null)
			{
				try
				{
					Double.parseDouble(number.toString());
				}
				catch(NumberFormatException e)
				{
					return false;
				}
			}
			
			return true;
		}
		
		protected static boolean isValidItemWithSlot(Object id)
		{
			if(id == null)
			{
				return false;
			}
			
			String[] split = id.toString().split("@", 2);
			
			if(split.length < 2)
			{
				return false;
			}
			
			return Identifier.isValidNamespace(split[0]) && split[1] != null;
		}
		
		protected static boolean isValidDataComponentIdWithSlot(Object id)
		{
			if(id == null)
			{
				return false;
			}
			
			String[] split = id.toString().split("@", 2);
			
			if(split.length < 2)
			{
				return false;
			}
			
			return Identifier.isValidNamespace(split[0]) && isValidDataComponentId(split[1]);
		}
	}
	
	public static void onConfigReload()
	{
		Perspective currentPerspective = Perspective.current();
		PerspectiveConfig perspectiveConfig = Config.CLIENT.getPerspectiveConfig();
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(!currentPerspective.isEnabled(perspectiveConfig) && (currentPerspective != Perspective.FIRST_PERSON || !instance.isTemporaryFirstPerson()))
		{
			instance.changePerspective(currentPerspective.next(perspectiveConfig));
		}
		
		if(perspectiveConfig.doRememberLastPerspective())
		{
			perspectiveConfig.setDefaultPerspective(Perspective.current());
		}
	}
}
