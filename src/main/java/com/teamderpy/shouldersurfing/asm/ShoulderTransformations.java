package com.teamderpy.shouldersurfing.asm;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DMUL;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.F2D;
import static org.objectweb.asm.Opcodes.FADD;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.POP;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.ShoulderSurfing;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.5
 * @since 2013-11-17
 */
@SideOnly(Side.CLIENT)
public class ShoulderTransformations implements IClassTransformer
{
	public static final int CODE_MODIFICATIONS = 4;
	public static int modifications = 0;
	private final ShoulderMappings mappings = new ShoulderMappings();
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if(name.equals(this.mappings.getObf("EntityRenderer")))
		{
			ShoulderSurfing.LOGGER.info("Injecting into obfuscated code");
			this.mappings.setObfuscated(true);
			return transformEntityRenderClass(bytes);
		}
		else if(name.equals(this.mappings.getPackage("EntityRenderer")))
		{
			ShoulderSurfing.LOGGER.info("Injecting into non-obfuscated code");
			this.mappings.setObfuscated(false);
			return transformEntityRenderClass(bytes);
		}
		
		return bytes;
	}
	
	/**
	 * Transforms {@link EntityRenderer}
	 * 
	 * @param bytes
	 * @param isObfuscated
	 * @return ClassWriter byte array
	 */
	private byte[] transformEntityRenderClass(byte[] bytes)
	{
		ShoulderSurfing.LOGGER.info("Attempting class transformation against EntityRender");
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		Iterator<MethodNode> methods = classNode.methods.iterator();
		
		while(methods.hasNext())
		{
			MethodNode method = methods.next();
			
			if(this.methodMatches(method, "EntityRenderer#orientCamera"))
			{
				ShoulderSurfing.LOGGER.info("Located method " + method.name + method.desc + ", locating signature");
				
				// Locate injection point, after the yaw and pitch fields in the camera function
				
				InsnList searchList = new InsnList();
				searchList.add(new VarInsnNode(ALOAD, 2));
				searchList.add(new FieldInsnNode(GETFIELD, this.mappings.getClassPath("Entity"), this.mappings.getFieldOrMethod("Entity#rotationYaw"), this.mappings.getDescriptor("Entity#rotationYaw")));
				searchList.add(new VarInsnNode(FSTORE, 12));
				searchList.add(new VarInsnNode(ALOAD, 2));
				searchList.add(new FieldInsnNode(GETFIELD, this.mappings.getClassPath("Entity"), this.mappings.getFieldOrMethod("Entity#rotationPitch"), this.mappings.getDescriptor("Entity#rotationPitch")));
				searchList.add(new VarInsnNode(FSTORE, 13));
				
				int offset = ShoulderASMHelper.locateOffset(method.instructions, searchList);
				
				if(offset == -1)
				{
					ShoulderSurfing.LOGGER.error("Failed to locate first of three offsets in " + method.name + method.desc + "! Is base file changed?");
					return bytes;
				}
				else
				{
					ShoulderSurfing.LOGGER.info("Located offset @" + offset);
					InsnList hackCode = new InsnList();
					
					// net/minecraft/client/renderer/EntityRenderer:653
					// f1 += InjectionDelegation.getShoulderRotation();
					
					hackCode.add(new VarInsnNode(FLOAD, 12));
					hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getShoulderRotation", "()F", false));
					hackCode.add(new InsnNode(FADD));
					hackCode.add(new VarInsnNode(FSTORE, 12));
					
					// net/minecraft/client/renderer/EntityRenderer:654
					// d3 *= InjectionDelegation.getShoulderZoomMod();
					
					hackCode.add(new VarInsnNode(DLOAD, 10));
					hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getShoulderZoomMod", "()F", false));
					hackCode.add(new InsnNode(F2D));
					hackCode.add(new InsnNode(DMUL));
					hackCode.add(new VarInsnNode(DSTORE, 10));
					
					hackCode.add(new LabelNode(new Label()));
					
					method.instructions.insertBefore(method.instructions.get(offset + 1), hackCode);
					
					ShoulderSurfing.LOGGER.info("Injected code for camera orientation!");
					this.modifications++;
				}
				
				// Locate second injection point, after the reverse raytrace is performed
				
				searchList = new InsnList();
				searchList.add(new VarInsnNode(DSTORE, 25));
				searchList.add(new VarInsnNode(DLOAD, 25));
				
				offset = ShoulderASMHelper.locateOffset(method.instructions, searchList);
				
				if(offset == -1)
				{
					ShoulderSurfing.LOGGER.error("Failed to locate second of three offsets in " + method.name + method.desc + "! Is base file changed?");
					return bytes;
				}
				else
				{
					ShoulderSurfing.LOGGER.info("Located offset @" + offset);
					
					InsnList hackCode = new InsnList();
					hackCode.add(new VarInsnNode(DLOAD, 25));
					hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "verifyReverseBlockDist", "(D)V", false));
					hackCode.add(new LabelNode(new Label()));
					
					method.instructions.insertBefore(method.instructions.get(offset), hackCode);
					
					ShoulderSurfing.LOGGER.info("Injected code for camera distance check!");
					this.modifications++;
				}
				
				// Locate third injection point, when the ray trace is performed
				
				searchList = new InsnList();
				searchList.add(new MethodInsnNode(INVOKEVIRTUAL, this.mappings.getClassPath("WorldClient"), this.mappings.getFieldOrMethod("WorldClient#rayTraceBlocks"), this.mappings.getDescriptor("WorldClient#rayTraceBlocks"), false));
				
				offset = ShoulderASMHelper.locateOffset(method.instructions, searchList);
				
				if(offset == -1)
				{
					ShoulderSurfing.LOGGER.error("Failed to locate third of three offsets in " + method.name + method.desc + "! Is base file changed?");
					return bytes;
				}
				else
				{
					method.instructions.set(method.instructions.get(offset), new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getRayTraceResult", this.mappings.getDescriptor("InjectionDelegation#getRayTraceResult"), false));
					
					ShoulderSurfing.LOGGER.info("Injected code for ray trace!");
					this.modifications++;
				}
			}
			else if(this.methodMatches(method, "EntityRenderer#renderWorldPass"))
			{
				ShoulderSurfing.LOGGER.info("Located method " + method.name + method.desc + ", locating signature");
				
				// Locate injection point, after the clipping helper returns an instance
				
				InsnList searchList = new InsnList();
				searchList.add(new MethodInsnNode(INVOKESTATIC, this.mappings.getClassPath("ClippingHelperImpl"), this.mappings.getFieldOrMethod("ClippingHelperImpl#getInstance"), this.mappings.getDescriptor("ClippingHelperImpl#getInstance"), false));
				searchList.add(new InsnNode(POP));
				
				InsnList searchListOptifine = new InsnList();
				searchListOptifine.add(new MethodInsnNode(INVOKESTATIC, this.mappings.getClassPath("ClippingHelperImpl"), this.mappings.getFieldOrMethod("ClippingHelperImpl#getInstance"), this.mappings.getDescriptor("ClippingHelperImpl#getInstance"), false));
				searchListOptifine.add(new VarInsnNode(ASTORE, 9));
				
				int offset = -1;
				int offsetVanilla = ShoulderASMHelper.locateOffset(method.instructions, searchList);				
				int offsetOptifine = ShoulderASMHelper.locateOffset(method.instructions, searchListOptifine);
				
				if(offsetVanilla != -1)
				{
					offset = offsetVanilla;
				}
				else if(offsetOptifine != -1)
				{
					offset = offsetOptifine;
					ShoulderSurfing.LOGGER.info("Optifine detected");
				}
				
				if(offset == -1)
				{
					ShoulderSurfing.LOGGER.error("Failed to locate offset in " + method.name + method.desc + "! Is base file changed?");
					return bytes;
				}
				else
				{
					ShoulderSurfing.LOGGER.info("Located offset @" + offset);
					
					InsnList hackCode = new InsnList();
					hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "calculateRayTraceProjection", "()V", false));
					hackCode.add(new LabelNode(new Label()));
					
					method.instructions.insertBefore(method.instructions.get(offset + 1), hackCode);
					
					ShoulderSurfing.LOGGER.info("Injected code for raytrace projection!");
					this.modifications++;
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	private boolean methodMatches(MethodNode method, String name)
	{
		return method.name.equals(this.mappings.getFieldOrMethod(name)) && method.desc.equals(this.mappings.getDescriptor(name));
	}
}
