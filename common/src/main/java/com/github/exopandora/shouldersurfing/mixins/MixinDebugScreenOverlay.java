package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.DebugScreenOverlayHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debug.DebugEntryPosition;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(DebugScreenOverlay.class)
public class MixinDebugScreenOverlay
{
	@Inject
	(
		method = "render",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/gui/components/DebugScreenOverlay.getLevel()Lnet/minecraft/world/level/Level;",
			shift = Shift.BEFORE
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void render(GuiGraphics guiGraphics, CallbackInfo ci, Collection<ResourceLocation> enabledEntries, ProfilerFiller profilerFiller, ChunkPos chunkPos, List<String> priorityLines, List<String> priorityLines2, Map<ResourceLocation, Collection<String>> map, List<String> lines, DebugScreenDisplayer debugScreenDisplayer)
	{
		if(map.containsKey(DebugEntryPosition.GROUP))
		{
			DebugScreenOverlayHandler.appendDebugText((List<String>) map.get(DebugEntryPosition.GROUP));
		}
	}
}
