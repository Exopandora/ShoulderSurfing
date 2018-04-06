package com.teamderpy.shouldersurfing.asm.transformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ITransformer
{
	String getClassName();
	String getMethodName();
}
