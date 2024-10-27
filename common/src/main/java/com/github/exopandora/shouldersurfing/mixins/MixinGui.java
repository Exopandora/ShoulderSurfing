package com.github.exopandora.shouldersurfing.mixins;

import java.util.function.Function;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.client.CrosshairRenderer;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.mixinducks.GuiDuck;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID;

@Mixin(Gui.class)
public abstract class MixinGui implements GuiDuck
{
	@Unique
	private static final ResourceLocation OBSTRUCTION_INDICATOR_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "hud/obstruction_indicator");
	
	@Unique
	private static final ResourceLocation OBSTRUCTED_CROSSHAIR_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "hud/obstructed_crosshair");
	
	@Unique
	private static final ResourceLocation OBSTRUCTED_CROSSHAIR_OPAQUE_SPRITE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "hud/obstructed_crosshair_opaque");
	
	@Shadow
	private @Final Minecraft minecraft;
	
	@Shadow
	protected abstract void renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
	
	@Shadow
	protected abstract boolean canRenderCrosshairForSpectator(HitResult hitResult);
	
	@Redirect
	(
		method = "renderCrosshair",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		)
	)
	private boolean doRenderCrosshair(CameraType cameraType)
	{
		return ShoulderSurfingImpl.getInstance().getCrosshairRenderer().doRenderCrosshair();
	}
	
	@Redirect
	(
		method = "renderCameraOverlays",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		),
		require = 0
	)
	private boolean isFirstPerson(CameraType cameraType)
	{
		return cameraType.isFirstPerson() || Perspective.SHOULDER_SURFING == Perspective.current() && this.minecraft.player.isScoping();
	}
	
	/**
	 * Used in loader-specific mixins
	 */
	public void shouldersurfing$renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker)
	{
		CrosshairRenderer crosshairRenderer = ShoulderSurfingImpl.getInstance().getCrosshairRenderer();
		
		if(!crosshairRenderer.doRenderCrosshair())
		{
			return;
		}
		
		boolean drawSecondary = crosshairRenderer.doRenderObstructionIndicator();
		boolean swapped = drawSecondary && ShoulderSurfing.getInstance().isAiming();
		
		// Draw primary crosshair
		crosshairRenderer.preRenderCrosshair(guiGraphics.pose(), this.minecraft.getWindow());
		
		if(swapped)
		{
			this.renderCustomCrosshair(guiGraphics, OBSTRUCTED_CROSSHAIR_SPRITE, RenderType::crosshair);
			this.renderCustomCrosshair(guiGraphics, OBSTRUCTED_CROSSHAIR_OPAQUE_SPRITE, RenderType::guiTexturedOverlay);
		}
		else
		{
			this.renderCrosshair(guiGraphics, deltaTracker);
		}
		
		crosshairRenderer.postRenderCrosshair(guiGraphics.pose());
		
		// Draw obstruction crosshair
		if(drawSecondary)
		{
			crosshairRenderer.preRenderCrosshair(guiGraphics.pose(), this.minecraft.getWindow(), true);
			
			if(swapped)
			{
				this.renderCrosshair(guiGraphics, deltaTracker);
			}
			else
			{
				this.renderCustomCrosshair(guiGraphics, OBSTRUCTION_INDICATOR_SPRITE, RenderType::crosshair);
			}
			
			crosshairRenderer.postRenderCrosshair(guiGraphics.pose(), true);
		}
	}
	
	/**
	 * A dumbed down version of the vanilla method. Made to avoid interference
	 * with other mods, and exclude other functionalities of the vanilla crosshair.
	 */
	@Unique
	private void renderCustomCrosshair(GuiGraphics guiGraphics, ResourceLocation sprite, Function<ResourceLocation, RenderType> renderType)
	{
		if(this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult))
		{
			guiGraphics.blitSprite(renderType, sprite, (guiGraphics.guiWidth() - 15) / 2, (guiGraphics.guiHeight() - 15) / 2, 15, 15);
		}
	}
}
