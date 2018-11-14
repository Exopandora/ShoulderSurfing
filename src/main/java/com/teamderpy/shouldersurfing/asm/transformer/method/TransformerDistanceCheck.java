package com.teamderpy.shouldersurfing.asm.transformer.method;

import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.asm.Mappings;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransformerDistanceCheck extends ATransformerOrientCamera
{
	@Override
	public InsnList getSearchList(Mappings mappings)
	{
		InsnList searchList = new InsnList();
		
		searchList.add(new IincInsnNode(20, 1));
		
		return searchList;
	}
	
	@Override
	public void transform(MethodNode method, Mappings mappings, int offset)
	{
		InsnList hackCode = new InsnList();
		
		hackCode.add(new VarInsnNode(DLOAD, 10));
		hackCode.add(new VarInsnNode(FLOAD, 12));
		hackCode.add(new VarInsnNode(DLOAD, 4));
		hackCode.add(new VarInsnNode(DLOAD, 6));
		hackCode.add(new VarInsnNode(DLOAD, 8));
		hackCode.add(new VarInsnNode(DLOAD, 14));
		hackCode.add(new VarInsnNode(DLOAD, 18));
		hackCode.add(new VarInsnNode(DLOAD, 16));
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "checkDistance", "(DFDDDDDD)D", false));
		hackCode.add(new VarInsnNode(DSTORE, 10));
		
		method.instructions.insert(method.instructions.get(offset + 4), hackCode);
	}
}
