package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.IShoulderMethodTransformer;
import com.teamderpy.shouldersurfing.asm.Mappings;

public class EntityRendererRayTrace implements IShoulderMethodTransformer
{
	@Override
	public InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKEVIRTUAL, mappings.map("WorldClient", obf), mappings.map("WorldClient#rayTraceBlocks", obf), mappings.desc("WorldClient#rayTraceBlocks", obf), false));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// this.mc.world.rayTraceBlocks(Vec3d, Vec3d);
		// -> 
		// InjectionDelegation.EntityRenderer_rayTrace(this.mc.world, Vec3d, Vec3d);
		
		MethodInsnNode instruction = new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "EntityRenderer_rayTrace", mappings.desc("InjectionDelegation#EntityRenderer_rayTrace", obf), false);
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
}
