package com.teamderpy.shouldersurfing.asm.transformers;

import static com.esotericsoftware.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.asm.IShoulderMethodTransformer;
import com.teamderpy.shouldersurfing.asm.Mappings;

public class GlStateManagerBlendFuncSeparate implements IShoulderMethodTransformer
{
	@Override
	public InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new InsnNode(RETURN));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// if(InjectionDelegation.GlStateManager_tryBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha, blendState)) return;
		
		LabelNode label = new LabelNode();
		AbstractInsnNode node = method.instructions.get(offset);
		method.instructions.insertBefore(node, label);
		
		AbstractInsnNode first = method.instructions.getFirst();
		method.instructions.insertBefore(first, new VarInsnNode(ILOAD, 0));
		method.instructions.insertBefore(first, new VarInsnNode(ILOAD, 1));
		method.instructions.insertBefore(first, new VarInsnNode(ILOAD, 2));
		method.instructions.insertBefore(first, new VarInsnNode(ILOAD, 3));
		method.instructions.insertBefore(first, new FieldInsnNode(GETSTATIC, mappings.map("GlStateManager", obf), mappings.map("GlStateManager#blendState", obf), mappings.desc("GlStateManager#blendState", obf)));
		method.instructions.insertBefore(first, new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "GlStateManager_tryBlendFuncSeparate", mappings.desc("InjectionDelegation#GlStateManager_tryBlendFuncSeparate", obf), false));
		method.instructions.insertBefore(first, new JumpInsnNode(IFNE, label));
	}
	
	@Override
	public String getClassId()
	{
		return "GlStateManager";
	}
	
	@Override
	public String getMethodId()
	{
		return "GlStateManager#tryBlendFuncSeparate";
	}
}
