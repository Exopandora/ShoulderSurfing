package com.github.exopandora.shouldersurfing.math;

import net.minecraft.util.Mth;

public record Vec2f(float x, float y)
{
	public static final Vec2f ZERO = new Vec2f(0, 0);
	
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
		return this.rotate(angle * Mth.DEG_TO_RAD);
	}
	
	public Vec2f rotate(float angle)
	{
		return new Vec2f(this.x * Mth.cos(angle) - this.y * Mth.sin(angle), this.x * Mth.sin(angle) + this.y * Mth.cos(angle));
	}
	
	public double lengthSquared()
	{
		return this.x * this.x + this.y * this.y;
	}
	
	public Vec2f lerp(Vec2f vec, float f)
	{
		return new Vec2f(Mth.lerp(f, this.x, vec.x), Mth.lerp(f, this.y, vec.y));
	}
	
	@Override
	public String toString()
	{
		return this.x + " " + this.y;
	}
}
