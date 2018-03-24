package com.teamderpy.shouldersurfing.asm.transformer;

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
import com.teamderpy.shouldersurfing.asm.transformer.abstr.TransformerOrientCamera;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransformerCameraOrientation extends TransformerOrientCamera
{
	@Override
	public InsnList getSearchList(Mappings mappings)
	{
		InsnList searchList = new InsnList();
		
		searchList.add(new VarInsnNode(ALOAD, 2));
		searchList.add(new FieldInsnNode(GETFIELD, mappings.getClassPath("Entity"), mappings.getFieldOrMethod("Entity#rotationYaw"), mappings.getDescriptor("Entity#rotationYaw")));
		searchList.add(new VarInsnNode(FSTORE, 12));
		searchList.add(new VarInsnNode(ALOAD, 2));
		searchList.add(new FieldInsnNode(GETFIELD, mappings.getClassPath("Entity"), mappings.getFieldOrMethod("Entity#rotationPitch"), mappings.getDescriptor("Entity#rotationPitch")));
		searchList.add(new VarInsnNode(FSTORE, 13));
		
		return searchList;
	}
	
	@Override
	public InsnList getInjcetionList(Mappings mappings)
	{
		InsnList hackCode = new InsnList();
		
		// net/minecraft/client/renderer/EntityRenderer.orientCamera:653
		// f1 += InjectionDelegation.getShoulderRotation();
		
		hackCode.add(new VarInsnNode(FLOAD, 12));
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getShoulderRotation", "()F", false));
		hackCode.add(new InsnNode(FADD));
		hackCode.add(new VarInsnNode(FSTORE, 12));
		
		// net/minecraft/client/renderer/EntityRenderer.orientCamera:654
		// d3 *= InjectionDelegation.getShoulderZoomMod();
		
		hackCode.add(new VarInsnNode(DLOAD, 10));
		hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getShoulderZoomMod", "()F", false));
		hackCode.add(new InsnNode(F2D));
		hackCode.add(new InsnNode(DMUL));
		hackCode.add(new VarInsnNode(DSTORE, 10));
		
		hackCode.add(new LabelNode(new Label()));
		
		return hackCode;
	}
	
	@Override
	public void transform(MethodNode method, InsnList hackCode, int offset)
	{
		method.instructions.insertBefore(method.instructions.get(offset + 1), hackCode);
	}
}
