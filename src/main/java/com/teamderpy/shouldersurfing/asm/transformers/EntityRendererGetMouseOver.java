package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

public class EntityRendererGetMouseOver extends ShoulderTransformer
{
	@Override
	protected InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new MethodInsnNode(INVOKEVIRTUAL, mappings.map("Vec3d", obf), mappings.map("Vec3d#addVector", obf), mappings.desc("Vec3d#addVector", obf), false));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// Vec3d vec3d2 = vec3d.addVector(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
		// ->
		// Entry<Vec3d, Vec3d> vec3d2 = InjectionDelegation.update(d0);
		
		AbstractInsnNode node = method.instructions.get(offset);
		AbstractInsnNode start = method.instructions.get(offset - 14);
		AbstractInsnNode stop = node.getPrevious().getPrevious();
		
		while(!start.getNext().equals(stop))
		{
			method.instructions.remove(start.getNext());
		}
		
		method.instructions.remove(node.getPrevious());
		method.instructions.set(node, new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "EntityRenderer_getMouseOver", "(D)Ljava/util/Map$Entry;", false));
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
