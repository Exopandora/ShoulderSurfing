package com.github.exopandora.shouldersurfing.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

class CallbackInvocationHandler<T> implements InvocationHandler
{
	private final PluginContext context;
	private final T delegate;
	private final Set<Method> knownMethods;
	
	protected CallbackInvocationHandler(PluginContext context, T delegate, Set<Class<?>> knownInterfaces)
	{
		this.context = context;
		this.delegate = delegate;
		this.knownMethods = knownInterfaces.stream().flatMap(klass -> Arrays.stream(klass.getDeclaredMethods())).collect(Collectors.toSet());
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		if(this.knownMethods.contains(method))
		{
			try
			{
				return method.invoke(this.delegate, args);
			}
			catch(Throwable t)
			{
				throw createExceptionWithContext(this.context, t);
			}
		}
		
		return method.invoke(this.delegate, args);
	}
	
	private static RuntimeException createExceptionWithContext(PluginContext context, Throwable t)
	{
		return new RuntimeException("Shoulder Surfing Reloaded encountered an unexpected error while trying to execute a callback for the plugin provided by " + context.formattedModName() + ". Please report this crash to " + context.formattedModName() + ".", t);
	}
}
