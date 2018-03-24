package com.teamderpy.shouldersurfing.asm.transformer;

import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.transformer.abstr.TransformerOrientCamera;

public class TransformerCameraDistanceCheck extends TransformerOrientCamera
{
	@Override
	public InsnList getSearchList(Mappings mappings)
	{
		InsnList searchList = new InsnList();
		
		searchList.add(new VarInsnNode(DSTORE, 25));
		searchList.add(new VarInsnNode(DLOAD, 25));
		
		return searchList;
	}
	
	@Override
	public InsnList getInjcetionList(Mappings mappings)
	{
		InsnList hackCode = new InsnList();
		
		// net/minecraft/client/renderer/EntityRenderer.orientCamera:658
		// InjectionDelegation.verifyReverseBlockDist(d7);
		
		hackCode.add(new VarInsnNode(DLOAD, 25));
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "verifyReverseBlockDist", "(D)V", false));
		hackCode.add(new LabelNode(new Label()));
		
		return hackCode;
	}
	
	@Override
	public void transform(MethodNode method, InsnList hackCode, int offset)
	{
		method.instructions.insertBefore(method.instructions.get(offset), hackCode);
	}
}
