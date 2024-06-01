package com.github.exopandora.shouldersurfing.api.client;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public class ShoulderSurfing
{
	private static final IShoulderSurfing INSTANCE = find(ServiceLoader.load(IShoulderSurfing.class));
	
	public static IShoulderSurfing getInstance()
	{
		return INSTANCE;
	}
	
	private static <S> S find(ServiceLoader<S> serviceLoader)
	{
		Iterator<S> iterator = serviceLoader.iterator();
		
		if(iterator.hasNext())
		{
			return iterator.next();
		}
		
		throw new NoSuchElementException();
	}
}
