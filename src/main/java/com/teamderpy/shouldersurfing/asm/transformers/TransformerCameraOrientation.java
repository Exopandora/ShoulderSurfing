package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DMUL;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.F2D;
import static org.objectweb.asm.Opcodes.FADD;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.asm.Mappings;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransformerCameraOrientation extends ATransformerOrientCamera
{
	@Override
	protected InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		
		searchList.add(new VarInsnNode(ALOAD, 2));
		searchList.add(new FieldInsnNode(GETFIELD, mappings.map("Entity", obf), mappings.map("Entity#rotationYaw", obf), mappings.getDesc("Entity#rotationYaw", obf)));
		searchList.add(new VarInsnNode(FSTORE, 12));
		searchList.add(new VarInsnNode(ALOAD, 2));
		searchList.add(new FieldInsnNode(GETFIELD, mappings.map("Entity", obf), mappings.map("Entity#rotationPitch", obf), mappings.getDesc("Entity#rotationPitch", obf)));
		searchList.add(new VarInsnNode(FSTORE, 13));
		
		return searchList;
	}
	
	@Override
	protected void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		InsnList hackCode = new InsnList();
		
		// net/minecraft/client/renderer/EntityRenderer.orientCamera:653
		// f1 += InjectionDelegation.getShoulderRotationYaw();
		
		hackCode.add(new VarInsnNode(FLOAD, 12));
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getShoulderRotationYaw", "()F", false));
		hackCode.add(new InsnNode(FADD));
		hackCode.add(new VarInsnNode(FSTORE, 12));

		// net/minecraft/client/renderer/EntityRenderer.orientCamera:653
		// f2 += InjectionDelegation.getShoulderRotationPitch();
		
		hackCode.add(new VarInsnNode(FLOAD, 13));
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getShoulderRotationPitch", "()F", false));
		hackCode.add(new InsnNode(FADD));
		hackCode.add(new VarInsnNode(FSTORE, 13));
		
		// net/minecraft/client/renderer/EntityRenderer.orientCamera:654
		// d3 *= InjectionDelegation.getShoulderZoomMod();
		
		hackCode.add(new VarInsnNode(DLOAD, 10));
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getShoulderZoomMod", "()F", false));
		hackCode.add(new InsnNode(F2D));
		hackCode.add(new InsnNode(DMUL));
		hackCode.add(new VarInsnNode(DSTORE, 10));
		
		hackCode.add(new LabelNode(new Label()));
		
		method.instructions.insertBefore(method.instructions.get(offset + 1), hackCode);
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
