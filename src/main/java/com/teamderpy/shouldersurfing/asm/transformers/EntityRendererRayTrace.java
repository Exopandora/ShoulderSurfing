package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

public class EntityRendererRayTrace extends ShoulderTransformer
{
	@Override
	protected InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKEVIRTUAL, mappings.map("WorldClient", obf), mappings.map("WorldClient#rayTraceBlocks", obf), mappings.desc("WorldClient#rayTraceBlocks", obf), false));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// this.mc.world.rayTraceBlocks(Vec3, Vec3);
		// -> 
		// InjectionDelegation.getRayTraceResult(this.mc.world, Vec3, Vec3);
		
		MethodInsnNode instruction = new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getRayTraceResult", mappings.desc("InjectionDelegation#getRayTraceResult", obf), false);
		method.instructions.set(method.instructions.get(offset), instruction);
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
