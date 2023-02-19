package com.teamderpy.shouldersurfing.asm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

public abstract class ShoulderTransformer implements IClassTransformer
{
	private static final Mappings MAPPINGS = Mappings.load("mappings.json");
	private static final Logger LOGGER = LogManager.getLogger("Shoulder Surfing");
	private static final boolean OBFUSCATED = !(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if(name.equals(this.getTransformedClassName(OBFUSCATED)))
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(bytes);
			classReader.accept(classNode, 0);
			
			if(this.hasMethodTransformer())
			{
				LOGGER.info("Attempting to transform method for class " + name + " -> " + transformedName);
				this.transformMethod(MAPPINGS, OBFUSCATED, classNode);
			}
			
			ClassWriter writer = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(writer);
			
			if(this.hasClassTransformer())
			{
				LOGGER.info("Attempting to transform class " + name + " -> " + transformedName);
				this.transform(MAPPINGS, OBFUSCATED, writer);
			}
			
			return writer.toByteArray();
		}
		
		return bytes;
	}

	private void transformMethod(Mappings mappings, boolean obf, ClassNode classNode)
	{
		String methodId = this.getMethodId();
		String methodName = mappings.map(methodId, obf);
		String methodDesc = mappings.desc(methodId, obf);
		
		for(Object m : classNode.methods)
		{
			MethodNode method = (MethodNode) m;
			
			if(method.name.equals(methodName) && method.desc.equals(methodDesc))
			{
				String methodDeobf = mappings.map(methodId, false) + mappings.desc(methodId, false);
				String methodObf = method.name + method.desc;
				int offset = ShoulderTransformer.locateOffset(method.instructions, this.searchList(mappings, obf), this.ignoreLabels(), this.ignoreLineNumber());
				
				if(offset == -1)
				{
					LOGGER.info(this.getClass().getSimpleName() + ": Failed to locate offset for method " + methodDeobf + " -> " + methodObf);
				}
				else
				{
					LOGGER.info(this.getClass().getSimpleName() + ": Found offset " + offset + " for method " + methodDeobf + " -> " + methodObf);
					this.transform(mappings, obf, method, offset);
				}
			}
		}
	}
	
	private String getTransformedClassName(boolean obf)
	{
		return MAPPINGS.map(this.getClassId(), obf).replace('/', '.');
	}
	
	protected void transform(Mappings mappings, boolean obf, MethodNode method, int offset)
	{
		return;
	}
	
	protected void transform(Mappings mappings, boolean obf, ClassWriter writer)
	{
		return;
	}
	
	protected boolean ignoreLabels()
	{
		return true;
	}
	
	protected boolean ignoreLineNumber()
	{
		return true;
	}
	
	protected String getMethodId()
	{
		return null;
	}
	
	protected InsnList searchList(Mappings mappings, boolean obf)
	{
		return null;
	}
	
	protected abstract String getClassId();
	
	protected abstract boolean hasMethodTransformer();
	
	protected abstract boolean hasClassTransformer();
	
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
	private static int locateOffset(InsnList instructions, InsnList search, boolean ignoreLabel, boolean ignoreLineNumber)
	{
		return locateOffset(instructions, search, 0, 0, instructions.size(), ignoreLabel, ignoreLineNumber);
	}
	
	/**
	 * Locates the offset of a set of instructions in the Java byte code.
	 * 
	 * This performs a linear search.
	 * <br />
	 * <br />
	 * <b>Implemented bytecode instructions: </b>
	 * <ul>
	 *   <li>INSN</li>
	 *   <li>INT_INSN</li>
	 *   <li>VAR_INSN</li>
	 *   <li>FIELD_INSN</li>
	 *   <li>METHOD_INSN</li>
	 *   <li>LABEL</li>
	 *   <li>FRAME</li>
	 *   <li>LINE</li>
	 *   <li>IINC_INSN</li>
	 *   <li>LDC_INSN</li>
	 *   </ul>
	 * <br />
	 * <b>Unimplemented bytecode instructions: </b>
	 * <ul>
	 *   <li>TYPE_INSN</li>
	 *   <li>INVOKE_DYNAMIC_INSN</li>
	 *   <li>JUMP_INSN</li>
	 *   <li>TABLESWITCH_INSN</li>
	 *   <li>LOOKUPSWITCH_INSN</li>
	 *   <li>MULTIANEWARRAY_INSN</li>
	 * </ul>
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
				else if(instruction.getType() == AbstractInsnNode.LDC_INSN)
				{
					if(((LdcInsnNode) searchNode).cst.equals(((LdcInsnNode) instruction).cst) && instruction.getOpcode() == searchNode.getOpcode())
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
