package com.teamderpy.shouldersurfing.gui;

import com.teamderpy.shouldersurfing.ShoulderSurfing;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

@SideOnly(Side.CLIENT)
public class GuiShoulderSurfingConfig extends GuiConfig
{
	public GuiShoulderSurfingConfig(GuiScreen parent)
	{
		super(parent, new ConfigElement(ShoulderSurfing.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), ShoulderSurfing.MODID, false, false, "Shoulder Surfing");
	}
}
