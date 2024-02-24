package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.asm.IShoulderMethodTransformer;
import com.teamderpy.shouldersurfing.asm.Mappings;

public class GlStateManagerDepthMask implements IShoulderMethodTransformer
{
	@Override
	public InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new VarInsnNode(ILOAD, 0));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// flagIn = InjectionDelegation.GlStateManager_depthMask(flagIn);
		
		AbstractInsnNode node = method.instructions.get(offset);
		method.instructions.insertBefore(node, new VarInsnNode(ILOAD, 0));
		method.instructions.insertBefore(node, new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "GlStateManager_depthMask", "(Z)Z", false));
		method.instructions.insertBefore(node, new VarInsnNode(ISTORE, 0));
	}
	
	@Override
	public String getClassId()
	{
		return "GlStateManager";
	}
	
	@Override
	public String getMethodId()
	{
		return "GlStateManager#depthMask";
	}
}
