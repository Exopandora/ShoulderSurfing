package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.IShoulderMethodTransformer;
import com.teamderpy.shouldersurfing.asm.Mappings;

public class ItemRayTraceBlocks implements IShoulderMethodTransformer
{
	@Override
	public InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKEVIRTUAL, mappings.map("World", obf), mappings.map("World#rayTraceBlocks", obf), mappings.desc("World#rayTraceBlocks", obf), false));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// world.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false)
		// ->
		// InjectionDelegation.Item_rayTraceBlocks(world, vec3d, vec3d1, useLiquids, !useLiquids, false);
		
		MethodInsnNode instruction = new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "Item_rayTraceBlocks", mappings.desc("InjectionDelegation#Item_rayTraceBlocks", obf), false);
		method.instructions.set(method.instructions.get(offset), instruction);
	}
	
	@Override
	public String getClassId()
	{
		return "Item";
	}
	
	@Override
	public String getMethodId()
	{
		return "Item#rayTrace";
	}
}
