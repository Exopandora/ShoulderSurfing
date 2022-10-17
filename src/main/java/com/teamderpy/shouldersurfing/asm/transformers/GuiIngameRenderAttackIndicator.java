package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

public class GuiIngameRenderAttackIndicator extends ShoulderTransformer
{
	@Override
	protected InsnList searchList(Mappings mappings, boolean obf)
	{
		InsnList searchList = new InsnList();
		searchList.add(new FieldInsnNode(GETFIELD, mappings.map("GameSettings", obf), mappings.map("GameSettings#thirdPersonView", obf), mappings.desc("GameSettings#thirdPersonView", obf)));
		return searchList;
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		// if(gamesettings.thirdPersonView == 0)
		// ->
		// if(InjectionDelegation.doRenderCrosshair() == 0)
		
		MethodInsnNode instruction = new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "GuiIngame_renderAttackIndicator", "()I", false);
		method.instructions.set(method.instructions.get(offset), instruction);
		method.instructions.remove(method.instructions.get(offset - 1));
	}
	
	@Override
	public String getClassId()
	{
		return "GuiIngame";
	}
	
	@Override
	public String getMethodId()
	{
		return "GuiIngame#renderAttackIndicator";
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
