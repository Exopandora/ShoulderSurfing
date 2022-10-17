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
		searchList.add(new MethodInsnNode(INVOKEVIRTUAL, mappings.map("World", obf), mappings.map("World#func_147447_a", obf), mappings.desc("World#func_147447_a", obf), false));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// p_77621_1_.func_147447_a(vec3, vec31, p_77621_3_, !p_77621_3_, false)
		// ->
		// InjectionDelegation.Item_getMovingObjectPositionFromPlayer(world, start, end, liquids, bool, bool);
		
		MethodInsnNode instruction = new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "Item_getMovingObjectPositionFromPlayer", mappings.desc("InjectionDelegation#Item_getMovingObjectPositionFromPlayer", obf), false);
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
		return "Item#getMovingObjectPositionFromPlayer";
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
