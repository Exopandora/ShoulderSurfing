package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.api.client.IClientConfig;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

import static org.spongepowered.asm.mixin.injection.At.Shift;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements GameRendererAccessor
{
	@Redirect
	(
		method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/world/entity/projectile/ProjectileUtil.getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"
		)
	)
	private EntityHitResult getEntityHitResult(Entity shooter, Vec3 startPos, Vec3 endPos, AABB boundingBox, Predicate<Entity> filter, double interactionRangeSq)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		
		if(instance.isShoulderSurfing())
		{
			PickContext pickContext = new PickContext.Builder(this.getMainCamera())
				.withEntity(shooter)
				.build();
			double interactionRange = Math.sqrt(interactionRangeSq);
			float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
			return instance.getObjectPicker().pickEntities(pickContext, interactionRange, partialTick);
		}
		
		return ProjectileUtil.getEntityHitResult(shooter, startPos, endPos, boundingBox, filter, interactionRangeSq);
	}
	
	@ModifyVariable
	(
		method = "getFov",
		at = @At
		(
			value = "FIELD",
			target = "net/minecraft/client/renderer/GameRenderer.oldFovModifier:F",
			shift = Shift.BY,
			by = -3
		),
		ordinal = 1
	)
	private float getFov(float fov)
	{
		ShoulderSurfingImpl instance = ShoulderSurfingImpl.getInstance();
		IClientConfig config = instance.getClientConfig();
		
		if(instance.isShoulderSurfing() && config.isFovOverrideEnabled())
		{
			return config.getFovOverride();
		}
		
		return fov;
	}
	
	@Inject
	(
		method = "render",
		at = @At("HEAD")
	)
	public void render(CallbackInfo ci)
	{
		ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().setCameraEntityRenderState(null);
	}
	
	@Redirect
	(
		method = "renderLevel",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/CameraType.isFirstPerson()Z"
		),
		slice = @Slice
		(
			from = @At
			(
				value = "FIELD",
				target = "net/minecraft/client/gui/components/debug/DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR:Lnet/minecraft/resources/ResourceLocation;",
				opcode = Opcodes.GETSTATIC
			)
		)
	)
	private boolean doRenderCrosshair(CameraType cameraType)
	{
		return ShoulderSurfingImpl.getInstance().getCrosshairRenderer().doRenderCrosshair();
	}
	
	@Redirect
	(
		method = "renderLevel",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/client/OptionInstance.get()Ljava/lang/Object;"
		),
		slice = @Slice
		(
			from = @At
			(
				value = "INVOKE",
				target = "net/minecraft/client/Options.bobView()Lnet/minecraft/client/OptionInstance;"
			),
			to = @At
			(
				value = "INVOKE",
				target = "net/minecraft/client/renderer/GameRenderer.bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"
			)
		)
	)
	public Object bobView(OptionInstance<Boolean> instance)
	{
		if(ShoulderSurfingImpl.getInstance().isShoulderSurfing())
		{
			return switch(Config.CLIENT.getViewBobbingMode())
			{
				case INHERIT -> instance.get();
				case ON -> true;
				case OFF -> false;
			};
		}
		
		return instance.get();
	}
}
