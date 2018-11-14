package com.teamderpy.shouldersurfing.asm.transformer.method;

import com.teamderpy.shouldersurfing.asm.transformer.IMethodTransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ATransformerAttackIndicator implements IMethodTransformer
{
	@Override
	public String getClassName()
	{
		return "GuiIngame";
	}
	
	@Override
	public String getMethodName()
	{
		return "GuiIngame#renderAttackIndicator";
	}
}
