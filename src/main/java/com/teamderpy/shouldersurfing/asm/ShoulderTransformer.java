package com.teamderpy.shouldersurfing.asm;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import com.teamderpy.shouldersurfing.asm.transformers.EntityPlayerRayTrace;
import com.teamderpy.shouldersurfing.asm.transformers.EntityRendererGetMouseOver;
import com.teamderpy.shouldersurfing.asm.transformers.EntityRendererGetMouseOver2;
import com.teamderpy.shouldersurfing.asm.transformers.EntityRendererOrientCamera;
import com.teamderpy.shouldersurfing.asm.transformers.EntityRendererRayTrace;
import com.teamderpy.shouldersurfing.asm.transformers.GlStateManagerBlendFunc;
import com.teamderpy.shouldersurfing.asm.transformers.GlStateManagerBlendFuncSeparate;
import com.teamderpy.shouldersurfing.asm.transformers.GlStateManagerColor;
import com.teamderpy.shouldersurfing.asm.transformers.GlStateManagerDepthMask;
import com.teamderpy.shouldersurfing.asm.transformers.GlStateManagerDisableBlend;
import com.teamderpy.shouldersurfing.asm.transformers.GuiCrosshairsBCRenderAttackIndicator;
import com.teamderpy.shouldersurfing.asm.transformers.GuiIngameRenderAttackIndicator;
import com.teamderpy.shouldersurfing.asm.transformers.ItemBoatRayTraceBlocks_1_11;
import com.teamderpy.shouldersurfing.asm.transformers.ItemBoatRayTraceBlocks_1_9;
import com.teamderpy.shouldersurfing.asm.transformers.ItemRayTraceBlocks;
import com.teamderpy.shouldersurfing.asm.transformers.ValkyrienSkiesMixinEntityRendererOrientCamera;
import com.teamderpy.shouldersurfing.asm.transformers.ValkyrienSkiesMixinEntityRendererOrientCamera2;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

public class ShoulderTransformer implements IClassTransformer
{
	private static final Mappings MAPPINGS = Mappings.load("mappings.json");
	private static final Logger LOGGER = LogManager.getLogger("Shoulder Surfing");
	private static final boolean OBFUSCATED = !(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	private static final Map<String, List<IShoulderTransformer>> TRANSFORMERS = build
	(
		new EntityPlayerRayTrace(),
		new EntityRendererGetMouseOver(),
		new EntityRendererGetMouseOver2(),
		new EntityRendererOrientCamera(),
		new EntityRendererRayTrace(),
		new GlStateManagerColor(),
		new GlStateManagerBlendFunc(),
		new GlStateManagerBlendFuncSeparate(),
		new GlStateManagerDepthMask(),
		new GlStateManagerDisableBlend(),
		new GuiIngameRenderAttackIndicator(),
		new ItemRayTraceBlocks(),
		new ItemBoatRayTraceBlocks_1_9(),
		new ItemBoatRayTraceBlocks_1_11(),
		new GuiCrosshairsBCRenderAttackIndicator(), // Better Combat compatibility: crosshair visibility
		new ValkyrienSkiesMixinEntityRendererOrientCamera(), // Valkyrien Skies compatibility: camera distance
		new ValkyrienSkiesMixinEntityRendererOrientCamera2() // Valkyrien Skies compatibility: camera offset
	);
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if(TRANSFORMERS.containsKey(name))
		{
			List<IShoulderTransformer> transformers = TRANSFORMERS.get(name);
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(bytes);
			classReader.accept(classNode, 0);
			
			LOGGER.info("Attempting to transform class " + name + " -> " + transformedName);
			
			List<IShoulderMethodTransformer> methodTransformers = transformers.stream()
				.filter(transformer -> transformer instanceof IShoulderMethodTransformer)
				.map(transformer -> (IShoulderMethodTransformer) transformer)
				.collect(Collectors.toList());
			
			if(!methodTransformers.isEmpty())
			{
				this.transformMethods(methodTransformers, MAPPINGS, OBFUSCATED, classNode);
			}
			
			ClassWriter writer = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(writer);
			
			List<IShoulderClassTransformer> classTransformers = transformers.stream()
				.filter(transfomer -> transfomer instanceof IShoulderClassTransformer)
				.map(transformer -> (IShoulderClassTransformer) transformer)
				.collect(Collectors.toList());
			
			if(!classTransformers.isEmpty())
			{
				this.transformClass(classTransformers, MAPPINGS, OBFUSCATED, writer);
			}
			
			return writer.toByteArray();
		}
		
		return bytes;
	}
	
	private void transformClass(List<IShoulderClassTransformer> transformers, Mappings mappings, boolean obf, ClassWriter writer)
	{
		for(IShoulderClassTransformer transformer : transformers)
		{
			LOGGER.info("Attempting to apply class transformer " + transformer.getClass().getSimpleName());
			transformer.transform(MAPPINGS, OBFUSCATED, writer);
		}
	}
	
	private void transformMethods(List<IShoulderMethodTransformer> transformers, Mappings mappings, boolean obf, ClassNode classNode)
	{
		Map<String, List<IShoulderMethodTransformer>> method2transformers = transformers.stream()
			.collect(Collectors.groupingBy(transformer -> mappings.map(transformer.getMethodId(), obf) + mappings.desc(transformer.getMethodId(), obf)));
		
		for(Object m : classNode.methods)
		{
			MethodNode method = (MethodNode) m;
			String methodObf = method.name + method.desc;
			
			if(method2transformers.containsKey(methodObf))
			{
				String methodDeobf = mappings.map(transformers.get(0).getMethodId(), false) + mappings.desc(transformers.get(0).getMethodId(), false);
				method2transformers.get(methodObf).forEach(transformer ->
				{
					String transformerName = transformer.getClass().getSimpleName();
					LOGGER.info("Attempting to apply method transformer " + transformerName + ", " + methodObf + " -> " + methodDeobf);
					InsnList searchList = transformer.searchList(mappings, obf);
					boolean ignoreLabels = transformer.ignoreLabels();
					boolean ignoreLineNumber = transformer.ignoreLineNumber();
					int offset = ShoulderTransformer.locateOffset(method.instructions, searchList, ignoreLabels, ignoreLineNumber);
					
					if(offset == -1)
					{
						LOGGER.info("Failed to locate offset for transformer " + transformerName);
					}
					else
					{
						LOGGER.info("Found offset " + offset + " for transformer " + transformerName);
						transformer.transform(mappings, obf, method, offset);
					}
				});
			}
		}
		
		method2transformers.values().stream().flatMap(List::stream).forEach(transformer ->
		{
			LOGGER.warn("Could not find method to apply " + transformer.getClass().getSimpleName());
		});
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
	
	private static Map<String, List<IShoulderTransformer>> build(IShoulderTransformer... transformers)
	{
		return Stream.of(transformers).collect(Collectors.groupingBy(transformer -> MAPPINGS.map(transformer.getClassId(), OBFUSCATED).replace('/', '.')));
	}
}
