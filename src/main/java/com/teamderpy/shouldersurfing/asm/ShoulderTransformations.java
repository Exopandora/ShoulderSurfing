package com.teamderpy.shouldersurfing.asm;

import static org.objectweb.asm.Opcodes.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.teamderpy.shouldersurfing.ShoulderSurfing;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
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
	private final HashMap<String, String> obfStrings;
	private final HashMap<String, String> mcpStrings;
	public static final int CODE_MODIFICATIONS = 3;
	
	public static int modifications = 0;
	
	public ShoulderTransformations()
	{
		obfStrings = new HashMap<String, String>();
		mcpStrings = new HashMap<String, String>();
		
		/*
		 * net.minecraft.client.renderer.EntityRenderer orientCamera MD: ban/g
		 * (F)V net/minecraft/src/EntityRenderer/func_78467_g (F)V
		 */
		
		registerMapping("EntityRendererClass", "net.minecraft.client.renderer.EntityRenderer", "bnd");
		registerMapping("EntityRendererJavaClass", "net/minecraft/client/renderer/EntityRenderer", "bnd");
		registerMapping("EntityJavaClass", "net/minecraft/entity/Entity", "rr");
		registerMapping("orientCameraMethod", "orientCamera", "f");
		registerMapping("rotationYawField", "rotationYaw", "v");
		registerMapping("rotationPitchField", "rotationPitch", "w");
		
		registerMapping("SHOULDER_ROTATIONField", "SHOULDER_ROTATION", "SHOULDER_ROTATION");
		registerMapping("SHOULDER_ZOOM_MODField", "SHOULDER_ZOOM_MOD", "SHOULDER_ZOOM_MOD");
		registerMapping("InjectionDelegationJavaClass", "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "com/teamderpy/shouldersurfing/asm/InjectionDelegation");
		registerMapping("ShoulderRenderBinJavaClass", "com/teamderpy/shouldersurfing/renderer/ShoulderRenderBin", "com/teamderpy/shouldersurfing/renderer/ShoulderRenderBin");
		
		/*
		 * net.minecraft.client.renderer.EntityRenderer renderWorld MD: ban/a
		 * (FJ)V net/minecraft/src/EntityRenderer/func_78471_a (FJ)V
		 */
		
		registerMapping("renderWorldPassMethod", "renderWorldPass", "a");
		registerMapping("clippingHelperImplJavaClass", "net/minecraft/client/renderer/culling/ClippingHelperImpl", "bqk");
		registerMapping("clippingHelperJavaClass", "net/minecraft/client/renderer/culling/ClippingHelper", "bqm");
		registerMapping("clippingHelperGetInstanceMethod", "getInstance", "a");
		
		/*
		 * net.minecraft.client.gui.GuiIngame renderGameOverlay M D: atr/a
		 * (FZII)V net/minecraft/src/GuiIngame/func_73830_a (FZII)V
		 */
//		registerMapping("GuiIngameClass", "net.minecraft.client.gui.GuiIngame", "bcs");
//		registerMapping("GuiIngameJavaClass", "net/minecraft/client/gui/GuiIngame", "bcs");
//		registerMapping("GuiJavaClass", "net/minecraft/client/gui/Gui", "bct");
//		registerMapping("renderGameOverlayMethod", "renderGameOverlay", "a");
//		registerMapping("drawTexturedModalRectMethod", "drawTexturedModalRect", "a"); // Maybe "b"
		
		/*
		 * net.minecraft.client.renderer.entity.RenderPlayer renderPlayer
		 */
//		registerMapping("RenderPlayerClass", "net.minecraft.client.renderer.entity.RenderPlayer", "buh");
//		registerMapping("RenderPlayerJavaClass", "net/minecraft/client/renderer/entity/RenderPlayer", "buh");
//		registerMapping("renderPlayerMethod", "doRender", "a");
//		registerMapping("EntityPlayerJavaClass", "net/minecraft/entity/player/EntityPlayer", "zj");
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		// This lets us transform code whether or not it is obfuscated yet
		if(name.equals(obfStrings.get("EntityRendererClass")))
		{
			ShoulderSurfing.logger.info("Injecting into obfuscated code - EntityRendererClass");
			return transformEntityRenderClass(bytes, obfStrings);
		}
		else if(name.equals(mcpStrings.get("EntityRendererClass")))
		{
			ShoulderSurfing.logger.info("Injecting into non-obfuscated code - EntityRendererClass");
			return transformEntityRenderClass(bytes, mcpStrings);
		}
		// this next section uses a forge event now
		//
		// else if (name.equals(obfStrings.get("GuiIngameClass")))
		// {
		// ShoulderSurfing.logger.info("Injecting into obfuscated code");
		// return transformGuiIngameClass(bytes, obfStrings);
		// }
		// else if (name.equals(mcpStrings.get("GuiIngameClass")))
		// {
		// ShoulderSurfing.logger.info("Injecting into non-obfuscated code");
		// return transformGuiIngameClass(bytes, mcpStrings);
		// }
		// else if (name.equals(obfStrings.get("RenderPlayerClass")))
		// {
		// ShoulderSurfing.logger.info("Injecting into obfuscated code -
		// RenderPlayerClass");
		// return transformRenderPlayerClass(bytes, obfStrings);
		// }
		// else if (name.equals(mcpStrings.get("RenderPlayerClass")))
		// {
		// ShoulderSurfing.logger.info("Injecting into non-obfuscated code -
		// RenderPlayerClass");
		// return transformRenderPlayerClass(bytes, mcpStrings);
		// }
		
		return bytes;
	}
	
	/**
	 * {@link EntityRenderer}
	 * @param bytes
	 * @param hm
	 * @return
	 */
	private byte[] transformEntityRenderClass(byte[] bytes, HashMap hm)
	{
		ShoulderSurfing.logger.info("Attempting class transformation against EntityRender");
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		// Find method
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			if(m.name.equals(hm.get("orientCameraMethod")) && m.desc.equals("(F)V"))
			{
				ShoulderSurfing.logger.info("Located method " + m.name + m.desc + ", locating signature");
				
				// Locate injection point, after the yaw and pitch fields in the
				// camera function
				InsnList searchList = new InsnList();
				searchList.add(new VarInsnNode(ALOAD, 2));
				searchList.add(new FieldInsnNode(GETFIELD, (String) hm.get("EntityJavaClass"), (String) hm.get("rotationYawField"), "F"));
				searchList.add(new VarInsnNode(FSTORE, 12));
				searchList.add(new VarInsnNode(ALOAD, 2));
				searchList.add(new FieldInsnNode(GETFIELD, (String) hm.get("EntityJavaClass"), (String) hm.get("rotationPitchField"), "F"));
				searchList.add(new VarInsnNode(FSTORE, 13));
				
				int offset = ShoulderASMHelper.locateOffset(m.instructions, searchList);
				if(offset == -1)
				{
					ShoulderSurfing.logger.error("Failed to locate first of two offsets in " + m.name + m.desc + "! Is base file changed?");
					return bytes;
				}
				else
				{
					ShoulderSurfing.logger.info("Located offset @ " + offset);
					InsnList hackCode = new InsnList();

					//net/minecraft/client/renderer/EntityRenderer:653
					//f1 += InjectionDelegation.getShoulderRotation();
					
					hackCode.add(new VarInsnNode(FLOAD, 12));
					hackCode.add(new MethodInsnNode(INVOKESTATIC, (String) hm.get("InjectionDelegationJavaClass"), "getShoulderRotation", "()F", false));
					hackCode.add(new InsnNode(FADD));
					hackCode.add(new VarInsnNode(FSTORE, 12));
					
					//net/minecraft/client/renderer/EntityRenderer:654
					//d3 *= InjectionDelegation.getShoulderZoomMod();
					
					hackCode.add(new VarInsnNode(DLOAD, 10));
					hackCode.add(new MethodInsnNode(INVOKESTATIC, (String) hm.get("InjectionDelegationJavaClass"), "getShoulderZoomMod", "()F", false));
					hackCode.add(new InsnNode(F2D));
					hackCode.add(new InsnNode(DMUL));
					hackCode.add(new VarInsnNode(DSTORE, 10));
					
					hackCode.add(new LabelNode(new Label()));
					m.instructions.insertBefore(m.instructions.get(offset + 1), hackCode);
					ShoulderSurfing.logger.info("Injected code for camera orientation!");
					modifications++;
				}
				
				// Locate second injection point, after the reverse raytrace is
				// performed
				searchList = new InsnList();
				searchList.add(new VarInsnNode(DSTORE, 25));
				searchList.add(new VarInsnNode(DLOAD, 25));
				
				offset = ShoulderASMHelper.locateOffset(m.instructions, searchList);
				if(offset == -1)
				{
					ShoulderSurfing.logger.error("Failed to locate second of two offsets in " + m.name + m.desc + "! Is base file changed?");
					return bytes;
				}
				else
				{
					ShoulderSurfing.logger.info("Located offset @ " + offset);
					InsnList hackCode = new InsnList();
					hackCode.add(new VarInsnNode(DLOAD, 25));
					hackCode.add(new MethodInsnNode(INVOKESTATIC, (String) hm.get("InjectionDelegationJavaClass"), "verifyReverseBlockDist", "(D)V", false));
					hackCode.add(new LabelNode(new Label()));
					m.instructions.insertBefore(m.instructions.get(offset), hackCode);
					ShoulderSurfing.logger.info("Injected code for camera distance check!");
					modifications++;
				}
			}
			else if(m.name.equals(hm.get("renderWorldPassMethod")) && m.desc.equals("(IFJ)V"))
			{
				ShoulderSurfing.logger.info("Located method " + m.name + m.desc + ", locating signature");
				
				// Locate injection point, after the clipping helper returns an
				// instance
				InsnList searchList = new InsnList();
				searchList.add(new MethodInsnNode(INVOKESTATIC, (String) hm.get("clippingHelperImplJavaClass"), (String) hm.get("clippingHelperGetInstanceMethod"), "()L" + (String) hm.get("clippingHelperJavaClass") + ";", false));
				searchList.add(new InsnNode(POP));
				
				int offset = ShoulderASMHelper.locateOffset(m.instructions, searchList);
				if(offset == -1)
				{
					ShoulderSurfing.logger.error("Failed to locate offset in " + m.name + m.desc + "! Is base file changed?");
					return bytes;
				}
				else
				{
					ShoulderSurfing.logger.info("Located offset @ " + offset);
					InsnList hackCode = new InsnList();
					hackCode.add(new MethodInsnNode(INVOKESTATIC, (String) hm.get("InjectionDelegationJavaClass"), "calculateRayTraceProjection", "()V", false));
					hackCode.add(new LabelNode(new Label()));
					m.instructions.insertBefore(m.instructions.get(offset + 1), hackCode);
					ShoulderSurfing.logger.info("Injected code for ray trace projection!");
					modifications++;
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
	
	private void registerMapping(String key, String normalValue, String obfuscatedValue)
	{
		mcpStrings.put(key, normalValue);
		obfStrings.put(key, obfuscatedValue);
	}
}
