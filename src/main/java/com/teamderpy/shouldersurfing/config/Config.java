package com.teamderpy.shouldersurfing.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraft.init.Items;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Config
{
	public static ClientConfig CLIENT;
	
	@SideOnly(Side.CLIENT)
	public static class ClientConfig
	{
		private DoubleValue offsetX;
		private DoubleValue offsetY;
		private DoubleValue offsetZ;
		
		private DoubleValue minOffsetX;
		private DoubleValue minOffsetY;
		private DoubleValue minOffsetZ;
		
		private DoubleValue maxOffsetX;
		private DoubleValue maxOffsetY;
		private DoubleValue maxOffsetZ;
		
		private BooleanValue unlimitedOffsetX;
		private BooleanValue unlimitedOffsetY;
		private BooleanValue unlimitedOffsetZ;
		
		private BooleanValue keepCameraOutOfHead;
		private BooleanValue replaceDefaultPerspective;
		private BooleanValue rememberLastPerspective;
		private BooleanValue limitPlayerReach;
		private DoubleValue cameraStepSize;
		private ConfigValue<Perspective> defaultPerspective;
		private BooleanValue centerCameraWhenClimbing;
		private DoubleValue cameraTransitionSpeed;
		private DoubleValue centerCameraWhenLookingDownAngle;
		
		private ConfigValue<CrosshairType> crosshairType;
		private DoubleValue customRaytraceDistance;
		private BooleanValue useCustomRaytraceDistance;
		private ConfigValue<List<String>> adaptiveCrosshairItems;
		private final Map<Perspective, ConfigValue<CrosshairVisibility>> crosshairVisibility = new HashMap<Perspective, ConfigValue<CrosshairVisibility>>();
		private BooleanValue compatibilityValkyrienSkiesCameraShipCollision;
		
		private final Configuration config;
		
		public ClientConfig(Configuration config)
		{
			this.config = config;
			this.config.load();
			this.sync();
		}
		
		public double getOffsetX()
		{
			return this.offsetX.get();
		}
		
		public void setOffsetX(double offsetX)
		{
			Config.set(this.offsetX, offsetX);
		}
		
		public double getOffsetY()
		{
			return this.offsetY.get();
		}
		
		public void setOffsetY(double offsetY)
		{
			Config.set(this.offsetY, offsetY);
		}
		
		public double getOffsetZ()
		{
			return this.offsetZ.get();
		}
		
		public void setOffsetZ(double offsetZ)
		{
			Config.set(this.offsetZ, offsetZ);
		}
		
		public double getMinOffsetX()
		{
			return this.minOffsetX.get();
		}
		
		public void setMinOffsetX(double minOffsetX)
		{
			Config.set(this.minOffsetX, minOffsetX);
		}
		
		public double getMinOffsetY()
		{
			return this.minOffsetY.get();
		}
		
		public void setMinOffsetY(double minOffsetY)
		{
			Config.set(this.minOffsetY, minOffsetY);
		}
		
		public double getMinOffsetZ()
		{
			return this.minOffsetZ.get();
		}
		
		public void setMinOffsetZ(double minOffsetZ)
		{
			Config.set(this.minOffsetZ, minOffsetZ);
		}
		
		public double getMaxOffsetX()
		{
			return this.maxOffsetX.get();
		}
		
		public void setMaxOffsetX(double maxOffsetX)
		{
			Config.set(this.maxOffsetX, maxOffsetX);
		}
		
		public double getMaxOffsetY()
		{
			return this.maxOffsetY.get();
		}
		
		public void setMaxOffsetY(double maxOffsetY)
		{
			Config.set(this.maxOffsetY, maxOffsetY);
		}
		
		public double getMaxOffsetZ()
		{
			return this.maxOffsetZ.get();
		}
		
		public void setMaxOffsetZ(double maxOffsetZ)
		{
			Config.set(this.maxOffsetZ, maxOffsetZ);
		}
		
		public boolean isUnlimitedOffsetX()
		{
			return this.unlimitedOffsetX.get();
		}
		
		public void setUnlimitedOffsetX(boolean unlimitedOffsetX)
		{
			Config.set(this.unlimitedOffsetX, unlimitedOffsetX);
		}
		
		public boolean isUnlimitedOffsetY()
		{
			return this.unlimitedOffsetY.get();
		}
		
		public void setUnlimitedOffsetY(boolean unlimitedOffsetY)
		{
			Config.set(this.unlimitedOffsetY, unlimitedOffsetY);
		}
		
		public boolean isUnlimitedOffsetZ()
		{
			return this.unlimitedOffsetZ.get();
		}
		
		public void setUnlimitedOffsetZ(boolean unlimitedOffsetZ)
		{
			Config.set(this.unlimitedOffsetZ, unlimitedOffsetZ);
		}
		
		public CrosshairVisibility getCrosshairVisibility(Perspective perspective)
		{
			return this.crosshairVisibility.get(perspective).get();
		}
		
		public void setCrosshairVisibility(Perspective perspective, CrosshairVisibility visibility)
		{
			Config.set(this.crosshairVisibility.get(perspective), visibility);
		}
		
		public boolean useCustomRaytraceDistance()
		{
			return this.useCustomRaytraceDistance.get();
		}
		
		public void setUseCustomRaytraceDistance(boolean useCustomRaytraceDistance)
		{
			Config.set(this.useCustomRaytraceDistance, useCustomRaytraceDistance);
		}
		
		public boolean keepCameraOutOfHead()
		{
			return this.keepCameraOutOfHead.get();
		}
		
		public void setKeepCameraOutOfHead(boolean enabled)
		{
			Config.set(this.keepCameraOutOfHead, enabled);
		}
		
		public boolean replaceDefaultPerspective()
		{
			return this.replaceDefaultPerspective.get();
		}
		
		public void setReplaceDefaultPerspective(boolean enabled)
		{
			Config.set(this.replaceDefaultPerspective, enabled);
		}
		
		public Perspective getDefaultPerspective()
		{
			return this.defaultPerspective.get();
		}
		
		public void setDefaultPerspective(Perspective perspective)
		{
			Config.set(this.defaultPerspective, perspective);
		}
		
		public CrosshairType getCrosshairType()
		{
			return this.crosshairType.get();
		}
		
		public void setCrosshairType(CrosshairType type)
		{
			Config.set(this.crosshairType, type);
		}
		
		public boolean doRememberLastPerspective()
		{
			return this.rememberLastPerspective.get();
		}
		
		public void setRememberLastPerspective(boolean enabled)
		{
			Config.set(this.rememberLastPerspective, enabled);
		}
		
		public double getCameraStepSize()
		{
			return this.cameraStepSize.get();
		}
		
		public void setCameraStepSize(double cameraStepSize)
		{
			Config.set(this.cameraStepSize, cameraStepSize);
		}
		
		public boolean doCenterCameraWhenClimbing()
		{
			return this.centerCameraWhenClimbing.get();
		}
		
		public void setCenterCameraWhenClimbing(boolean enabled)
		{
			Config.set(this.centerCameraWhenClimbing, enabled);
		}
		
		public double getCameraTransitionSpeed()
		{
			return this.cameraTransitionSpeed.get();
		}
		
		public void setCameraInterpolationSpeed(double cameraTransitionSpeed)
		{
			Config.set(this.cameraTransitionSpeed, cameraTransitionSpeed);
		}
		
		public double getCenterCameraWhenLookingDownAngle()
		{
			return this.centerCameraWhenLookingDownAngle.get();
		}
		
		public void setCenterCameraWhenLookingDown(double centerCameraWhenLookingDownAngle)
		{
			Config.set(this.centerCameraWhenLookingDownAngle, centerCameraWhenLookingDownAngle);
		}
		
		public double getCustomRaytraceDistance()
		{
			return this.customRaytraceDistance.get();
		}
		
		public void setCustomRaytraceDistance(double raytraceDistance)
		{
			Config.set(this.customRaytraceDistance, raytraceDistance);
		}
		
		public boolean limitPlayerReach()
		{
			return this.limitPlayerReach.get();
		}
		
		public void setLimitPlayerReach(boolean limitPlayerReach)
		{
			Config.set(this.limitPlayerReach, limitPlayerReach);
		}
		
		public List<String> getAdaptiveCrosshairItems()
		{
			return this.adaptiveCrosshairItems.get();
		}
		
		public boolean doCompatibilityValkyrienSkiesCameraShipCollision()
		{
			return this.compatibilityValkyrienSkiesCameraShipCollision.get();
		}
		
		public void setCompatibilityValkyrienSkiesCameraShipCollision(boolean enabled)
		{
			Config.set(this.compatibilityValkyrienSkiesCameraShipCollision, enabled);
		}
		
		public void adjustCameraLeft()
		{
			this.setOffsetX(this.addStep(this.getOffsetX(), this.getMaxOffsetX(), this.isUnlimitedOffsetX()));
		}
		
		public void adjustCameraRight()
		{
			this.setOffsetX(this.subStep(this.getOffsetX(), this.getMinOffsetX(), this.isUnlimitedOffsetX()));
		}
		
		public void adjustCameraUp()
		{
			this.setOffsetY(this.addStep(this.getOffsetY(), this.getMaxOffsetY(), this.isUnlimitedOffsetY()));
		}
		
		public void adjustCameraDown()
		{
			this.setOffsetY(this.subStep(this.getOffsetY(), this.getMinOffsetY(), this.isUnlimitedOffsetY()));
		}
		
		public void adjustCameraIn()
		{
			this.setOffsetZ(this.subStep(this.getOffsetZ(), this.getMinOffsetZ(), this.isUnlimitedOffsetZ()));
		}
		
		public void adjustCameraOut()
		{
			this.setOffsetZ(this.addStep(this.getOffsetZ(), this.getMaxOffsetZ(), this.isUnlimitedOffsetZ()));
		}
		
		private double addStep(double value, double max, boolean unlimited)
		{
			double next = value + this.getCameraStepSize();
			
			if(unlimited)
			{
				return next;
			}
			
			return Math.min(next, max);
		}
		
		private double subStep(double value, double min, boolean unlimited)
		{
			double next = value - this.getCameraStepSize();
			
			if(unlimited)
			{
				return next;
			}
			
			return Math.max(next, min);
		}
		
		public void swapShoulder()
		{
			this.setOffsetX(-this.getOffsetX());
		}
		
		private static String[] toStringArray(Enum<?>[] enums)
		{
			String[] stringArray = new String[enums.length];
			
			for(int x = 0; x < enums.length; x++)
			{
				stringArray[x] = enums[x].toString();
			}
			
			return stringArray;
		}
		
		public Configuration getConfig()
		{
			return this.config;
		}
		
		public void sync()
		{
			this.offsetX = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "x-offset", -0.75D, "Third person camera x-offset", -Double.MAX_VALUE, Double.MAX_VALUE));
			this.offsetY = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "y-offset", 0.0D, "Third person camera y-offset", -Double.MAX_VALUE, Double.MAX_VALUE));
			this.offsetZ = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "z-offset", 3.0D, "Third person camera z-offset", -Double.MAX_VALUE, Double.MAX_VALUE));
			
			this.minOffsetX = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "Minimum x-offset", -3.0D, "If x-offset is limited this is the minimum amount", -Double.MAX_VALUE, Double.MAX_VALUE));
			this.minOffsetY = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "Minimum y-offset", -1.0D, "If y-offset is limited this is the minimum amount", -Double.MAX_VALUE, Double.MAX_VALUE));
			this.minOffsetZ = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "Minimum z-offset", -3.0D, "If z-offset is limited this is the minimum amount", -Double.MAX_VALUE, Double.MAX_VALUE));
			
			this.maxOffsetX = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "Maximum x-offset", 3.0D, "If x-offset is limited this is the maximum amount", -Double.MAX_VALUE, Double.MAX_VALUE));
			this.maxOffsetY = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "Maximum y-offset", 1.5D, "If y-offset is limited this is the maximum amount", -Double.MAX_VALUE, Double.MAX_VALUE));
			this.maxOffsetZ = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "Maximum z-offset", 5.0D, "If z-offset is limited this is the maximum amount", -Double.MAX_VALUE, Double.MAX_VALUE));
			
			this.unlimitedOffsetX = new BooleanValue(this.config.get(Configuration.CATEGORY_GENERAL, "Unlimited x-offset", false, "Whether or not x-offset adjustment has limits"));
			this.unlimitedOffsetY = new BooleanValue(this.config.get(Configuration.CATEGORY_GENERAL, "Unlimited y-offset", false, "Whether or not y-offset adjustment has limits"));
			this.unlimitedOffsetZ = new BooleanValue(this.config.get(Configuration.CATEGORY_GENERAL, "Unlimited z-Offset", false, "Whether or not z-offset adjustment has limits"));
			
			this.keepCameraOutOfHead = new BooleanValue(this.config.get(Configuration.CATEGORY_GENERAL, "Keep Camera Out Of Head", true, "Whether or not to hide the player model if the camera gets too close to it"));
			this.defaultPerspective = new EnumValue<Perspective>(this.config.get(Configuration.CATEGORY_GENERAL, "Default Perspective", Perspective.SHOULDER_SURFING.toString(), "The default perspective when you load the game", ClientConfig.toStringArray(Perspective.values())), Perspective.class);
			this.rememberLastPerspective = new BooleanValue(this.config.get(Configuration.CATEGORY_GENERAL, "Remember Last Perspective", true, "Whether or not to remember the last perspective used"));
			this.replaceDefaultPerspective = new BooleanValue(this.config.get(Configuration.CATEGORY_GENERAL, "Replace Default Perspective", false, "Whether or not to replace the default third person perspective"));
			this.limitPlayerReach = new BooleanValue(this.config.get(Configuration.CATEGORY_GENERAL, "Limit player reach", true, "Whether or not to limit the player reach depending on the crosshair location (perspective offset)"));
			this.cameraStepSize = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "Camera step size", 0.025D, "Size of the camera adjustment per step", -Double.MAX_VALUE, Double.MAX_VALUE));
			this.centerCameraWhenClimbing = new BooleanValue(this.config.get(Configuration.CATEGORY_GENERAL, "Center camera when climbing", true, "Whether or not to temporarily center the camera when climbing"));
			this.cameraTransitionSpeed = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "Camera transition speed", 0.25D, "The speed at which the camera transitions between positions", 0.05D, 1.0D));
			this.centerCameraWhenLookingDownAngle = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "Center camera when looking down angle", 15D, "The angle at which the camera will be centered when looking down. Set to 0 to disable.", 0D, 90D));
			
			this.crosshairType = new EnumValue<CrosshairType>(this.config.get(Configuration.CATEGORY_GENERAL, "Crosshair type", CrosshairType.ADAPTIVE.toString(), "Crosshair type to use for shoulder surfing", ClientConfig.toStringArray(CrosshairType.values())), CrosshairType.class);
			this.customRaytraceDistance = new DoubleValue(this.config.get(Configuration.CATEGORY_GENERAL, "Custom Raytrace Distance", 400, "The raytrace distance used for the dynamic crosshair", 0, Double.MAX_VALUE));
			this.useCustomRaytraceDistance = new BooleanValue(this.config.get(Configuration.CATEGORY_GENERAL, "Use Custom Raytrace Distance", true, "Whether or not to use the custom raytrace distance used for the dynamic crosshair"));

			this.adaptiveCrosshairItems = new ListValue(this.config.get(Configuration.CATEGORY_GENERAL, "Adaptive Crosshair Items", new String[]
			{
				Items.SNOWBALL.getRegistryName().toString(),
				Items.EGG.getRegistryName().toString(),
				Items.EXPERIENCE_BOTTLE.getRegistryName().toString(),
				Items.ENDER_PEARL.getRegistryName().toString(),
				Items.SPLASH_POTION.getRegistryName().toString(),
				Items.FISHING_ROD.getRegistryName().toString(),
				Items.LINGERING_POTION.getRegistryName().toString()
			}));
			
			String[] visibilities = ClientConfig.toStringArray(CrosshairVisibility.values());
			
			for(Perspective perspective : Perspective.values())
			{
				this.crosshairVisibility.put(perspective, new EnumValue<CrosshairVisibility>(this.config.get(Configuration.CATEGORY_GENERAL, perspective.toString() + " Crosshair Visibility", perspective.getDefaultCrosshairVisibility().toString(), "Crosshair visibility for " + perspective.toString(), visibilities), CrosshairVisibility.class));
			}
			
			this.compatibilityValkyrienSkiesCameraShipCollision = new BooleanValue(this.config.get(Configuration.CATEGORY_GENERAL, "[Compat] [Valkyrien Skies] Camera Ship Collision", false, "Whether or not the camera can collide with valkyrien skies ships"));
			
			if(this.config.hasChanged())
			{
				this.config.save();
			}
		}
	}
	
	protected static <T> void set(ConfigValue<T> configValue, T value)
	{
		if(value != null && !value.equals(configValue.get()))
		{
			configValue.set(value);
			
			if(Config.CLIENT.getConfig().hasChanged())
			{
				Config.CLIENT.getConfig().save();
			}
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(Config.CLIENT.doRememberLastPerspective())
		{
			Config.CLIENT.setDefaultPerspective(Perspective.current());
		}
	}
	
	private static interface ConfigValue<T>
	{
		void set(T value);
		
		T get();
	}
	
	private static class EnumValue<T extends Enum<T>> implements ConfigValue<T>
	{
		private Property property;
		private T value;
		
		public EnumValue(Property property, Class<T> klass)
		{
			this.property = property;
			this.value = this.valueOf(klass, property.getString());
		}
		
		public void set(T value)
		{
			this.value = value;
			this.property.set(this.value.toString());
		}
		
		public T get()
		{
			return this.value;
		}
		
		private T valueOf(Class<T> klass, String value)
		{
			try
			{
				return Enum.valueOf(klass, value);
			}
			catch(Exception e)
			{
				return Enum.valueOf(klass, this.property.getDefault());
			}
		}
	}
	
	private static class BooleanValue implements ConfigValue<Boolean>
	{
		private Property property;
		private boolean value;
		
		public BooleanValue(Property property)
		{
			this.property = property;
			this.value = property.getBoolean();
		}
		
		public void set(Boolean value)
		{
			this.value = value;
			this.property.set(this.value);
		}
		
		public Boolean get()
		{
			return this.value;
		}
	}
	
	private static class DoubleValue implements ConfigValue<Double>
	{
		private Property property;
		private double value;
		
		public DoubleValue(Property property)
		{
			this.property = property;
			this.value = property.getDouble();
		}
		
		public void set(Double value)
		{
			this.value = value;
			this.property.set(this.value);
		}
		
		public Double get()
		{
			return this.value;
		}
	}
	
	private static class ListValue implements ConfigValue<List<String>>
	{
		private Property property;
		private List<String> value;
		
		public ListValue(Property property)
		{
			this.property = property;
			this.value = Lists.newArrayList(property.getStringList());
		}
		
		@Override
		public void set(List<String> value)
		{
			this.value = value;
			this.property.set(this.toArray(value));
		}
		
		@Override
		public List<String> get()
		{
			return this.value;
		}
		
		private String[] toArray(List<String> list)
		{
			String[] array = new String[list.size()];
			list.toArray(array);
			return array;
		}
	}
}
