package com.teamderpy.shouldersurfing.gui;

import com.teamderpy.shouldersurfing.ShoulderSurfing;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiShoulderSurfingConfig extends GuiConfig
{
	public GuiShoulderSurfingConfig(GuiScreen parent)
	{
		super(parent, new ConfigElement(ShoulderSurfing.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), ShoulderSurfing.MODID, false, false, "Shoulder Surfing");
	}
}
