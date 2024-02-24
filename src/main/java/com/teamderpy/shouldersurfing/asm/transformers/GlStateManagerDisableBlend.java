package com.teamderpy.shouldersurfing.asm.transformers;

import static com.esotericsoftware.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.IShoulderMethodTransformer;
import com.teamderpy.shouldersurfing.asm.Mappings;

public class GlStateManagerDisableBlend implements IShoulderMethodTransformer
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
//		if(InjectionDelegation.GlStateManager_disableBlend()) return;
		
		LabelNode label = new LabelNode();
		AbstractInsnNode node = method.instructions.get(offset);
		method.instructions.insertBefore(node, label);
		
		method.instructions.insert(new JumpInsnNode(IFNE, label));
		method.instructions.insert(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "GlStateManager_disableBlend", "()Z", false));
	}
	
	@Override
	public String getClassId()
	{
		return "GlStateManager";
	}
	
	@Override
	public String getMethodId()
	{
		return "GlStateManager#disableBlend";
	}
}
