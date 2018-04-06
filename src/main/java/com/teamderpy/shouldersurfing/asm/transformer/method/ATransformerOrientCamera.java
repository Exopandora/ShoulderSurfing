package com.teamderpy.shouldersurfing.asm.transformer.method;

import com.teamderpy.shouldersurfing.asm.transformer.IMethodTransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ATransformerOrientCamera implements IMethodTransformer
{
	@Override
	public String getClassName()
	{
		return "EntityRenderer";
	}
	
	@Override
	public String getMethodName()
	{
		return "EntityRenderer#orientCamera";
	}
}
