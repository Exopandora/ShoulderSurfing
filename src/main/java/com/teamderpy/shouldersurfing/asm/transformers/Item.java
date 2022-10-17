package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

public class Item extends ShoulderTransformer
{
	@Override
	protected InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKEVIRTUAL, mappings.map("World", obf), mappings.map("World#rayTraceBlocks", obf), mappings.desc("World#rayTraceBlocks", obf), false));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// world.rayTrace(vec3d, vec3d1, useLiquids, !useLiquids, false)
		// ->
		// InjectionDelegation.Item_rayTrace(world, vec3d, vec3d1, useLiquids, !useLiquids, false);
		
		MethodInsnNode instruction = new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "Item_rayTrace", mappings.desc("InjectionDelegation#Item_rayTrace", obf), false);
		method.instructions.set(method.instructions.get(offset), instruction);
	}
	
	@Override
	protected String getClassId()
	{
		return "Item";
	}
	
	@Override
	protected String getMethodId()
	{
		return "Item#rayTrace";
	}
	
	@Override
	protected boolean hasMethodTransformer()
	{
		return true;
	}
	
	@Override
	protected boolean hasClassTransformer()
	{
		return false;
	}
}
