package com.teamderpy.shouldersurfing.asm.transformers;

import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ATransformerOrientCamera extends ShoulderTransformer
{
	@Override
	public String getClassId()
	{
		return "EntityRenderer";
	}
	
	@Override
	public String getMethodId()
	{
		return "EntityRenderer#orientCamera";
	}
}
