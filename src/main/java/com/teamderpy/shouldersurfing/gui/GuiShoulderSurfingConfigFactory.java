package com.teamderpy.shouldersurfing.gui;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiShoulderSurfingConfigFactory implements IModGuiFactory
{
	@Override
	public void initialize(Minecraft minecraft){}
	
	/**
	 * Compatibility
	 */
	public Class<? extends GuiScreen> mainConfigGuiClass()
	{
		return GuiShoulderSurfingConfig.class;
	}
	
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	{
		return null;
	}
	
	@Override
	public boolean hasConfigGui()
	{
		return true;
	}
	
	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen)
	{
		return new GuiShoulderSurfingConfig(parentScreen);
	}
}
