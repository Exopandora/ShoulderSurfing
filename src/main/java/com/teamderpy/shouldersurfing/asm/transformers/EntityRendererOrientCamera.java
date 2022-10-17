package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.D2F;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DNEG;
import static org.objectweb.asm.Opcodes.FCONST_0;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

public class EntityRendererOrientCamera extends ShoulderTransformer
{
	@Override
	protected InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glRotatef", "(FFFF)V", false));
		searchList.add(new InsnNode(FCONST_0));
		searchList.add(new InsnNode(FCONST_0));
		searchList.add(new VarInsnNode(DLOAD, 10));
		searchList.add(new InsnNode(DNEG));
		searchList.add(new InsnNode(D2F));
		searchList.add(new MethodInsnNode(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glTranslatef", "(FFF)V", false));
		return searchList;
	}
	
	@Override
	protected void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// GL11.glTranslatef(0.0F, 0.0F, (float) -d3);
		// ->
		// InjectionDelegation.EntityRenderer_orientCamera(0.0F, 0.0F, (float) -d3);
		method.instructions.set(method.instructions.get(offset), new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "EntityRenderer_orientCamera", "(FFF)V", false));
	}
	
	@Override
	public String getClassId()
	{
		return "EntityRenderer";
	}
	
	@Override
	public String getMethodId()
	{
		return "EntityRenderer#orientCamera";
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
