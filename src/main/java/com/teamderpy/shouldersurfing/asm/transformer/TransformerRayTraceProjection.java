package com.teamderpy.shouldersurfing.asm.transformer;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.POP;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.Mappings;

public class TransformerRayTraceProjection implements ITransformer
{
	@Override
	public String getClassName()
	{
		return "EntityRenderer";
	}
	
	@Override
	public String getMethodName()
	{
		return "EntityRenderer#renderWorldPass";
	}
	
	@Override
	public InsnList getSearchList(Mappings mappings)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKESTATIC, mappings.getClassPath("ClippingHelperImpl"), mappings.getFieldOrMethod("ClippingHelperImpl#getInstance"), mappings.getDescriptor("ClippingHelperImpl#getInstance"), false));
		
		return searchList;
	}
	
	@Override
	public InsnList getInjcetionList(Mappings mappings)
	{
		InsnList hackCode = new InsnList();
		
		// net/minecraft/client/renderer/EntityRenderer.renderWorldPass:1332
		// InjectionDelegation.calculateRayTraceProjection();
		
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "calculateRayTraceProjection", "()V", false));
		hackCode.add(new LabelNode(new Label()));
		
		return hackCode;
	}
	
	@Override
	public void transform(MethodNode method, InsnList hackCode, int offset)
	{
		method.instructions.insert(method.instructions.get(offset + 1), hackCode);
	}
}
