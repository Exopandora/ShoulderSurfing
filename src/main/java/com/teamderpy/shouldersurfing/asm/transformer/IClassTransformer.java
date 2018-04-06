package com.teamderpy.shouldersurfing.asm.transformer;

import org.objectweb.asm.ClassWriter;

import com.teamderpy.shouldersurfing.asm.Mappings;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IClassTransformer extends ITransformer
{
	void transform(ClassWriter writer, Mappings mappings);
}
