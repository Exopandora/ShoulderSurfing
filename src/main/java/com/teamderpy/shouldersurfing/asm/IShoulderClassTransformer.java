package com.teamderpy.shouldersurfing.asm;

import org.objectweb.asm.ClassWriter;

public interface IShoulderClassTransformer extends IShoulderTransformer
{
	void transform(Mappings mappings, boolean obf, ClassWriter writer);
}