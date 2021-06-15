package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransformerThirdPersonMode extends ShoulderTransformer
{
	@Override
	public String getClassId()
	{
		return "Minecraft";
	}
	
	@Override
	public String getMethodId()
	{
		return "Minecraft#processKeyBinds";
	}
	
	@Override
	protected InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new FieldInsnNode(GETFIELD, mappings.map("GameSettings", obf), mappings.map("GameSettings#thirdPersonView", obf), mappings.getDesc("GameSettings#thirdPersonView", obf)));
		searchList.add(new InsnNode(ICONST_2));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		MethodInsnNode instruction = new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getMax3ppId", "()I", false);
		method.instructions.set(method.instructions.get(offset), instruction);
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
