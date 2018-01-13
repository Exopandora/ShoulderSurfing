package com.teamderpy.shouldersurfing.gui;

import java.util.Set;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

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
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
	{
		return null;
	}
}
