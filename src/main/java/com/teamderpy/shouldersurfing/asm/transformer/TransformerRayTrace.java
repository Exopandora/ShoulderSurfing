package com.teamderpy.shouldersurfing.asm.transformer;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.transformer.abstr.TransformerOrientCamera;

public class TransformerRayTrace extends TransformerOrientCamera
{
	@Override
	public InsnList getSearchList(Mappings mappings)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKEVIRTUAL, mappings.getClassPath("WorldClient"), mappings.getFieldOrMethod("WorldClient#rayTraceBlocks"), mappings.getDescriptor("WorldClient#rayTraceBlocks"), false));
		
		return searchList;
	}
	
	@Override
	public InsnList getInjcetionList(Mappings mappings)
	{
		// net/minecraft/client/renderer/EntityRenderer.orientCamera:653
		// InjectionDelegation.getRayTraceResult(this.mc.world, Vec3d, Vec3d);
		
		InsnList hackCode = new InsnList();
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getRayTraceResult", mappings.getDescriptor("InjectionDelegation#getRayTraceResult"), false));
		
		return hackCode;
	}
	
	@Override
	public void transform(MethodNode method, InsnList hackCode, int offset)
	{
		method.instructions.set(method.instructions.get(offset), hackCode.getFirst());
	}
}
