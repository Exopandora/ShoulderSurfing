package com.github.exopandora.shouldersurfing.api.accessors;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.vector.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ActiveRenderInfo.class)
public interface ActiveRenderInfoAccessor
{
	@Accessor
	Vector3f getLeft();
}
