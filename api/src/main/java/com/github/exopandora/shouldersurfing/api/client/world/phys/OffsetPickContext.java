package com.github.exopandora.shouldersurfing.api.client.world.phys;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.util.Couple;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public final class OffsetPickContext extends PickContext {
	private final PickOrigin blockPickOrigin;
	private final PickOrigin entityPickOrigin;
	
	public OffsetPickContext(
		Camera camera,
		ClipContext.Fluid fluidContext,
		Entity entity,
		Predicate<Entity> entityFilter,
		PickOrigin blockPickOrigin,
		PickOrigin entityPickOrigin
	) {
		super(camera, fluidContext, entity, entityFilter);
		this.blockPickOrigin = blockPickOrigin;
		this.entityPickOrigin = entityPickOrigin;
	}
	
	@Override
	public ClipContext.Block blockContext() {
		var instance = IShoulderSurfing.getInstance();
		if (instance.isAiming() || instance.getCrosshairRenderer().isCrosshairDynamic()) {
			return ClipContext.Block.COLLIDER;
		}
		return ClipContext.Block.OUTLINE;
	}
	
	@Override
	public Couple<Vec3> entityTrace(double interactionRange, float partialTick) {
		return calcRay(this.camera(), this.entity(), interactionRange, partialTick, this.entityPickOrigin);
	}
	
	@Override
	public Couple<Vec3> blockTrace(double interactionRange, float partialTick) {
		return calcRay(this.camera(), this.entity(), interactionRange, partialTick, this.blockPickOrigin);
	}
	
	private static Couple<Vec3> calcRay(
		Camera camera,
		Entity entity,
		double interactionRange,
		float partialTick,
		PickOrigin pickOrigin
	) {
		var eyePosition = entity.getEyePosition(partialTick);
		var cameraPos = camera.getPosition();
		var cameraOffset = cameraPos.subtract(eyePosition);
		var renderOffset = IShoulderSurfing.getInstance().getCamera().getRenderOffset();
		var rayTraceStartOffset = new Vec3(camera.getLeftVector())
			.scale(renderOffset.x())
			.add(new Vec3(camera.getUpVector()).scale(renderOffset.y()));
		var viewVector = new Vec3(camera.getLookVector());
		var interactionRangeSq = Mth.square(interactionRange);
		if (rayTraceStartOffset.lengthSqr() < interactionRangeSq) {
			interactionRange = Math.sqrt(interactionRangeSq - rayTraceStartOffset.lengthSqr());
		}
		var distance = interactionRange + cameraOffset.distanceTo(rayTraceStartOffset);
		var startPos = pickOrigin.calc(cameraPos, eyePosition, rayTraceStartOffset);
		var endPos = cameraPos.add(viewVector.scale(distance));
		return new Couple<Vec3>(startPos, endPos);
	}
}
