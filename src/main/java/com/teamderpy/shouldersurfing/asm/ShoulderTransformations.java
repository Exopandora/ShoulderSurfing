package com.teamderpy.shouldersurfing.asm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.asm.transformer.ITransformer;
import com.teamderpy.shouldersurfing.asm.transformer.TransformerCameraDistanceCheck;
import com.teamderpy.shouldersurfing.asm.transformer.TransformerCameraOrientation;
import com.teamderpy.shouldersurfing.asm.transformer.TransformerDistanceCheck;
import com.teamderpy.shouldersurfing.asm.transformer.TransformerRayTrace;
import com.teamderpy.shouldersurfing.asm.transformer.TransformerRayTraceProjection;
import com.teamderpy.shouldersurfing.asm.transformer.TransformerThirdPersonMode;

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
	
	private final Map<String, Set<ITransformer>> transformers = new HashMap<String, Set<ITransformer>>();
	private final Mappings mappings = new Mappings("assets/shouldersurfing/mappings/mappings.json");
	
	public ShoulderTransformations()
	{
		this.register(new TransformerCameraDistanceCheck());
		this.register(new TransformerCameraOrientation());
		this.register(new TransformerDistanceCheck());
		this.register(new TransformerRayTrace());
		this.register(new TransformerRayTraceProjection());
		this.register(new TransformerThirdPersonMode());
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if(this.transformers.containsKey(transformedName))
		{
			return this.transformMethods(bytes, name, this.transformers.get(transformedName));
		}
		
		return bytes;
	}
	
	private byte[] transformMethods(byte[] bytes, String name, Set<ITransformer> transformers)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		for(MethodNode method : classNode.methods)
		{
			for(ITransformer transformer : transformers)
			{
				this.mappings.setObfuscated(!name.equals(this.mappings.getPackage(transformer.getClassName())));
				
				if(this.methodMatches(method, transformer.getMethodName()))
				{
					int offset = ShoulderASMHelper.locateOffset(method.instructions, transformer.getSearchList(this.mappings), transformer.ignoreLabels(), transformer.ignoreLineNumber());
					
					if(offset == -1)
					{
						ShoulderSurfing.LOGGER.error("Failed to locate offset in " + method.name + method.desc + " for " + transformer.getMethodName());
					}
					else
					{
						transformer.transform(method, transformer.getInjcetionList(this.mappings), offset);
						MODIFICATIONS++;
					}
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
	
	private void register(ITransformer transformer)
	{
		String mapping = this.mappings.getClassPackage(transformer.getClassName());
		
		if(this.transformers.containsKey(mapping))
		{
			this.transformers.get(mapping).add(transformer);
		}
		else
		{
			this.transformers.put(mapping, new HashSet<ITransformer>(Arrays.asList(transformer)));
		}
	}
}
