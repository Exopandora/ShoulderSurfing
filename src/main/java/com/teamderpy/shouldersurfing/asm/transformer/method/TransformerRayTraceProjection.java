package com.teamderpy.shouldersurfing.asm.transformer.method;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.transformer.IMethodTransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransformerRayTraceProjection implements IMethodTransformer
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
	public void transform(MethodNode method, Mappings mappings, int offset)
	{
		// net/minecraft/client/renderer/EntityRenderer.renderWorldPass:1332
		// InjectionDelegation.calculateRayTraceProjection();
		
		InsnList hackCode = new InsnList();
		
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "calculateRayTraceProjection", "()V", false));
		hackCode.add(new LabelNode(new Label()));
		
		method.instructions.insert(method.instructions.get(offset + 1), hackCode);
	}
}
