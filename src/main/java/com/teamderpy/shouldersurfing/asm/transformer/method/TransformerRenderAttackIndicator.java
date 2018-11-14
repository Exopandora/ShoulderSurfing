package com.teamderpy.shouldersurfing.asm.transformer.method;

import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IFEQ;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.Mappings;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransformerRenderAttackIndicator extends ATransformerAttackIndicator
{
	@Override
	public InsnList getSearchList(Mappings mappings)
	{
		InsnList searchList = new InsnList();
		
		searchList.add(new FieldInsnNode(GETFIELD, mappings.getClassPath("GameSettings"), mappings.getFieldOrMethod("GameSettings#attackIndicator"), mappings.getDescriptor("GameSettings#attackIndicator")));
		searchList.add(new InsnNode(ICONST_1));
		
		return searchList;
	}
	
	@Override
	public void transform(MethodNode method, Mappings mappings, int offset)
	{
		InsnList hackCode = new InsnList();
		hackCode.add(new FieldInsnNode(GETSTATIC, "com/teamderpy/shouldersurfing/ShoulderSettings", "ENABLE_ATTACK_INDICATOR", "Z"));
		
		JumpInsnNode jump = (JumpInsnNode) method.instructions.get(offset + 1);
		hackCode.add(new JumpInsnNode(IFEQ, jump.label));
		
		method.instructions.insert(jump, hackCode);
	}
}
