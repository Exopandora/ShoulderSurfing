package com.teamderpy.shouldersurfing.asm.transformer.clazz;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.transformer.IClassTransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransformerPositionEyes implements IClassTransformer
{
	@Override
	public String getClassName()
	{
		return "EntityPlayer";
	}
	
	@Override
	public String getMethodName()
	{
		return "EntityPlayer#getPositionEyes";
	}
	
	@Override
	public void transform(ClassWriter writer, Mappings mappings)
	{
		String vec3d = mappings.getClassPath("Vec3d");
		String entity = mappings.getClassPath("Entity");
		String eyes = mappings.getFieldOrMethod("EntityPlayer#getPositionEyes");
		
		Method method = Method.getMethod(vec3d + " " + eyes + " (float)", true);
		GeneratorAdapter adapter = new GeneratorAdapter(ACC_PUBLIC, method, null, null, writer);
		
		//return InjectionDelegation.getPositionEyes(this, super.getPositionEyes(partialTicks));
		
		adapter.loadThis();
		adapter.loadThis();
		adapter.loadArg(0);
		adapter.invokeConstructor(Type.getType(entity), method);
		adapter.invokeStatic(Type.getType("com/teamderpy/shouldersurfing/asm/InjectionDelegation"), Method.getMethod(vec3d + " getPositionEyes (" + entity + ", " + vec3d + ")", true));
		
		adapter.returnValue();
		adapter.endMethod();
	}
}
