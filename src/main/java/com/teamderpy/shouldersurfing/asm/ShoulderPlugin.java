package com.teamderpy.shouldersurfing.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.0
 * @since 2012-12-30
 */
@SideOnly(Side.CLIENT)
public class ShoulderPlugin implements IFMLLoadingPlugin
{
	@Override
	public String[] getASMTransformerClass()
	{
		return new String[]{"com.teamderpy.shouldersurfing.asm.ShoulderTransformations"};
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
