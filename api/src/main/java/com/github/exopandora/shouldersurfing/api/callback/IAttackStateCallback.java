package com.github.exopandora.shouldersurfing.api.callback;

import com.github.exopandora.shouldersurfing.api.model.TurningMode;
import net.minecraft.client.Minecraft;

/**
 * This callback allows implementing custom logic to determine whether the player is attacking.
 * Useful for supporting custom attack keybinds that differ from the vanilla attack key.
 * The final result is calculated from all partial results using a logical OR. If no callback provides a definitive result, the default attack logic is used.
 */
public interface IAttackStateCallback
{
	/**
	 * Determines whether the player is currently attacking.
	 *
	 * @param context The arguments of this callback, containing the {@link Minecraft} instance and the {@link TurningMode} used when attacking.
	 * @return The result of this callback:
	 * <ul>
	 *   <li>{@link Result#TRUE} – forces the attack state to <code>true</code></li>
	 *   <li>{@link Result#FALSE} – forces the attack state to <code>false</code></li>
	 *   <li>{@link Result#PASS} – ignores this callback and lets others or the default logic decide</li>
	 * </ul>
	 */
	Result isAttacking(Context context);
	
	/**
	 * The arguments passed to an {@link IAttackStateCallback}.
	 *
	 * @param minecraft          The Minecraft instance.
	 * @param turningMode        The {@link TurningMode} used when attacking.
	 * @param defaultIsAttacking The result of the default attack check,
	 *                           equivalent to <code>minecraft.options.keyAttack.isDown()
	 *                           && turningMode.shouldTurn(minecraft.hitResult)</code>.
	 *                           This value is provided for reference when deciding whether to override
	 *                           or pass in the callback.
	 */
	record Context(Minecraft minecraft, TurningMode turningMode, boolean defaultIsAttacking)
	{
	}
	
	/**
	 * Represents the possible outcomes of an {@link IAttackStateCallback}.
	 */
	enum Result
	{
		/** Forces the attack state to <code>true</code>. */
		TRUE,
		/** Forces the attack state to <code>false</code>. */
		FALSE,
		/** Defers to other callbacks or the default logic. */
		PASS
	}
}
