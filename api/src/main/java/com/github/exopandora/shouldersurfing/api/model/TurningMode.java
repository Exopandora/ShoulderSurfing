package com.github.exopandora.shouldersurfing.api.model;

import com.google.common.base.Predicates;
import net.minecraft.util.math.RayTraceResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public enum TurningMode
{
	ALWAYS(Predicates.alwaysTrue()),
	NEVER(Predicates.alwaysFalse()),
	REQUIRES_TARGET(hitResult -> hitResult != null && !RayTraceResult.Type.MISS.equals(hitResult.getType()));
	
	private final Predicate<@Nullable RayTraceResult> shouldTurnPredicate;
	
	TurningMode(Predicate<@Nullable RayTraceResult> shouldTurnPredicate)
	{
		this.shouldTurnPredicate = shouldTurnPredicate;
	}
	
	public boolean shouldTurn(@Nullable RayTraceResult hitResult)
	{
		return this.shouldTurnPredicate.test(hitResult);
	}
}