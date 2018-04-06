package com.teamderpy.shouldersurfing.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * <b>Implemented bytecode instructions: </b>
 * INSN,
 * INT_INSN,
 * VAR_INSN,
 * FIELD_INSN,
 * METHOD_INSN,
 * LABEL,
 * FRAME,
 * LINE,
 * IINC_INSN
 * <br />
 * <b>Unimplemented bytecode instructions: </b>
 * TYPE_INSN,
 * INVOKE_DYNAMIC_INSN,
 * JUMP_INSN,
 * LDC_INSN,
 * TABLESWITCH_INSN,
 * LOOKUPSWITCH_INSN,
 * MULTIANEWARRAY_INSN
 * 
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.0
 * @since 2012-12-30
 */
@SideOnly(Side.CLIENT)
public class ShoulderASMHelper
{
	/**
	 * Locates the offset of a set of instructions in the Java byte code.
	 * Ignores label nodes and line number nodes by default.
	 * 
	 * This performs a linear search.
	 * @deprecated
	 * @param instructions
	 *            {@link InsnList} containing Java byte instructions to go
	 *            through
	 * @param search
	 *            {@link InsnList} containing Java byte instructions to match
	 * @return Returns an integer with the byte offset after the matched code,
	 *         or -1 if no match is found.
	 */
	@Deprecated
	public static int locateOffset(InsnList instructions, InsnList search)
	{
		return locateOffset(instructions, search, true, true);
	}
	
	/**
	 * Locates the offset of a set of instructions in the Java byte code.
	 * 
	 * This performs a linear search.
	 *
	 * @param instructions
	 *            {@link InsnList} containing Java byte instructions to go
	 *            through
	 * @param search
	 *            {@link InsnList} containing Java byte instructions to match
	 * @param ignoreLabel
	 *            Whether or not to ignore Label nodes
	 * @param ignoreLineNumber
	 *            Whether or not to ignore line number nodes
	 * @return Returns an integer with the byte offset after the matched code,
	 *         or -1 if no match is found.
	 */
	public static int locateOffset(InsnList instructions, InsnList search, boolean ignoreLabel, boolean ignoreLineNumber)
	{
		return locateOffset(instructions, search, 0, 0, instructions.size(), ignoreLabel, ignoreLineNumber);
	}
	
	/**
	 * Locates the offset of a set of instructions in the Java byte code.
	 * 
	 * This performs a linear search.
	 *
	 * @param instructions
	 *            {@link InsnList} containing Java byte instructions to go
	 *            through
	 * @param search
	 *            {@link InsnList} containing Java byte instructions to match
	 * @param searchNdx
	 *            The index of the search instruction to look for
	 * @param startAt
	 *            The instruction index to start searching from
	 * @param limit
	 *            The maximum number of times to search for a match
	 * @param ignoreLabel
	 *            Whether or not to ignore Label nodes
	 * @param ignoreLineNumber
	 *            Whether or not to ignore line number nodes
	 * @return Returns an integer with the byte offset after the matched code,
	 *         or -1 if no match is found.
	 */
	private static int locateOffset(InsnList instructions, InsnList search, int searchNdx, int startAt, int limit, boolean ignoreLabel, boolean ignoreLineNumber)
	{
		int attempts = 0;
		
		for(int i = startAt; i < instructions.size(); i++)
		{
			if(attempts >= limit)
			{
				break;
			}
			
			AbstractInsnNode instruction = instructions.get(i);
			
			if(ignoreLabel && instruction.getType() == AbstractInsnNode.LABEL)
			{
				continue;
			}
			
			if(ignoreLineNumber && instruction.getType() == AbstractInsnNode.LINE)
			{
				continue;
			}
			
			boolean match = false;
			
			AbstractInsnNode searchNode = search.get(searchNdx);
			
			if(instruction.getType() == searchNode.getType())
			{
				if(instruction.getType() == AbstractInsnNode.FIELD_INSN)
				{
					if(((FieldInsnNode) instruction).desc.equals(((FieldInsnNode) searchNode).desc) && ((FieldInsnNode) instruction).name.equals(((FieldInsnNode) searchNode).name) && ((FieldInsnNode) instruction).owner.equals(((FieldInsnNode) searchNode).owner))
					{
						match = true;
					}
				}
				else if(instruction.getType() == AbstractInsnNode.VAR_INSN)
				{
					if(((VarInsnNode) instruction).var == ((VarInsnNode) searchNode).var && instruction.getOpcode() == searchNode.getOpcode())
					{
						match = true;
					}
				}
				else if(instruction.getType() == AbstractInsnNode.INSN)
				{
					if(((InsnNode) instruction).getOpcode() == ((InsnNode) searchNode).getOpcode())
					{
						match = true;
					}
				}
				else if(instruction.getType() == AbstractInsnNode.METHOD_INSN)
				{
					if(((MethodInsnNode) instruction).desc.equals(((MethodInsnNode) searchNode).desc) && ((MethodInsnNode) instruction).name.equals(((MethodInsnNode) searchNode).name) && ((MethodInsnNode) instruction).owner.equals(((MethodInsnNode) searchNode).owner))
					{
						match = true;
					}
				}
				else if(instruction.getType() == AbstractInsnNode.INT_INSN)
				{
					if(((IntInsnNode) instruction).operand == ((IntInsnNode) searchNode).operand && instruction.getOpcode() == searchNode.getOpcode())
					{
						match = true;
					}
				}
				else if(instruction.getType() == AbstractInsnNode.IINC_INSN)
				{
					if(((IincInsnNode) instruction).var == ((IincInsnNode) searchNode).var && ((IincInsnNode) instruction).incr == ((IincInsnNode) searchNode).incr && instruction.getOpcode() == searchNode.getOpcode())
					{
						match = true;
					}
				}
				
				if(match)
				{
					if(searchNdx < search.size() - 1)
					{
						int offset = locateOffset(instructions, search, searchNdx + 1, i + 1, 1, ignoreLabel, ignoreLineNumber);
						
						if(offset != -1)
						{
							return offset;
						}
					}
					else
					{
						return i;
					}
				}
			}
			
			if(!match)
			{
				attempts++;
			}
		}
		
		return -1;
	}
}
