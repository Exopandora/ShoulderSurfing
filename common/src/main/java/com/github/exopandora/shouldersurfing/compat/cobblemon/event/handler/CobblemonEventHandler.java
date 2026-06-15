package com.github.exopandora.shouldersurfing.compat.cobblemon.event.handler;

import com.cobblemon.mod.common.OrientationControllable;
import com.cobblemon.mod.common.api.orientation.OrientationController;
import com.cobblemon.mod.common.api.riding.behaviour.ActiveRidingContext;
import com.cobblemon.mod.common.api.riding.behaviour.types.liquid.BoatBehaviour;
import com.cobblemon.mod.common.api.riding.behaviour.types.liquid.DolphinBehaviour;
import com.cobblemon.mod.common.api.riding.behaviour.types.liquid.SubmarineBehaviour;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.item.PokeBallItem;
import com.cobblemon.mod.common.item.interactive.PokerodItem;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerAimStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ComputePlayerRideBoatStateEvent;
import com.github.exopandora.shouldersurfing.api.client.event.ForceVanillaPlayerInputEvent;
import com.github.exopandora.shouldersurfing.api.client.event.SetupCameraRotationEvent;
import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import com.github.exopandora.shouldersurfing.compat.Mods;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum CobblemonEventHandler {
	INSTANCE;
	
	public void computePlayerAimState(ComputePlayerAimStateEvent event) {
		if (isAdaptiveItemStack(event.getEntity().getMainHandItem()) || isAdaptiveItemStack(event.getEntity().getOffhandItem())) {
			event.setResult(true);
		}
	}
	
	private static boolean isAdaptiveItemStack(ItemStack stack) {
		return stack.getItem() instanceof PokeBallItem || stack.getItem() instanceof PokerodItem;
	}
	
	public void computePlayerRideBoatState(ComputePlayerRideBoatStateEvent event) {
		if (hasActiveBoatBehaviour(event.getVehicle())) {
			event.setResult(true);
		} else if (hasActiveSubmarineBehaviour(event.getCameraEntity().getVehicle())) {
			event.setResult(true);
		} else if (hasActiveDolphinBehaviour(event.getCameraEntity().getVehicle())) {
			event.setResult(true);
		}
	}
	
	public void forceVanillaPlayerInput(ForceVanillaPlayerInputEvent event) {
		if (event.getCameraEntity().isPassenger()) {
			if (hasActiveBoatBehaviour(event.getCameraEntity().getVehicle())) {
				event.cancel();
			} else if (hasActiveSubmarineBehaviour(event.getCameraEntity().getVehicle())) {
				event.cancel();
			} else if (hasActiveDolphinBehaviour(event.getCameraEntity().getVehicle())) {
				event.cancel();
			}
		}
	}
	
	public void preSetupCameraRotation(SetupCameraRotationEvent event) {
		if (event.getPlayer().getVehicle() instanceof OrientationControllable controllableVehicle) {
			OrientationController vehicleController = controllableVehicle.getOrientationController();
			if (vehicleController.isActive()) {
				event.setResult(new Vec2f(vehicleController.getPitch(), vehicleController.getYaw()));
			}
		}
	}
	
	public void postSetupCameraRotation(SetupCameraRotationEvent event) {
		if (hasActiveBoatBehaviour(event.getPlayer().getVehicle())) {
			Entity vehicle = event.getPlayer().getVehicle();
			float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
			float yRotLerped = Mth.rotLerp(partialTick, vehicle.yRotO, vehicle.getYRot());
			float delta = Mth.wrapDegrees(event.getResult().y()- yRotLerped);
			float clamped = Mth.clamp(delta, -105.0F, 105.0F);
			event.setResult(event.getResult().add(0, clamped - delta));
		}
	}
	
	private static boolean hasActiveBoatBehaviour(@Nullable Entity vehicle) {
		return hasActiveBehaviour(vehicle, BoatBehaviour.Companion.getKEY());
	}
	
	private static boolean hasActiveSubmarineBehaviour(@Nullable Entity vehicle) {
		return hasActiveBehaviour(vehicle, SubmarineBehaviour.Companion.getKEY());
	}
	
	private static boolean hasActiveDolphinBehaviour(@Nullable Entity vehicle) {
		return hasActiveBehaviour(vehicle, DolphinBehaviour.Companion.getKEY());
	}
	
	private static boolean hasActiveBehaviour(@Nullable Entity entity, ResourceLocation behavior) {
		if (entity instanceof PokemonEntity pokemon && pokemon.getRidingController() != null) {
			ActiveRidingContext ridingContext = pokemon.getRidingController().getContext();
			return ridingContext != null && ridingContext.getSettings().getKey().equals(behavior);
		}
		return false;
	}
	
	public static boolean isRidingSupported() {
		return Mods.COBBLEMON.isSameOrLaterVersion("1.7.0");
	}
}
