package com.teamderpy.shouldersurfing.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShoulderPlugin implements IFMLLoadingPlugin
{
	@Override
	public String[] getASMTransformerClass()
	{
		return new String[]
		{
			"com.teamderpy.shouldersurfing.asm.transformers.TransformerCameraDistanceCheck",
			"com.teamderpy.shouldersurfing.asm.transformers.TransformerCameraOrientation",
			"com.teamderpy.shouldersurfing.asm.transformers.TransformerDistanceCheck",
			"com.teamderpy.shouldersurfing.asm.transformers.TransformerPositionEyes",
			"com.teamderpy.shouldersurfing.asm.transformers.TransformerRayTrace",
			"com.teamderpy.shouldersurfing.asm.transformers.TransformerRayTraceProjection",
			"com.teamderpy.shouldersurfing.asm.transformers.TransformerRenderAttackIndicator",
			"com.teamderpy.shouldersurfing.asm.transformers.TransformerRenderCrosshair",
			"com.teamderpy.shouldersurfing.asm.transformers.TransformerThirdPersonMode"
		};
	}
	
	@Override
	public String getModContainerClass()
	{
		return null;
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data)
	{
		
	}
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}
