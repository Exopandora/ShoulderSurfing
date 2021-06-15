package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransformerPositionEyes extends ShoulderTransformer
{
	@Override
	public String getClassId()
	{
		return "EntityPlayer";
	}
	
	@Override
	public String getMethodId()
	{
		return "EntityPlayer#getPositionEyes";
	}
	
	@Override
	public void transform(Mappings mappings, boolean obf, ClassWriter writer)
	{
		String vec3d = mappings.map("Vec3d", obf);
		String entity = mappings.map("Entity", obf);
		String eyes = mappings.map("EntityPlayer#getPositionEyes", obf);
		
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
	
	@Override
	protected boolean hasMethodTransformer()
	{
		return false;
	}
	
	@Override
	protected boolean hasClassTransformer()
	{
		return true;
	}
}
