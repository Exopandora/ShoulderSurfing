package com.github.exopandora.shouldersurfing.api.model;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

public sealed abstract class PickContext permits OffsetPickContext, DynamicPickContext
{
	private final Camera camera;
	private final ClipContext.Fluid fluidContext;
	private final Entity entity;
	
	protected PickContext(Camera camera, ClipContext.Fluid fluidContext, Entity entity)
	{
		this.camera = camera;
		this.fluidContext = fluidContext;
		this.entity = entity;
	}
	
	public abstract ClipContext.Block blockContext();
	
	public abstract Couple<Vec3> entityTrace(double interactionRange, float partialTick);
	
	public abstract Couple<Vec3> blockTrace(double interactionRange, float partialTick);
	
	public ClipContext toClipContext(double interactionRange, float partialTick)
	{
		Couple<Vec3> blockTrace = this.blockTrace(interactionRange, partialTick);
		return new ClipContext(blockTrace.left(), blockTrace.right(), this.blockContext(), this.fluidContext(), this.entity());
	}
	
	public Camera camera()
	{
		return this.camera;
	}
	
	public ClipContext.Fluid fluidContext()
	{
		return this.fluidContext;
	}
	
	public Entity entity()
	{
		return this.entity;
	}
	
	public static class Builder
	{
		private final Camera camera;
		private ClipContext.Fluid fluidContext;
		private Entity entity;
		private Boolean offsetTrace = null;
		private Vec3 endPos = null;
		
		public Builder(Camera camera)
		{
			this.camera = camera;
		}
		
		public Builder withFluidContext(ClipContext.Fluid fluidContext)
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
		
		public Builder hybridTrace(Vec3 endPos)
		{
			this.endPos = endPos;
			return this;
		}
		
		public PickContext build()
		{
			Entity entity = this.entity == null ? Minecraft.getInstance().getCameraEntity() : this.entity;
			ClipContext.Fluid fluidContext = this.fluidContext == null ? ClipContext.Fluid.NONE : this.fluidContext;
			if (this.endPos != null)
			{
				return new HybridPickContext(this.camera, fluidContext, entity, endPos);
			}

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
