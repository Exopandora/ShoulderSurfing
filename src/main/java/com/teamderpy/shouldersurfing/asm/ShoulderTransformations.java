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
import static org.objectweb.asm.Opcodes.POP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
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

import com.google.gson.Gson;
import com.teamderpy.shouldersurfing.json.JsonShoulderSurfing;
import com.teamderpy.shouldersurfing.json.JsonShoulderSurfing.JsonVersions;
import com.teamderpy.shouldersurfing.json.JsonShoulderSurfing.JsonVersions.JsonMappings.JsonMapping;
import com.teamderpy.shouldersurfing.util.Util;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.common.ForgeVersion;
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
	private final HashMap<String, JsonMapping> mappings = new HashMap<String, JsonMapping>();
	
	public static final int CODE_MODIFICATIONS = 3;
	
	public static int modifications = 0;
	
	public ShoulderTransformations()
	{
		String version = ForgeVersion.mcVersion;
		JsonShoulderSurfing json = new JsonShoulderSurfing();
		
		try
		{
			version = ForgeVersion.class.getDeclaredField("mcVersion").get(ForgeVersion.class).toString();
			
			InputStream in = getClass().getClassLoader().getResourceAsStream("assets/shouldersurfing/mappings/mappings.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			json = new Gson().fromJson(reader, JsonShoulderSurfing.class);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		for(JsonVersions versions : json.getVersions())
		{			
			if(versions.getVersion().equals(version))
			{
				// System.out.println("Found version " + versions.getVersion());
				
				for(JsonMapping clazz : versions.getMappings().getClasses())
				{
					// System.out.println("Found class " + clazz.getName());
					this.mappings.put(clazz.getName() + "Class", clazz);
				}
				
				for(JsonMapping method : versions.getMappings().getMethods())
				{
					// System.out.println("Found method " + method.getName());
					this.mappings.put(method.getName() + "Method", method);
				}
				
				for(JsonMapping field : versions.getMappings().getFields())
				{
					// System.out.println("Found field " + field.getName());
					this.mappings.put(field.getName() + "Field", field);
				}
				
				Util.LOGGER.info("Loaded mappings for Minecraft " + versions.getVersion());
				return;
			}
		}
		
		Util.LOGGER.error("No mappings found for Minecraft " + ForgeVersion.mcVersion);
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if(name.equals(this.mappings.get("EntityRendererClass").getObf()))
		{
			Util.LOGGER.info("Injecting into obfuscated code - EntityRendererClass");
			return transformEntityRenderClass(bytes, true);
		}
		else if(name.equals(this.mappings.get("EntityRendererClass").getPackage()))
		{
			Util.LOGGER.info("Injecting into non-obfuscated code - EntityRendererClass");
			return transformEntityRenderClass(bytes, false);
		}
		
		return bytes;
	}
	
	/**
	 * Transforms {@link EntityRenderer}
	 * 
	 * @param bytes
	 * @param hm
	 * @return
	 */
	private byte[] transformEntityRenderClass(byte[] bytes, boolean isObfuscated)
	{
		Util.LOGGER.info("Attempting class transformation against EntityRender");
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		// Find method
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if(m.name.equals(this.mappings.get("orientCameraMethod").getFieldOrMethod(isObfuscated)) && m.desc.equals(this.mappings.get("orientCameraMethod").getDescriptor()))
			{
				Util.LOGGER.info("Located method " + m.name + m.desc + ", locating signature");
				
				// Locate injection point, after the yaw and pitch fields in the
				// camera function
				InsnList searchList = new InsnList();
				searchList.add(new VarInsnNode(ALOAD, 2));
				searchList.add(new FieldInsnNode(GETFIELD, this.mappings.get("EntityClass").getClassPath(isObfuscated), this.mappings.get("rotationYawField").getFieldOrMethod(isObfuscated), this.mappings.get("rotationYawField").getDescriptor()));
				searchList.add(new VarInsnNode(FSTORE, 12));
				searchList.add(new VarInsnNode(ALOAD, 2));
				searchList.add(new FieldInsnNode(GETFIELD, this.mappings.get("EntityClass").getClassPath(isObfuscated), this.mappings.get("rotationPitchField").getFieldOrMethod(isObfuscated), this.mappings.get("rotationPitchField").getDescriptor()));
				searchList.add(new VarInsnNode(FSTORE, 13));
				
				int offset = ShoulderASMHelper.locateOffset(m.instructions, searchList);
				if(offset == -1)
				{
					Util.LOGGER.error("Failed to locate first of two offsets in " + m.name + m.desc + "! Is base file changed?");
					return bytes;
				}
				else
				{
					Util.LOGGER.info("Located offset @ " + offset);
					InsnList hackCode = new InsnList();
					
					// net/minecraft/client/renderer/EntityRenderer:653
					// f1 += InjectionDelegation.getShoulderRotation();
					
					hackCode.add(new VarInsnNode(FLOAD, 12));
					hackCode.add(new MethodInsnNode(INVOKESTATIC, this.mappings.get("InjectionDelegationClass").getClassPath(isObfuscated), "getShoulderRotation", "()F", false));
					hackCode.add(new InsnNode(FADD));
					hackCode.add(new VarInsnNode(FSTORE, 12));
					
					// net/minecraft/client/renderer/EntityRenderer:654
					// d3 *= InjectionDelegation.getShoulderZoomMod();
					
					hackCode.add(new VarInsnNode(DLOAD, 10));
					hackCode.add(new MethodInsnNode(INVOKESTATIC, this.mappings.get("InjectionDelegationClass").getClassPath(isObfuscated), "getShoulderZoomMod", "()F", false));
					hackCode.add(new InsnNode(F2D));
					hackCode.add(new InsnNode(DMUL));
					hackCode.add(new VarInsnNode(DSTORE, 10));
					
					hackCode.add(new LabelNode(new Label()));
					m.instructions.insertBefore(m.instructions.get(offset + 1), hackCode);
					Util.LOGGER.info("Injected code for camera orientation!");
					this.modifications++;
				}
				
				// Locate second injection point, after the reverse raytrace is
				// performed
				searchList = new InsnList();
				searchList.add(new VarInsnNode(DSTORE, 25));
				searchList.add(new VarInsnNode(DLOAD, 25));
				
				offset = ShoulderASMHelper.locateOffset(m.instructions, searchList);
				if(offset == -1)
				{
					Util.LOGGER.error("Failed to locate second of two offsets in " + m.name + m.desc + "! Is base file changed?");
					return bytes;
				}
				else
				{
					Util.LOGGER.info("Located offset @ " + offset);
					InsnList hackCode = new InsnList();
					hackCode.add(new VarInsnNode(DLOAD, 25));
					hackCode.add(new MethodInsnNode(INVOKESTATIC, this.mappings.get("InjectionDelegationClass").getClassPath(isObfuscated), "verifyReverseBlockDist", "(D)V", false));
					hackCode.add(new LabelNode(new Label()));
					m.instructions.insertBefore(m.instructions.get(offset), hackCode);
					Util.LOGGER.info("Injected code for camera distance check!");
					this.modifications++;
				}
			}
			else if(m.name.equals(this.mappings.get("renderWorldPassMethod").getFieldOrMethod(isObfuscated)) && m.desc.equals(this.mappings.get("renderWorldPassMethod").getDescriptor()))
			{
				Util.LOGGER.info("Located method " + m.name + m.desc + ", locating signature");
				
				// Locate injection point, after the clipping helper returns an
				// instance
				
				InsnList searchList = new InsnList();
				searchList.add(new MethodInsnNode(INVOKESTATIC, this.mappings.get("ClippingHelperImplClass").getClassPath(isObfuscated), this.mappings.get("getInstanceMethod").getFieldOrMethod(isObfuscated), this.mappings.get("getInstanceMethod").getDescriptor() + this.mappings.get("ClippingHelperClass").getClassPath(isObfuscated) + ";", false));
				searchList.add(new InsnNode(POP));
				
				InsnList searchListOptifine = new InsnList();
				searchListOptifine.add(new MethodInsnNode(INVOKESTATIC, this.mappings.get("ClippingHelperImplClass").getClassPath(isObfuscated), this.mappings.get("getInstanceMethod").getFieldOrMethod(isObfuscated), this.mappings.get("getInstanceMethod").getDescriptor() + this.mappings.get("ClippingHelperClass").getClassPath(isObfuscated) + ";", false));
				searchListOptifine.add(new VarInsnNode(ASTORE, 9));
				
				int offset = -1;
				int offsetVanilla = ShoulderASMHelper.locateOffset(m.instructions, searchList);				
				int offsetOptifine = ShoulderASMHelper.locateOffset(m.instructions, searchListOptifine);
				
				if(offsetVanilla != -1)
				{
					offset = offsetVanilla;
				}
				else if(offsetOptifine != -1)
				{
					offset = offsetOptifine;
					Util.LOGGER.info("Optifine detected");
				}
				
				if(offset == -1)
				{
					Util.LOGGER.error("Failed to locate offset in " + m.name + m.desc + "! Is base file changed?");
					return bytes;
				}
				else
				{
					Util.LOGGER.info("Located offset @ " + offset);
					InsnList hackCode = new InsnList();
					hackCode.add(new MethodInsnNode(INVOKESTATIC, this.mappings.get("InjectionDelegationClass").getClassPath(isObfuscated), "calculateRayTraceProjection", "()V", false));
					hackCode.add(new LabelNode(new Label()));
					m.instructions.insertBefore(m.instructions.get(offset + 1), hackCode);
					Util.LOGGER.info("Injected code for ray trace projection!");
					this.modifications++;
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
