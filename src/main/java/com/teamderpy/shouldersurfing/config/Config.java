package com.teamderpy.shouldersurfing.config;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.teamderpy.shouldersurfing.event.ClientEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config
{
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;
	
	private static ModConfig MOD_CONFIG;
	private static CommentedFileConfig CONFIG_DATA;
	
	static
	{
		Pair<ClientConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = pair.getRight();
		CLIENT = pair.getLeft();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class ClientConfig
	{
		private final DoubleValue shoulderRotationYaw;
		private final DoubleValue shoulderZoomMod;
		private final BooleanValue unlimitedRotation;
		private final DoubleValue rotationMin;
		private final DoubleValue rotationMax;
		private final BooleanValue unlimitedZoom;
		private final DoubleValue zoomMin;
		private final DoubleValue zoomMax;
		private final BooleanValue show3ppCrosshair;
		private final BooleanValue show1ppCrosshair;
		private final ConfigValue<CrosshairVisibility> crosshairVisibility;
		private final BooleanValue showCrosshairFarther;
		private final BooleanValue keepCameraOutOfHead;
		private final BooleanValue attackIndicator;
		private final BooleanValue replaceDefaultPerspective;
		private final ConfigValue<Perspective> defaultPerspective;
		private final ConfigValue<CrosshairType> crosshairType;
		private final BooleanValue rememberLastPerspective;
		
		public ClientConfig(ForgeConfigSpec.Builder builder)
		{
			this.shoulderRotationYaw = builder
					.comment("Third person camera rotation")
					.translation("Rotation Offset")
					.defineInRange("rotation_offset", 0, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.shoulderZoomMod = builder
					.comment("Third person camera zoom")
					.translation("Zoom Offset")
					.defineInRange("zoom_offset", 0.7, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.unlimitedRotation = builder
					.comment("Whether or not rotation adjustment has limits")
					.translation("Unlimited Rotation")
					.define("unlimited_rotation", false);
			
			this.rotationMin = builder
					.comment("If rotation is limited this is the minimum amount")
					.translation("Rotation Minimum")
					.defineInRange("rotation_min", -60.0, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.rotationMax = builder
					.comment("If rotation is limited this is the maximum amount")
					.translation("Rotation Maximum")
					.defineInRange("rotation_max", 60.0, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.unlimitedZoom = builder
					.comment("Whether or not zoom adjustment has limits")
					.translation("Unlimited Zoom")
					.define("unlimited_zoom", false);
			
			this.zoomMin = builder
					.comment("If zoom is limited this is the minimum amount")
					.translation("Zoom Minimum")
					.defineInRange("zoom_min", 0.3, -Double.MAX_VALUE, Double.MAX_VALUE);
			
			this.zoomMax = builder
					.comment("If zoom is limited this is the maximum amount")
					.translation("Zoom Maximum")
					.defineInRange("zoom_max", 2.0, -Double.MAX_VALUE, Double.MAX_VALUE);
						
			this.show3ppCrosshair = builder
					.comment("Enable or disable the crosshair in third person")
					.translation("Third Person Crosshair")
					.define("3pp_crosshair", true);
			
			this.show1ppCrosshair = builder
					.comment("Enable or disable the crosshair in first person")
					.translation("First Person Crosshair")
					.define("1pp_crosshair", true);
			
			this.crosshairVisibility = builder
					.comment("Crosshair visibility in 3PP")
					.translation("Crosshair Visibility")
					.defineEnum("crosshair_visibility", CrosshairVisibility.ALWAYS, CrosshairVisibility.values());
			
			this.showCrosshairFarther = builder
					.comment("Whether or not to show the crosshairs farther than normal")
					.translation("Show Crosshair Farther")
					.define("show_crosshair_farther", true);
			
			this.keepCameraOutOfHead = builder
					.comment("Whether or not to hide the player model if the camera gets too close to it")
					.translation("Keep Camera Out Of Head")
					.define("keep_camera_out_of_head", true);
			
			this.attackIndicator = builder
					.comment("Enable or disable the attack indicator in third person")
					.translation("Third Person Attack Indicator")
					.define("third_person_attack_indicator", true);
			
			this.replaceDefaultPerspective = builder
					.comment("Whether or not to replace the default third person perspective")
					.translation("Replace Default Perspective")
					.define("replace_default_perspective", false);
			
			this.defaultPerspective = builder
					.comment("The default perspective when you load the game")
					.translation("Default Perspective")
					.defineEnum("default_perspective", Perspective.SHOULDER_SURFING, Perspective.values());
			
			this.crosshairType = builder
					.comment("Crosshair type to use in 3PP")
					.translation("Crosshair type")
					.defineEnum("crosshair_type", CrosshairType.ADAPTIVE, CrosshairType.values());
			
			this.rememberLastPerspective = builder
					.comment("Whether or not to remember the last perspective used")
					.translation("Remember Last Perspective")
					.define("remember_last_perspective", true);
		}
		
		@OnlyIn(Dist.CLIENT)
		public static enum CrosshairType
		{
			ADAPTIVE,
			DYNAMIC,
			STATIC,
			STATIC_WITH_1PP;
			
			public boolean isDynamic()
			{
				if(this == CrosshairType.ADAPTIVE)
				{
					return ClientEventHandler.isHoldingSpecialItem();
				}
				else if(this == CrosshairType.DYNAMIC)
				{
					return true;
				}
				
				return false;
			}
			
			public boolean doSwitchPerspective()
			{
				if(this == CrosshairType.STATIC_WITH_1PP)
				{
					return ClientEventHandler.isHoldingSpecialItem();
				}
				
				return false;
			}
		}
		
		@OnlyIn(Dist.CLIENT)
		public static enum Perspective
		{
			FIRST_PERSON,
			THIRD_PERSON,
			FRONT_THIRD_PERSON,
			SHOULDER_SURFING;
			
			public int getPerspectiveId()
			{
				if(this == Perspective.SHOULDER_SURFING)
				{
					return Config.CLIENT.getShoulderSurfing3ppId();
				}
				
				return this.ordinal();
			}
			
			public static Perspective of(int id)
			{
				if(id == Perspective.SHOULDER_SURFING.getPerspectiveId())
				{
					return Perspective.SHOULDER_SURFING;
				}
				else if(id >= 0 && id <= 2)
				{
					return Perspective.values()[id];
				}
				
				return Perspective.FIRST_PERSON;
			}
		}
		
		@OnlyIn(Dist.CLIENT)
		public static enum CrosshairVisibility
		{
			ALWAYS,
			WHEN_AIMING,
			WHEN_IN_RANGE,
			WHEN_AIMING_OR_IN_RANGE;
			
			public boolean doRender()
			{
				if(this == CrosshairVisibility.WHEN_AIMING)
				{
					return ClientEventHandler.isAiming;
				}
				else if(this == CrosshairVisibility.WHEN_IN_RANGE)
				{
					return Minecraft.getInstance().objectMouseOver != null && !Minecraft.getInstance().objectMouseOver.getType().equals(RayTraceResult.Type.MISS);
				}
				else if(this == CrosshairVisibility.WHEN_AIMING_OR_IN_RANGE)
				{
					return CrosshairVisibility.WHEN_IN_RANGE.doRender() || CrosshairVisibility.WHEN_AIMING.doRender();
				}
				
				return true;
			}
		}
		
		public double getShoulderRotationYaw()
		{
			return this.shoulderRotationYaw.get();
		}
		
		public void setShoulderRotationYaw(double yaw)
		{
			Config.set(this.shoulderRotationYaw, yaw);
		}
		
		public double getShoulderZoomMod()
		{
			return this.shoulderZoomMod.get();
		}
		
		public void setShoulderZoomMod(double zoomMod)
		{
			Config.set(this.shoulderZoomMod, zoomMod);
		}
		
		public boolean isRotationUnlimited()
		{
			return this.unlimitedRotation.get();
		}
		
		public void setRotationUnlimited(boolean enabled)
		{
			Config.set(this.unlimitedRotation, enabled);
		}
		
		public double getRotationMin()
		{
			return this.rotationMin.get();
		}
		
		public void setRotationMin(double min)
		{
			Config.set(this.rotationMin, min);
		}
		
		public double getRotationMax()
		{
			return this.rotationMax.get();
		}
		
		public void setRotationMax(double max)
		{
			Config.set(this.rotationMax, max);
		}
		
		public boolean isZoomUnlimited()
		{
			return this.unlimitedZoom.get();
		}
		
		public void setZoomUnlimited(boolean enabled)
		{
			Config.set(this.unlimitedZoom, enabled);
		}
		
		public double getZoomMin()
		{
			return this.zoomMin.get();
		}
		
		public void setZoomMin(double min)
		{
			Config.set(this.zoomMin, min);
		}
		
		public double getZoomMax()
		{
			return this.zoomMax.get();
		}
		
		public void setZoomMax(double max)
		{
			Config.set(this.rotationMax, max);
		}
		
		public boolean show3ppCrosshair()
		{
			return this.show3ppCrosshair.get();
		}
		
		public void setShow3ppCrosshair(boolean enabled)
		{
			Config.set(this.show3ppCrosshair, enabled);
		}
		
		public boolean show1ppCrosshair()
		{
			return this.show1ppCrosshair.get();
		}
		
		public void setShow1ppCrosshair(boolean enabled)
		{
			Config.set(this.show1ppCrosshair, enabled);
		}
		
		public CrosshairVisibility getCrosshairVisibility()
		{
			return this.crosshairVisibility.get();
		}
		
		public void setCrosshairVisibility(CrosshairVisibility visibility)
		{
			Config.set(this.crosshairVisibility, visibility);
		}
		
		public boolean showCrosshairFarther()
		{
			return this.showCrosshairFarther.get();
		}
		
		public void setShowCrosshairFarther(boolean enabled)
		{
			Config.set(this.showCrosshairFarther, enabled);
		}
		
		public boolean keepCameraOutOfHead()
		{
			return this.keepCameraOutOfHead.get();
		}
		
		public void setKeepCameraOutOfHead(boolean enabled)
		{
			Config.set(this.keepCameraOutOfHead, enabled);
		}
		
		public boolean showAttackIndicator()
		{
			return this.attackIndicator.get();
		}
		
		public void setShowAttackIndicator(boolean enabled)
		{
			Config.set(this.attackIndicator, enabled);
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
		
		public int getShoulderSurfing3ppId()
		{
			if(this.replaceDefaultPerspective())
			{
				return 1;
			}
			
			return 3;
		}
		
		public void adjustCameraLeft()
		{
			if(this.isRotationUnlimited() || this.getShoulderRotationYaw() < this.getRotationMax())
			{
				this.setShoulderRotationYaw(this.getShoulderRotationYaw() + 0.5F);
			}
		}
		
		public void adjustCameraRight()
		{
			if(this.isRotationUnlimited() || this.getShoulderRotationYaw() > this.getRotationMin())
			{
				this.setShoulderRotationYaw(this.getShoulderRotationYaw() - 0.5F);
			}
		}
		
		public void adjustCameraIn()
		{
			if(this.isZoomUnlimited() || this.getShoulderZoomMod() < this.getZoomMax())
			{
				this.setShoulderZoomMod(this.getShoulderZoomMod() + 0.01F);
			}
		}
		
		public void adjustCameraOut()
		{
			if(this.isZoomUnlimited() || this.getShoulderZoomMod() > this.getZoomMin())
			{
				this.setShoulderZoomMod(this.getShoulderZoomMod() - 0.01F);
			}
		}
		
		public void swapShoulder()
		{
			this.setShoulderRotationYaw(-this.getShoulderRotationYaw());
		}
	}
	
	protected static <T> void set(ForgeConfigSpec.ConfigValue<T> configValue, T value)
	{
		if(value != null && !value.equals(configValue.get()))
		{
			Config.CONFIG_DATA.set(configValue.getPath(), value);
		}
	}
	
	@SubscribeEvent
	public static void configLoad(ModConfig.Loading event)
	{
		if(event.getConfig().getType().equals(Type.CLIENT))
		{
			Config.MOD_CONFIG = event.getConfig();
			Config.CONFIG_DATA = (CommentedFileConfig) Config.MOD_CONFIG.getConfigData();
		}
	}
	
	@SubscribeEvent
	public static void configReload(ModConfig.ConfigReloading event)
	{
		if(event.getConfig().getType().equals(Type.CLIENT) && Config.CONFIG_DATA != null)
		{
			Config.CONFIG_DATA.load();
			
			if(Minecraft.getInstance().gameSettings.thirdPersonView == 3 && Config.CLIENT.replaceDefaultPerspective())
			{
				Minecraft.getInstance().gameSettings.thirdPersonView = 1;
			}
			else if(Minecraft.getInstance().gameSettings.thirdPersonView == 1 && !Config.CLIENT.replaceDefaultPerspective())
			{
				Minecraft.getInstance().gameSettings.thirdPersonView = 3;
			}
		}
	}
}
