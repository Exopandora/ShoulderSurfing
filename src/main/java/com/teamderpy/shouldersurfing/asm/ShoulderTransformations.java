package com.teamderpy.shouldersurfing.asm;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.collect.Sets;
import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.asm.transformer.IClassTransformer;
import com.teamderpy.shouldersurfing.asm.transformer.IMethodTransformer;
import com.teamderpy.shouldersurfing.asm.transformer.ITransformer;
import com.teamderpy.shouldersurfing.asm.transformer.clazz.TransformerPositionEyes;
import com.teamderpy.shouldersurfing.asm.transformer.method.TransformerCameraDistanceCheck;
import com.teamderpy.shouldersurfing.asm.transformer.method.TransformerCameraOrientation;
import com.teamderpy.shouldersurfing.asm.transformer.method.TransformerDistanceCheck;
import com.teamderpy.shouldersurfing.asm.transformer.method.TransformerRayTrace;
import com.teamderpy.shouldersurfing.asm.transformer.method.TransformerRayTraceProjection;
import com.teamderpy.shouldersurfing.asm.transformer.method.TransformerRenderAttackIndicator;
import com.teamderpy.shouldersurfing.asm.transformer.method.TransformerRenderCrosshair;
import com.teamderpy.shouldersurfing.asm.transformer.method.TransformerThirdPersonMode;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Joshua Powers <jsh.powers@yahoo.com>
 * @version 1.5
 * @since 2013-11-17
 */
@SideOnly(Side.CLIENT)
public class ShoulderTransformations implements net.minecraft.launchwrapper.IClassTransformer
{
	public static final int TOTAL_MODIFICATIONS = 9;
	public static int MODIFICATIONS = 0;
	
	private final Map<String, Entry<Set<IMethodTransformer>, Set<IClassTransformer>>> transformers = new HashMap<String, Entry<Set<IMethodTransformer>, Set<IClassTransformer>>>();
	private final Mappings mappings = new Mappings("assets/shouldersurfing/mappings/mappings.json");
	
	public ShoulderTransformations()
	{
		this.register(new TransformerCameraDistanceCheck());
		this.register(new TransformerCameraOrientation());
		this.register(new TransformerDistanceCheck());
		this.register(new TransformerRayTrace());
		this.register(new TransformerRayTraceProjection());
		this.register(new TransformerThirdPersonMode());
		this.register(new TransformerPositionEyes());
		this.register(new TransformerRenderCrosshair());
		this.register(new TransformerRenderAttackIndicator());
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if(this.transformers.containsKey(transformedName))
		{
			Entry<Set<IMethodTransformer>, Set<IClassTransformer>> transformers = this.transformers.get(transformedName);
			
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(bytes);
			classReader.accept(classNode, 0);
			
			if(!transformers.getKey().isEmpty())
			{
				for(Object m : classNode.methods)
				{
					MethodNode method = (MethodNode) m;
					
					for(IMethodTransformer transformer : transformers.getKey())
					{
						this.updateMappings(name, transformer);
						
						if(method.name.equals(this.mappings.getFieldOrMethod(transformer.getMethodName())) && method.desc.equals(this.mappings.getDescriptor(transformer.getMethodName())))
						{
							int offset = ShoulderASMHelper.locateOffset(method.instructions, transformer.getSearchList(this.mappings), transformer.ignoreLabels(), transformer.ignoreLineNumber());
							
							if(offset == -1)
							{
								ShoulderSurfing.LOGGER.error("Failed to locate offset in " + method.name + method.desc + " for " + transformer.getMethodName());
							}
							else
							{
								transformer.transform(method, this.mappings, offset);
								MODIFICATIONS++;
							}
						}
					}
				}
			}
			
			ClassWriter writer = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(writer);
			
			if(!transformers.getValue().isEmpty())
			{
				for(IClassTransformer transformer : transformers.getValue())
				{
					this.updateMappings(name, transformer);
					transformer.transform(writer, this.mappings);
				}
			}
			
			return writer.toByteArray();
		}
		
		return bytes;
	}
	
	private void updateMappings(String name, ITransformer transformer)
	{
		this.mappings.setObfuscated(!name.equals(this.mappings.getPackage(transformer.getClassName())));
	}
	
	private void register(ITransformer transformer)
	{
		String mapping = this.mappings.getClassPackage(transformer.getClassName());
		
		if(this.transformers.containsKey(mapping))
		{
			if(transformer instanceof IMethodTransformer)
			{
				this.transformers.get(mapping).getKey().add((IMethodTransformer) transformer);
			}
			else if(transformer instanceof IClassTransformer)
			{
				this.transformers.get(mapping).getValue().add((IClassTransformer) transformer);
			}
		}
		else
		{
			if(transformer instanceof IMethodTransformer)
			{
				this.transformers.put(mapping, new SimpleEntry<Set<IMethodTransformer>, Set<IClassTransformer>>(Sets.newHashSet((IMethodTransformer) transformer), Sets.newHashSet()));
			}
			else if(transformer instanceof IClassTransformer)
			{
				this.transformers.put(mapping, new SimpleEntry<Set<IMethodTransformer>, Set<IClassTransformer>>(Sets.newHashSet(), Sets.newHashSet((IClassTransformer) transformer)));
			}
		}
	}
}
