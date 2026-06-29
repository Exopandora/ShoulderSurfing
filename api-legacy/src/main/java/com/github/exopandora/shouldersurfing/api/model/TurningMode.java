package com.github.exopandora.shouldersurfing.api.model;

import com.google.common.base.Predicates;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public enum TurningMode {
	ALWAYS(Predicates.alwaysTrue()),
	NEVER(Predicates.alwaysFalse()),
	REQUIRES_TARGET(hitResult -> hitResult != null && !HitResult.Type.MISS.equals(hitResult.getType()));
	
	private final Predicate<@Nullable HitResult> shouldTurnPredicate;
	
	TurningMode(Predicate<@Nullable HitResult> shouldTurnPredicate) {
		this.shouldTurnPredicate = shouldTurnPredicate;
	}
	
	public boolean shouldTurn(@Nullable HitResult hitResult) {
		return this.shouldTurnPredicate.test(hitResult);
	}
	
	public com.github.exopandora.shouldersurfing.api.client.TurningMode toNewApi() {
		return switch (this) {
			case ALWAYS -> com.github.exopandora.shouldersurfing.api.client.TurningMode.ALWAYS;
			case NEVER -> com.github.exopandora.shouldersurfing.api.client.TurningMode.NEVER;
			case REQUIRES_TARGET -> com.github.exopandora.shouldersurfing.api.client.TurningMode.REQUIRES_TARGET;
		};
	}
	
	public static TurningMode fromNewApi(com.github.exopandora.shouldersurfing.api.client.TurningMode perspective) {
		return switch (perspective) {
			case ALWAYS -> ALWAYS;
			case NEVER -> NEVER;
			case REQUIRES_TARGET -> REQUIRES_TARGET;
		};
	}
}
