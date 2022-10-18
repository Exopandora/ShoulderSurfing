package com.teamderpy.shouldersurfing.compatibility;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum EnumShaderCompatibility
{
	NONE,
	OLD,
	NEW;
}
