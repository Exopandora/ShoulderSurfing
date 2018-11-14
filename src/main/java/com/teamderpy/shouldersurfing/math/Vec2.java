package com.teamderpy.shouldersurfing.math;

public class Vec2<T>
{
	private T x;
	private T y;
	
	public Vec2(T x, T y)
	{
		this.x = x;
		this.y = y;
	}
	
	public T getX()
	{
		return this.x;
	}
	
	public void setX(T x)
	{
		this.x = x;
	}
	
	public T getY()
	{
		return this.y;
	}
	
	public void setY(T y)
	{
		this.y = y;
	}
}
