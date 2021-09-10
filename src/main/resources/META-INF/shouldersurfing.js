function initializeCoreMod() {
	var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
	var Label = Java.type('org.objectweb.asm.Label');
	var Opcodes = Java.type('org.objectweb.asm.Opcodes');
	var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
	var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
	var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
	
	return {
		'GameRenderer#pick': {
			'target': {
				'type': 'METHOD',
				'class': 'net.minecraft.client.renderer.GameRenderer',
				'methodName': 'm_109087_', // pick
				'methodDesc': '(F)V'
			},
			'transformer': function(method) {
				var desc = '(' +
					'Lnet/minecraft/world/entity/Entity;' +
					'Lnet/minecraft/world/phys/Vec3;' +
					'Lnet/minecraft/world/phys/Vec3;' +
					'Lnet/minecraft/world/phys/AABB;' +
					'Ljava/util/function/Predicate;' +
					'D' +
				')' + 'Lnet/minecraft/world/phys/EntityHitResult;';
				var getEntityHitResult = ASMAPI.mapMethod('m_37287_'); // getEntityHitResult
				var oldMethod = ASMAPI.findFirstMethodCall(method, ASMAPI.MethodType.STATIC, 'net/minecraft/world/entity/projectile/ProjectileUtil', getEntityHitResult, desc);
				var newMethod = ASMAPI.buildMethodCall('com/teamderpy/shouldersurfing/asm/InjectionDelegation', 'getEntityHitResult', desc, ASMAPI.MethodType.STATIC);
				
				method.instructions.set(oldMethod, newMethod);
				ASMAPI.log('INFO', 'Injected GameRenderer#pick');
				return method;
			}
		},
		'Options#setCameraType': {
			'target': {
				'type': 'METHOD',
				'class': 'net.minecraft.client.Options',
				'methodName': 'm_92157_', // setCameraType
				'methodDesc': '(Lnet/minecraft/client/CameraType;)V'
			},
			'transformer': function(method) {
				var desc = '(' +
					'Lnet/minecraft/client/CameraType;' +
				')' + 'V';
				var newMethod = ASMAPI.buildMethodCall('com/teamderpy/shouldersurfing/asm/InjectionDelegation', 'setCameraType', desc, ASMAPI.MethodType.STATIC);
				
				ASMAPI.appendMethodCall(method, newMethod);
				method.instructions.insertBefore(method.instructions.getFirst(), new VarInsnNode(Opcodes.ALOAD, 1));
				ASMAPI.log('INFO', 'Injected Options#setCameraType');
				return method;
			}
		},
		'Gui#renderCrosshair': {
			'target': {
				'type': 'METHOD',
				'class': 'net.minecraft.client.gui.Gui',
				'methodName': 'm_93080_', // renderCrosshair
				'methodDesc': '(Lcom/mojang/blaze3d/vertex/PoseStack;)V'
			},
			'transformer': function(method) {
				var desc = '(' +
					'Lnet/minecraft/client/CameraType;' +
				')' + 'Z';
				var isFirstPerson = ASMAPI.mapMethod('m_90612_'); // isFirstPerson
				var oldMethod = ASMAPI.findFirstMethodCall(method, ASMAPI.MethodType.VIRTUAL, 'net/minecraft/client/CameraType', isFirstPerson, '()Z');
				var newMethod = ASMAPI.buildMethodCall('com/teamderpy/shouldersurfing/asm/InjectionDelegation', 'doRenderCrosshair', desc, ASMAPI.MethodType.STATIC);
				
				method.instructions.set(oldMethod, newMethod);
				ASMAPI.log('INFO', 'Injected Gui#renderCrosshair');
				return method;
			}
		},
		'Player#pick': {
			'target': {
				'type': 'CLASS',
				'name': 'net.minecraft.world.entity.player.Player'
			},
			'transformer': function(classNode) {
				var methods = classNode.methods;
				
				//	public HitResult pick(double distance, float partialTicks, boolean stopOnFluid)
				//	{
				//		return InjectionDelegation.pick(this, super.pick(distance, partialTicks, stopOnFluid), distance, partialTicks, stopOnFluid);
				//	}
				
				//	public pick(DFZ)Lnet/minecraft/world/phys/HitResult;
				//		L0
				//			ALOAD 0
				//			ALOAD 0
				//			DLOAD 1
				//			FLOAD 3
				//			ILOAD 4
				//			INVOKESPECIAL Lnet/minecraft/world/entity/Entity.pick(DFZ)Lnet/minecraft/world/phys/HitResult;
				//			DLOAD 1
				//			FLOAD 3
				//			ILOAD 4
				//			INVOKESTATIC com/teamderpy/shouldersurfing/asm/InjectionDelegation.pick(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/HitResult;DFZ)Lnet/minecraft/world/phys/HitResult;
				//			ARETURN
				
				var pick = ASMAPI.mapMethod('m_19907_'); // pick
				var method = ASMAPI.getMethodNode();
				var desc = '(' +
					'Lnet/minecraft/world/entity/Entity;' +
					'Lnet/minecraft/world/phys/HitResult;' +
					'D' +
					'F' +
					'Z' +
				')' + 'Lnet/minecraft/world/phys/HitResult;';
				
				method.access = Opcodes.ACC_PUBLIC;
				method.name = pick;
				method.desc = '(DFZ)Lnet/minecraft/world/phys/HitResult;';
				method.instructions.add(new LabelNode(new Label()));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.DLOAD, 1));
				method.instructions.add(new VarInsnNode(Opcodes.FLOAD, 3));
				method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
				method.instructions.add(ASMAPI.buildMethodCall('net/minecraft/world/entity/Entity', pick, '(DFZ)Lnet/minecraft/world/phys/HitResult;', ASMAPI.MethodType.SPECIAL));
				method.instructions.add(new VarInsnNode(Opcodes.DLOAD, 1));
				method.instructions.add(new VarInsnNode(Opcodes.FLOAD, 3));
				method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
				method.instructions.add(ASMAPI.buildMethodCall('com/teamderpy/shouldersurfing/asm/InjectionDelegation', 'pick', desc, ASMAPI.MethodType.STATIC));
				method.instructions.add(new InsnNode(Opcodes.ARETURN));
				
				methods.add(method);
				ASMAPI.log('INFO', 'Injected Player#pick');
				return classNode;
			}
		},
		'ShadersRender#updateActiveRenderInfo': {
			'target': {
				'type': 'METHOD',
				'class': 'net.optifine.shaders.ShadersRender',
				'methodName': 'updateActiveRenderInfo',
				'methodDesc': '(Lnet/minecraft/client/CameraType;Lnet/minecraft/client/Minecraft;F)V'
			},
			'transformer': function(method) {
				var desc = '(' +
					'Lnet/minecraft/client/CameraType;' +
					'Lnet/minecraft/client/Minecraft;' +
					'F' +
				')' + 'V';
				var newMethod = ASMAPI.buildMethodCall('com/teamderpy/shouldersurfing/asm/InjectionDelegation', 'updateActiveRenderInfo', desc, ASMAPI.MethodType.STATIC);
				
				method.instructions.clear();
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
				method.instructions.add(newMethod);
				method.instructions.add(new InsnNode(Opcodes.ARETURN));
				
				ASMAPI.log('INFO', 'Injected ShadersRender#updateActiveRenderInfo');
				return method;
			}
		}
	}
}
