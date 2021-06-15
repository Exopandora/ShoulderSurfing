package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransformerRayTraceProjection extends ShoulderTransformer
{
	@Override
	public String getClassId()
	{
		return "EntityRenderer";
	}
	
	@Override
	public String getMethodId()
	{
		return "EntityRenderer#renderWorldPass";
	}
	
	@Override
	protected InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKESTATIC, mappings.map("ClippingHelperImpl", obf), mappings.map("ClippingHelperImpl#getInstance", obf), mappings.getDesc("ClippingHelperImpl#getInstance", obf), false));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// net/minecraft/client/renderer/EntityRenderer.renderWorldPass:1332
		// InjectionDelegation.calculateRayTraceProjection();
		
		InsnList hackCode = new InsnList();
		
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "calculateRayTraceProjection", "()V", false));
		hackCode.add(new LabelNode(new Label()));
		
		method.instructions.insert(method.instructions.get(offset + 1), hackCode);
	}
	
	@Override
	protected boolean hasMethodTransformer()
	{
		return true;
	}
	
	@Override
	protected boolean hasClassTransformer()
	{
		return false;
	}
}
