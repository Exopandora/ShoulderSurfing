package com.github.exopandora.shouldersurfing.api.client.world.phys;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.util.Couple;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public final class ObstructionPickContext extends PickContext {
	private final Vec3 endPos;
	
	public ObstructionPickContext(
		Camera camera,
		ClipContext.Fluid fluidContext,
		Entity entity,
		Predicate<Entity> entityFilter,
		Vec3 endPos
	) {
		super(camera, fluidContext, entity, entityFilter);
		this.endPos = endPos;
	}
	
	@Override
	public ClipContext.Block blockContext() {
		return IShoulderSurfing.getInstance().isAiming() ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE;
	}
	
	@Override
	public Couple<Vec3> entityTrace(double interactionRange, float partialTick) {
		return this.calcRay(partialTick);
	}
	
	@Override
	public Couple<Vec3> blockTrace(double interactionRange, float partialTick) {
		return this.calcRay(partialTick);
	}
	
	private Couple<Vec3> calcRay(float partialTick) {
		Vec3 startPos = this.entity().getEyePosition(partialTick);
		return new Couple<Vec3>(startPos, this.endPos);
	}
}
