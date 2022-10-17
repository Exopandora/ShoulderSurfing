package com.teamderpy.shouldersurfing.compatibility;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum EnumShaderCompatibility
{
	NONE,
	OLD,
	NEW;
}
