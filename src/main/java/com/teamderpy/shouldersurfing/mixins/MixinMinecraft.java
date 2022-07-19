package com.teamderpy.shouldersurfing.mixins;

import com.teamderpy.shouldersurfing.ShoulderSurfing;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "tick()V", at = @At("RETURN"))
    private void tick(CallbackInfo ci) {
        ShoulderSurfing.INSTANCE.handleKeyInputs(Minecraft.getInstance());
    }
}
