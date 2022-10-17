package com.teamderpy.shouldersurfing.asm.transformers;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import com.teamderpy.shouldersurfing.asm.Mappings;
import com.teamderpy.shouldersurfing.asm.ShoulderTransformer;

public class EntityPlayerRayTrace extends ShoulderTransformer
{
	@Override
	public void transform(Mappings mappings, boolean obf, ClassWriter writer)
	{
		// @Override
		// public MovingObjectPosition rayTrace(double blockReachDistance, float partialTicks)
		// {
		//     return InjectionDelegation.EntityPlayer_rayTrace(this, blockReachDistance, partialTicks);
		// }
		
		String rayTraceResult = mappings.map("RayTraceResult", obf);
		String entity = mappings.map("EntityLivingBase", obf);
		String rayTrace = mappings.map("EntityLivingBase#rayTrace", obf);
		Method method = Method.getMethod(rayTraceResult + " " + rayTrace + " (double, float)", true);
		GeneratorAdapter adapter = new GeneratorAdapter(ACC_PUBLIC, method, null, null, writer);
		adapter.loadThis();
		adapter.loadArg(0);
		adapter.loadArg(1);
		adapter.invokeStatic(Type.getType("com/teamderpy/shouldersurfing/asm/InjectionDelegation"), Method.getMethod(rayTraceResult + " EntityPlayer_rayTrace (" + entity + ", double, float)", true));
		adapter.returnValue();
		adapter.endMethod();
	}
	
	@Override
	public String getClassId()
	{
		return "EntityPlayer";
	}
	
	@Override
	public String getMethodId()
	{
		return null;
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
