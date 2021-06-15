package com.teamderpy.shouldersurfing.asm.transformers;

import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ATransformerAttackIndicator extends ShoulderTransformer
{
	@Override
	public String getClassId()
	{
		return "GuiIngame";
	}
	
	@Override
	public String getMethodId()
	{
		return "GuiIngame#renderAttackIndicator";
	}
}
