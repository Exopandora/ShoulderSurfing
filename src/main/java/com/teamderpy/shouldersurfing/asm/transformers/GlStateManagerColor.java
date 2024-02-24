package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.asm.IShoulderMethodTransformer;
import com.teamderpy.shouldersurfing.asm.Mappings;

public class GlStateManagerColor implements IShoulderMethodTransformer
{
	@Override
	public InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new VarInsnNode(FLOAD, 0));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// colorAlpha = InjectionDelegation.GlStateManager_color(colorAlpha);
		
		AbstractInsnNode node = method.instructions.get(offset);
		method.instructions.insertBefore(node, new VarInsnNode(FLOAD, 3));
		method.instructions.insertBefore(node, new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "GlStateManager_color", "(F)F", false));
		method.instructions.insertBefore(node, new VarInsnNode(FSTORE, 3));
	}
	
	@Override
	public String getClassId()
	{
		return "GlStateManager";
	}
	
	@Override
	public String getMethodId()
	{
		return "GlStateManager#color";
	}
}
