package com.teamderpy.shouldersurfing.asm.transformer;

import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DMUL;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.F2D;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FNEG;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.transformer.abstr.TransformerOrientCamera;

public class TransformerDistanceCheck extends TransformerOrientCamera
{
	@Override
	public InsnList getSearchList(Mappings mappings)
	{
		InsnList searchList = new InsnList();
		
		searchList.add(new InsnNode(FNEG));
		searchList.add(new InsnNode(F2D));
		searchList.add(new VarInsnNode(DLOAD, 10));
		searchList.add(new InsnNode(DMUL));
		searchList.add(new VarInsnNode(DSTORE, 18));
		
		return searchList;
	}
	
	@Override
	public InsnList getInjcetionList(Mappings mappings)
	{
		InsnList hackCode = new InsnList();
		
		hackCode.add(new VarInsnNode(DLOAD, 10));
		hackCode.add(new VarInsnNode(FLOAD, 12));
		hackCode.add(new VarInsnNode(DLOAD, 4));
		hackCode.add(new VarInsnNode(DLOAD, 6));
		hackCode.add(new VarInsnNode(DLOAD, 8));
		hackCode.add(new VarInsnNode(DLOAD, 14));
		hackCode.add(new VarInsnNode(DLOAD, 18));
		hackCode.add(new VarInsnNode(DLOAD, 16));
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "checkDistance", "(DFDDDDDD)D", false));
		hackCode.add(new VarInsnNode(DSTORE, 10));
		
		return hackCode;
	}
	
	@Override
	public void transform(MethodNode method, InsnList hackCode, int offset)
	{
		method.instructions.insert(method.instructions.get(offset + 1), hackCode);
	}
}
