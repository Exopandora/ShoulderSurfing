package com.github.exopandora.shouldersurfing.api.model;

public class Couple<T>
{
	private final T left;
	private final T right;
	
	public Couple(T left, T right)
	{
		this.left = left;
		this.right = right;
	}
	
	public T left()
	{
		return this.left;
	}
	
	public T right()
	{
		return this.right;
	}
}
