package com.github.exopandora.shouldersurfing.api.callback;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * This callback allows providing the raw input state that Shoulder Surfing uses to determine whether the player is attacking, interacting with an item, or picking (mouse middle click).
 * <p>
 * It does not define what should happen — Shoulder Surfing decides that based on its own logic.
 * <p>
 * For example, a controller mod can report that the attack input is pressed using <code>isAttacking</code>, without caring what Shoulder Surfing does in response.
 * <p>
 * Consider using a different API if you want to control the exact behavior. The exact behavior when any of these is <code>true</code> is handled entirely by Shoulder Surfing.
 * <p>
 * The final result is calculated from all partial results using a logical OR.
 * <p>
 * If no callback provides a definitive result, the default logic is used.
 */
public interface IPlayerStateCallback
{
	/**
	 * Determines whether the player is currently attacking.
	 * <p>
	 * Default value expression:
	 * <pre>{@code
	 * minecraft.options.keyAttack.isDown()
	 * }</pre>
	 * <p>
	 * <p>
	 * An example use case is to support custom keybinds that differ from the vanilla attack key, or for a controller mod that supports only input/button binds.
	 *
	 * @param context The arguments of this callback.
	 *                <ul>
	 *                  <li>{@link Result#TRUE} – forces the attack state to <code>true</code></li>
	 *                  <li>{@link Result#FALSE} – forces the attack state to <code>false</code></li>
	 *                  <li>{@link Result#PASS} – ignores this callback and lets others or the default logic decide</li>
	 *                </ul>
	 */
	Result isAttacking(@NotNull IsAttackingContext context);
	
	record IsAttackingContext(@NotNull Minecraft minecraft)
	{
	}
	
	/**
	 * Determines whether the player is currently interacting with an item.
	 * <p>
	 * Default value expression:
	 * <pre>{@code
	 * minecraft.options.keyUse.isDown() && !cameraEntity.isUsingItem()
	 * }</pre>
	 * <p>
	 *
	 * An example use case is to support custom keybinds that differ from the vanilla use key, or for a controller mod that supports only input/button binds.
	 *
	 * @param context The arguments of this callback.
	 *                <ul>
	 *                  <li>{@link Result#TRUE} – forces the interacting state to <code>true</code></li>
	 *                  <li>{@link Result#FALSE} – forces the interacting state to <code>false</code></li>
	 *                  <li>{@link Result#PASS} – ignores this callback and lets others or the default logic decide</li>
	 *                </ul>
	 */
	Result isInteracting(@NotNull IsInteractingContext context);
	
	record IsInteractingContext(@NotNull Minecraft minecraft, @NotNull LivingEntity cameraEntity)
	{
	}
	
	/**
	 * Determines whether the player is currently picking with an item.
	 * <p>
	 * Default value expression:
	 * <pre>{@code
	 * minecraft.options.keyPickItem.isDown()
	 * }</pre>
	 * <p>
	 *
	 * An example use case is to support custom keybinds that differ from the vanilla pick key (mouse middle button), or for a controller mod that supports only input/button binds.
	 *
	 * @param context The arguments of this callback.
	 *                <ul>
	 *                  <li>{@link Result#TRUE} – forces the picking state to <code>true</code></li>
	 *                  <li>{@link Result#FALSE} – forces the picking state to <code>false</code></li>
	 *                  <li>{@link Result#PASS} – ignores this callback and lets others or the default logic decide</li>
	 *                </ul>
	 */
	Result isPicking(@NotNull IsPickingContext context);
	
	record IsPickingContext(@NotNull Minecraft minecraft)
	{
	}
	
	/**
	 * Determines whether the player is currently using an item.
	 * <p>
	 * Default value expression:
	 * <pre>{@code
	 * cameraEntity.isUsingItem() && !cameraEntity.getUseItem().has(DataComponents.FOOD)
	 * }</pre>
	 * <p>
	 *
	 * @param context The arguments of this callback.
	 *                <ul>
	 *                  <li>{@link Result#TRUE} – forces the using state to <code>true</code></li>
	 *                  <li>{@link Result#FALSE} – forces the using state to <code>false</code></li>
	 *                  <li>{@link Result#PASS} – ignores this callback and lets others or the default logic decide</li>
	 *                </ul>
	 */
	Result isUsingItem(@NotNull IsUsingContext context);
	
	record IsUsingContext(@NotNull Minecraft minecraft, @NotNull LivingEntity cameraEntity)
	{
	}
	
	/**
	 * Represents the possible outcomes of an {@link IPlayerStateCallback}.
	 */
	enum Result
	{
		TRUE,
		FALSE,
		/** Defers to other callbacks or the default logic. */
		PASS
	}
}
