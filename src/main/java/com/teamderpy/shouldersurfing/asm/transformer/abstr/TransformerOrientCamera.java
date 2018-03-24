package com.teamderpy.shouldersurfing.asm.transformer.abstr;

import com.teamderpy.shouldersurfing.asm.transformer.ITransformer;

public abstract class TransformerOrientCamera implements ITransformer
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
