package com.teamderpy.shouldersurfing.mixins;

import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Accessor
    float getEyeHeightOld();

    @Accessor
    float getEyeHeight();

    @Invoker
    void invokeSetPosition(double x, double y, double z);

    @Invoker
    void invokeMove(double x, double y, double z);

    @Invoker
    double invokeGetMaxZoom(double zoom);

    @Accessor
    Vector3f getLeft();
}
