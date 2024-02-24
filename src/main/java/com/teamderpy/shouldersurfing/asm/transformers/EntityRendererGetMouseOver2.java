package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.asm.IShoulderMethodTransformer;
import com.teamderpy.shouldersurfing.asm.Mappings;

public class EntityRendererGetMouseOver2 implements IShoulderMethodTransformer
{
	@Override
	public InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKEVIRTUAL, mappings.map("AxisAlignedBB", obf), mappings.map("AxisAlignedBB#calculateIntercept", obf), mappings.desc("AxisAlignedBB#calculateIntercept", obf), false));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
		// ->
		// RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d2.getKey(), vec3d2.getValue());
		
		AbstractInsnNode vec3d2 = method.instructions.get(offset - 1);
		
		if(vec3d2 instanceof VarInsnNode)
		{
			method.instructions.set(vec3d2.getPrevious(), new VarInsnNode(vec3d2.getOpcode(), ((VarInsnNode) vec3d2).var));
			method.instructions.insertBefore(vec3d2, new MethodInsnNode(INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;", true));
			method.instructions.insertBefore(vec3d2, new TypeInsnNode(CHECKCAST, mappings.map("Vec3d", obf)));
			method.instructions.insert(vec3d2, new TypeInsnNode(CHECKCAST, mappings.map("Vec3d", obf)));
			method.instructions.insert(vec3d2, new MethodInsnNode(INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;", true));
		}
	}
	
	@Override
	public String getClassId()
	{
		return "EntityRenderer";
	}
	
	@Override
	public String getMethodId()
	{
		return "EntityRenderer#getMouseOver";
	}
}
