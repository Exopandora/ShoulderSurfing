package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.D2F;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DNEG;
import static org.objectweb.asm.Opcodes.FCONST_0;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.asm.IShoulderMethodTransformer;
import com.teamderpy.shouldersurfing.asm.Mappings;

public class ValkyrienSkiesMixinEntityRendererOrientCamera2 implements IShoulderMethodTransformer
{
	@Override
	public InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKESTATIC, mappings.map("GlStateManager", false), mappings.map("SRG#GlStateManager#rotate", obf), mappings.desc("SRG#GlStateManager#rotate", obf), false));
		searchList.add(new InsnNode(FCONST_0));
		searchList.add(new InsnNode(FCONST_0));
		searchList.add(new VarInsnNode(DLOAD, 12));
		searchList.add(new InsnNode(DNEG));
		searchList.add(new InsnNode(D2F));
		searchList.add(new MethodInsnNode(INVOKESTATIC, mappings.map("GlStateManager", false), mappings.map("SRG#GlStateManager#translate", obf), mappings.desc("SRG#GlStateManager#translate", obf), false));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// GlStateManager.translate(0.0F, 0.0F, (float) -d3);
		// ->
		// InjectionDelegation.EntityRenderer_orientCamera_cameraOffset(0.0F, 0.0F, (float) -d3);
		
		AbstractInsnNode instruction = method.instructions.get(offset);
		method.instructions.insertBefore(instruction, new VarInsnNode(FLOAD, 15)); // BetterThirdPerson compatibility
		method.instructions.insertBefore(instruction, new VarInsnNode(FLOAD, 16)); // BetterThirdPerson compatibility
		method.instructions.set(instruction, new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "EntityRenderer_orientCamera", "(FFFFF)V", false));
	}
	
	@Override
	public String getClassId()
	{
		return "ValkyrienSkiesMixinEntityRenderer";
	}
	
	@Override
	public String getMethodId()
	{
		return "ValkyrienSkiesMixinEntityRenderer#orientCamera";
	}
}
