package com.github.exopandora.shouldersurfing.api.client;

import com.github.exopandora.shouldersurfing.legacy.adapter.ShoulderSurfingLegacyAdapter;
import com.github.exopandora.shouldersurfing.legacy.mixinduck.IShoulderSurfingLegacy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ShoulderSurfing {
	private static IShoulderSurfing adapter;
	
	public static IShoulderSurfing getInstance() {
		if (adapter == null) {
			adapter = (IShoulderSurfing) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class[] {IShoulderSurfing.class},
				new LegacyApiInvocationHandler(new ShoulderSurfingLegacyAdapter())
			);
		}
		return adapter;
	}
	
	private record LegacyApiInvocationHandler(IShoulderSurfingLegacy delegate) implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return method.invoke(this.delegate, args);
		}
	}
}
