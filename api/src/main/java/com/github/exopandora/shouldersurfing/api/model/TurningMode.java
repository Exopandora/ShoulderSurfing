package com.github.exopandora.shouldersurfing.api.model;

import com.google.common.base.Predicates;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public enum TurningMode
{
	ALWAYS(Predicates.alwaysTrue()),
	NEVER(Predicates.alwaysFalse()),
	REQUIRES_TARGET(hitResult -> hitResult != null && !HitResult.Type.MISS.equals(hitResult.getType()));
	
	private final Predicate<@Nullable HitResult> shouldTurnPredicate;
	
	TurningMode(Predicate<@Nullable HitResult> shouldTurnPredicate)
	{
		this.shouldTurnPredicate = shouldTurnPredicate;
	}
	
	public boolean shouldTurn(@Nullable HitResult hitResult)
	{
		return this.shouldTurnPredicate.test(hitResult);
	}
}