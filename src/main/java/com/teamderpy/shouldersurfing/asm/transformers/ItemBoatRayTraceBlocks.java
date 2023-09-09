package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

public abstract class ItemBoatRayTraceBlocks extends ShoulderTransformer
{
	@Override
	protected InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKEVIRTUAL, mappings.map("World", obf), mappings.map("World#rayTraceBlocks2", obf), mappings.desc("World#rayTraceBlocks2", obf), false));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// world.rayTraceBlocks(vec3d, vec3d1, true);
		// ->
		// InjectionDelegation.ItemBoat_rayTraceBlocks(world, vec3d, vec3d1, useLiquids, !useLiquids, false);
		
		MethodInsnNode instruction = new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "ItemBoat_rayTraceBlocks", mappings.desc("InjectionDelegation#ItemBoat_rayTraceBlocks", obf), false);
		method.instructions.set(method.instructions.get(offset), instruction);
	}
	
	@Override
	protected String getClassId()
	{
		return "ItemBoat";
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
