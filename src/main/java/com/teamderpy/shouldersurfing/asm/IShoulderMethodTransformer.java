package com.teamderpy.shouldersurfing.asm;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public interface IShoulderMethodTransformer extends IShoulderTransformer
{
	InsnList searchList(Mappings mappings, boolean obf);
	
	void transform(Mappings mappings, boolean obf, MethodNode method, int offset);
	
	String getMethodId();
	
	default boolean ignoreLabels()
	{
		return true;
	}
	
	default boolean ignoreLineNumber()
	{
		return true;
	}
}