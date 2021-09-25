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
		searchList.add(new MethodInsnNode(INVOKEVIRTUAL, mappings.map("Vec3", obf), mappings.map("Vec3#addVector", obf), mappings.desc("Vec3#addVector", obf), false));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// Vec3 Vec32 = Vec3.addVector(Vec31.x * d0, Vec31.y * d0, Vec31.z * d0);
		// ->
		// Entry<Vec3, Vec3> Vec32 = InjectionDelegation.update(d0);
		
		AbstractInsnNode node = method.instructions.get(offset);
		AbstractInsnNode start = method.instructions.get(offset - 14);
		AbstractInsnNode stop = node.getPrevious().getPrevious();
		
		while(!start.getNext().equals(stop))
		{
			method.instructions.remove(start.getNext());
		}
		
		method.instructions.remove(node.getPrevious());
		method.instructions.set(node, new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "shoulderSurfingLook", "(D)Ljava/util/Map$Entry;", false));
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
