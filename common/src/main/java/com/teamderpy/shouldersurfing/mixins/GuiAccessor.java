package com.teamderpy.shouldersurfing.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public interface GuiAccessor
{
	@Invoker
	void invokeRenderCrosshair(PoseStack poseStack);
}
