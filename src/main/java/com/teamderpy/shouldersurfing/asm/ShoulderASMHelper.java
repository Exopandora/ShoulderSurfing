package com.teamderpy.shouldersurfing.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
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
	 *
	 * @param instructions
	 *            {@link InsnList} containing Java byte instructions to go
	 *            through
	 * @param search
	 *            {@link InsnList} containing Java byte instructions to match
	 * @return Returns an integer with the byte offset after the matched code,
	 *         or -1 if no match is found.
	 */
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
			
			if(ignoreLabel && instructions.get(i).getType() == AbstractInsnNode.LABEL)
			{
				continue;
			}
			
			if(ignoreLineNumber && instructions.get(i).getType() == AbstractInsnNode.LINE)
			{
				continue;
			}
			
			boolean match = false;
			if(instructions.get(i).getType() == search.get(searchNdx).getType())
			{
				if(instructions.get(i).getType() == AbstractInsnNode.FIELD_INSN)
				{
					if(((FieldInsnNode) instructions.get(i)).desc.equals(((FieldInsnNode) search.get(searchNdx)).desc) && ((FieldInsnNode) instructions.get(i)).name.equals(((FieldInsnNode) search.get(searchNdx)).name) && ((FieldInsnNode) instructions.get(i)).owner.equals(((FieldInsnNode) search.get(searchNdx)).owner))
					{
						// System.err.println("field hit");
						match = true;
					}
				}
				else if(instructions.get(i).getType() == AbstractInsnNode.VAR_INSN)
				{
					if(((VarInsnNode) instructions.get(i)).var == ((VarInsnNode) search.get(searchNdx)).var && instructions.get(i).getOpcode() == search.get(searchNdx).getOpcode())
					{
						// System.err.println("var hit");
						match = true;
					}
				}
				else if(instructions.get(i).getType() == AbstractInsnNode.INSN)
				{
					if(((InsnNode) instructions.get(i)).getOpcode() == ((InsnNode) search.get(searchNdx)).getOpcode())
					{
						// System.err.println("insn hit");
						match = true;
					}
				}
				else if(instructions.get(i).getType() == AbstractInsnNode.METHOD_INSN)
				{
					// System.err.println(((MethodInsnNode)
					// instructions.get(i)).name + " " + ((MethodInsnNode)
					// instructions.get(i)).owner + " " + ((MethodInsnNode)
					// instructions.get(i)).desc);
					// System.err.println(((MethodInsnNode)
					// search.get(searchNdx)).name + " " + ((MethodInsnNode)
					// search.get(searchNdx)).owner + " " + ((MethodInsnNode)
					// search.get(searchNdx)).desc);
					if(((MethodInsnNode) instructions.get(i)).desc.equals(((MethodInsnNode) search.get(searchNdx)).desc) && ((MethodInsnNode) instructions.get(i)).name.equals(((MethodInsnNode) search.get(searchNdx)).name) && ((MethodInsnNode) instructions.get(i)).owner.equals(((MethodInsnNode) search.get(searchNdx)).owner))
					{
						// System.err.println("method hit");
						match = true;
					}
				}
				else if(instructions.get(i).getType() == AbstractInsnNode.INT_INSN)
				{
					if(((IntInsnNode) instructions.get(i)).operand == ((IntInsnNode) search.get(searchNdx)).operand && instructions.get(i).getOpcode() == search.get(searchNdx).getOpcode())
					{
						// System.err.println("int insn hit");
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
				// System.err.println("miss");
				attempts++;
			}
		}
		
		return -1;
	}
}
