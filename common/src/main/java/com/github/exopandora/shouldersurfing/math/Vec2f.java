package com.github.exopandora.shouldersurfing.math;

import net.minecraft.util.math.MathHelper;

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
	
	public float x()
	{
		return this.x;
	}
	
	public float y()
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
	
	public Vec2f scale(Vec2f vec)
	{
		return new Vec2f(this.x * vec.x, this.y * vec.y);
	}
	
	public Vec2f divide(float div)
	{
		return new Vec2f(this.x / div, this.y / div);
	}
	
	public Vec2f divide(Vec2f vec)
	{
		return new Vec2f(this.x / vec.x, this.y / vec.y);
	}
	
	public Vec2f rotateDegrees(float angle)
	{
		return this.rotate(angle * MathUtil.DEG_TO_RAD);
	}
	
	public Vec2f rotate(float angle)
	{
		return new Vec2f(this.x * MathHelper.cos(angle) - this.y * MathHelper.sin(angle), this.x * MathHelper.sin(angle) + this.y * MathHelper.cos(angle));
	}
	
	public double lengthSquared()
	{
		return this.x * this.x + this.y * this.y;
	}
	
	public Vec2f lerp(Vec2f vec, float f)
	{
		return new Vec2f(MathHelper.lerp(f, this.x, vec.x), MathHelper.lerp(f, this.y, vec.y));
	}
	
	@Override
	public String toString()
	{
		return this.x + " " + this.y;
	}
}
