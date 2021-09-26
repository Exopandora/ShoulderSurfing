package com.teamderpy.shouldersurfing.math;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Vec2f
{
	public static final Vec2f ZERO = new Vec2f(0, 0);
	
	private final float x;
	private final float y;
	
	public Vec2f(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public float getX()
	{
		return this.x;
	}
	
	public float getY()
	{
		return this.y;
	}
	
	public Vec2f negate()
	{
		return new Vec2f(-this.x, -this.y);
	}
	
	public Vec2f add(Vec2f vec)
	{
		return new Vec2f(this.x + vec.x, this.y + vec.y);
	}
	
	public Vec2f subtract(Vec2f vec)
	{
		return new Vec2f(this.x - vec.x, this.y - vec.y);
	}
	
	public Vec2f scale(float scale)
	{
		return new Vec2f(this.x * scale, this.y * scale);
	}
	
	public Vec2f divide(float div)
	{
		return new Vec2f(this.x / div, this.y / div);
	}
	
	@Override
	public String toString()
	{
		return this.x + " " + this.y;
	}
}
