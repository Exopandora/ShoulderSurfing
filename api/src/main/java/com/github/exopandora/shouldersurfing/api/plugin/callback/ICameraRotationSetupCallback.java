package com.github.exopandora.shouldersurfing.api.plugin.callback;

import com.github.exopandora.shouldersurfing.api.math.Vec2f;
import net.minecraft.client.player.LocalPlayer;

/**
 * This callback can be used to change the camera angle computation.
 *
 * @since 4.17.0
 */
public interface ICameraRotationSetupCallback {
	/**
	 * @param context The arguments of this callback.
	 * @param result  The result of this callback.
	 * @since 4.17.0
	 */
	default void pre(CameraRotationSetupContext context, CameraRotationSetupResult result) {
	}
	
	/**
	 * @param context The arguments of this callback.
	 * @param result  The result of this callback.
	 * @since 4.17.0
	 */
	default void post(CameraRotationSetupContext context, CameraRotationSetupResult result) {
	}
	
	record CameraRotationSetupContext(LocalPlayer player, double deltaXRot, double deltaYRot) {
	}
	
	class CameraRotationSetupResult {
		private float xRot;
		private float yRot;
		
		public CameraRotationSetupResult(float xRot, float yRot) {
			this.xRot = xRot;
			this.yRot = yRot;
		}
		
		public float getXRot() {
			return this.xRot;
		}
		
		public void setXRot(float xRot) {
			this.xRot = xRot;
		}
		
		public float getYRot() {
			return this.yRot;
		}
		
		public void setYRot(float yRot) {
			this.yRot = yRot;
		}
		
		public void setRotation(Vec2f rotation) {
			this.xRot = rotation.x();
			this.yRot = rotation.y();
		}
		
		public Vec2f getRotation() {
			return new Vec2f(this.xRot, this.yRot);
		}
	}
}
