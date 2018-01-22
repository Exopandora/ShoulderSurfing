package com.teamderpy.shouldersurfing.asm;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DMUL;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.F2D;
import static org.objectweb.asm.Opcodes.FADD;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FNEG;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.POP;

import java.util.Iterator;
import java.util.function.Function;

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
	public static final int TOTAL_MODIFICATIONS = 6;
	public static int MODIFICATIONS = 0;
	private final ShoulderMappings mappings = new ShoulderMappings();
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if(name.equals(this.mappings.getObf("EntityRenderer")))
		{
			return transformClass(bytes, true, this::transformEntityRender);
		}
		else if(name.equals(this.mappings.getPackage("EntityRenderer")))
		{
			return transformClass(bytes, false, this::transformEntityRender);
		}
		else if(name.equals(this.mappings.getObf("Minecraft")))
		{
			return transformClass(bytes, true, this::transformMinecraft);
		}
		else if(name.equals(this.mappings.getPackage("Minecraft")))
		{
			return transformClass(bytes, false, this::transformMinecraft);
		}
		
		return bytes;
	}
	
	private byte[] transformClass(byte[] bytes, boolean obfuscated, Function<MethodNode, Boolean> transformer)
	{
		ShoulderSurfing.LOGGER.info(obfuscated ? "Injecting into obfuscated code" : "Injecting into non-obfuscated code");
		this.mappings.setObfuscated(obfuscated);
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		Iterator<MethodNode> methods = classNode.methods.iterator();
		
		while(methods.hasNext())
		{
			MethodNode method = methods.next();
			
			if(!transformer.apply(method))
			{
				return bytes;
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	private boolean transformEntityRender(MethodNode method)
	{
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
				ShoulderSurfing.LOGGER.error("Failed to locate first of four offsets in " + method.name + method.desc + "! Is base file changed?");
				return false;
			}
			else
			{
				ShoulderSurfing.LOGGER.info("Located offset @" + offset);
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
				
				method.instructions.insertBefore(method.instructions.get(offset + 1), hackCode);
				
				ShoulderSurfing.LOGGER.info("Injected code for camera orientation!");
				MODIFICATIONS++;
			}
			
			// Locate second injection point, after the reverse ray trace is performed
			
			searchList = new InsnList();
			searchList.add(new VarInsnNode(DSTORE, 25));
			searchList.add(new VarInsnNode(DLOAD, 25));
			
			offset = ShoulderASMHelper.locateOffset(method.instructions, searchList);
			
			if(offset == -1)
			{
				ShoulderSurfing.LOGGER.error("Failed to locate second of four offsets in " + method.name + method.desc + "! Is base file changed?");
				return false;
			}
			else
			{
				ShoulderSurfing.LOGGER.info("Located offset @" + offset);
				
				InsnList hackCode = new InsnList();
				
				// net/minecraft/client/renderer/EntityRenderer.orientCamera:658
				// InjectionDelegation.verifyReverseBlockDist(d7);
				
				hackCode.add(new VarInsnNode(DLOAD, 25));
				hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "verifyReverseBlockDist", "(D)V", false));
				hackCode.add(new LabelNode(new Label()));
				
				method.instructions.insertBefore(method.instructions.get(offset), hackCode);
				
				ShoulderSurfing.LOGGER.info("Injected code for alternative camera distance check!");
				MODIFICATIONS++;
			}
			
			// Locate third injection point, when the ray trace is performed
			
			searchList = new InsnList();
			searchList.add(new MethodInsnNode(INVOKEVIRTUAL, this.mappings.getClassPath("WorldClient"), this.mappings.getFieldOrMethod("WorldClient#rayTraceBlocks"), this.mappings.getDescriptor("WorldClient#rayTraceBlocks"), false));
			
			offset = ShoulderASMHelper.locateOffset(method.instructions, searchList);
			
			if(offset == -1)
			{
				ShoulderSurfing.LOGGER.error("Failed to locate third of four offsets in " + method.name + method.desc + "! Is base file changed?");
				return false;
			}
			else
			{
				ShoulderSurfing.LOGGER.info("Located offset @" + offset);
				
				// net/minecraft/client/renderer/EntityRenderer.orientCamera:653
				// InjectionDelegation.getRayTraceResult(this.mc.world, Vec3d, Vec3d);
				
				method.instructions.set(method.instructions.get(offset), new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getRayTraceResult", this.mappings.getDescriptor("InjectionDelegation#getRayTraceResult"), false));
				
				ShoulderSurfing.LOGGER.info("Injected code for ray trace!");
				MODIFICATIONS++;
			}
			
			// Locate fourth injection point before the distance check is performed
			
			searchList = new InsnList();
			searchList.add(new InsnNode(FNEG));
			searchList.add(new InsnNode(F2D));
			searchList.add(new VarInsnNode(DLOAD, 10));
			searchList.add(new InsnNode(DMUL));
			searchList.add(new VarInsnNode(DSTORE, 18));
			
			offset = ShoulderASMHelper.locateOffset(method.instructions, searchList);
			
			if(offset == 1)
			{
				ShoulderSurfing.LOGGER.error("Failed to locate fourth of four offsets in " + method.name + method.desc + "! Is base file changed?");
				return false;
			}
			else
			{
				ShoulderSurfing.LOGGER.info("Located offset @" + offset);
				
				InsnList hackCode = new InsnList();
				
				// net/minecraft/client/renderer/EntityRenderer.orientCamera:644
				//d3 = InjectionDelegation.checkDistance(d3, f1, d0, d1, d2, d4, d5, d6);
				
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
				
				method.instructions.insert(method.instructions.get(offset + 1), hackCode);
				
				ShoulderSurfing.LOGGER.info("Injected code for camera distance check!");
				MODIFICATIONS++;
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
				return false;
			}
			else
			{
				ShoulderSurfing.LOGGER.info("Located offset @" + offset);
				
				InsnList hackCode = new InsnList();
				
				// net/minecraft/client/renderer/EntityRenderer.renderWorldPass:1332
				// InjectionDelegation.calculateRayTraceProjection();
				
				hackCode.add(new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "calculateRayTraceProjection", "()V", false));
				hackCode.add(new LabelNode(new Label()));
				
				method.instructions.insert(method.instructions.get(offset + 1), hackCode);
				
				ShoulderSurfing.LOGGER.info("Injected code for raytrace projection!");
				MODIFICATIONS++;
			}
		}
		
		return true;
	}
	
	private boolean transformMinecraft(MethodNode method)
	{
		if(this.methodMatches(method, "Minecraft#processKeyBinds"))
		{
			ShoulderSurfing.LOGGER.info("Located method " + method.name + method.desc + ", locating signature");
			
			//Find injection point when thirdPersonView is compared against 2
			
			InsnList searchList = new InsnList();
			searchList.add(new FieldInsnNode(GETFIELD, this.mappings.getClassPath("GameSettings"), this.mappings.getFieldOrMethod("GameSettings#thirdPersonView"), this.mappings.getDescriptor("GameSettings#thirdPersonView")));
			searchList.add(new InsnNode(ICONST_2));
			
			int offset = ShoulderASMHelper.locateOffset(method.instructions, searchList);
			
			if(offset == -1)
			{
				ShoulderSurfing.LOGGER.error("Failed to locate offset in " + method.name + method.desc + "! Is base file changed?");
				return false;
			}
			else
			{
				//Set comparison value to InjectionDelegation.getMax3ppId()
				method.instructions.set(method.instructions.get(offset), new MethodInsnNode(INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getMax3ppId", "()I", false));
				
				ShoulderSurfing.LOGGER.info("Injected code new third person mode!");
				MODIFICATIONS++;
			}
		}
		
		return true;
	}
	
	private boolean methodMatches(MethodNode method, String name)
	{
		return method.name.equals(this.mappings.getFieldOrMethod(name)) && method.desc.equals(this.mappings.getDescriptor(name));
	}
}
