package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;

public abstract class PickContext
{
	private final ActiveRenderInfo camera;
	private final RayTraceContext.FluidMode fluidContext;
	private final Entity entity;
	
	protected PickContext(ActiveRenderInfo camera, RayTraceContext.FluidMode fluidContext, Entity entity)
	{
		this.camera = camera;
		this.fluidContext = fluidContext;
		this.entity = entity;
	}
	
	public abstract RayTraceContext.BlockMode blockContext();
	
	public abstract Couple<Vector3d> entityTrace(double interactionRange, float partialTick);
	
	public abstract Couple<Vector3d> blockTrace(double interactionRange, float partialTick);
	
	public RayTraceContext toClipContext(double interactionRange, float partialTick)
	{
		Couple<Vector3d> blockTrace = this.blockTrace(interactionRange, partialTick);
		return new RayTraceContext(blockTrace.left(), blockTrace.right(), this.blockContext(), this.fluidContext(), this.entity());
	}
	
	public ActiveRenderInfo camera()
	{
		return this.camera;
	}
	
	public RayTraceContext.FluidMode fluidContext()
	{
		return this.fluidContext;
	}
	
	public Entity entity()
	{
		return this.entity;
	}
	
	public static class Builder
	{
		private final ActiveRenderInfo camera;
		private RayTraceContext.FluidMode fluidContext;
		private Entity entity;
		private Boolean offsetTrace = null;
		
		public Builder(ActiveRenderInfo camera)
		{
			this.camera = camera;
		}
		
		public Builder withFluidContext(RayTraceContext.FluidMode fluidContext)
		{
			this.fluidContext = fluidContext;
			return this;
		}
		
		public Builder withEntity(Entity entity)
		{
			this.entity = entity;
			return this;
		}
		
		public Builder dynamicTrace()
		{
			this.offsetTrace = false;
			return this;
		}
		
		public Builder offsetTrace()
		{
			this.offsetTrace = true;
			return this;
		}
		
		public PickContext build()
		{
			Entity entity = this.entity == null ? Minecraft.getInstance().getCameraEntity() : this.entity;
			RayTraceContext.FluidMode fluidContext = this.fluidContext == null ? RayTraceContext.FluidMode.NONE : this.fluidContext;
			boolean offsetTrace = this.offsetTrace == null ?
				!ShoulderSurfing.getInstance().getCrosshairRenderer().isCrosshairDynamic(entity) : this.offsetTrace;
			
			if(offsetTrace)
			{
				return new OffsetPickContext(this.camera, fluidContext, entity);
			}
			
			return new DynamicPickContext(this.camera, fluidContext, entity);
		}
	}
}
