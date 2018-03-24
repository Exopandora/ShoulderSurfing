package com.teamderpy.shouldersurfing.asm.transformer;

import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.Mappings;

public class TransformerThirdPersonMode implements ITransformer
{
	@Override
	public String getClassName()
	{
		return "Minecraft";
	}
	
	@Override
	public String getMethodName()
	{
		return "Minecraft#processKeyBinds";
	}
	
	@Override
	public InsnList getSearchList(Mappings mappings)
	{
		InsnList searchList = new InsnList();
		
		searchList.add(new FieldInsnNode(GETFIELD, mappings.getClassPath("GameSettings"), mappings.getFieldOrMethod("GameSettings#thirdPersonView"), mappings.getDescriptor("GameSettings#thirdPersonView")));
		searchList.add(new InsnNode(ICONST_2));
		
		return searchList;
	}
	
	@Override
	public InsnList getInjcetionList(Mappings mappings)
	{
		InsnList hackCode = new InsnList();
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getMax3ppId", "()I", false));
		
		return hackCode;
	}
	
	@Override
	public void transform(MethodNode method, InsnList hackCode, int offset)
	{
		method.instructions.set(method.instructions.get(offset), hackCode.getFirst());
	}
}
