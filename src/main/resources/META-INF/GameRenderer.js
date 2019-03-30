function initializeCoreMod() {
	return {
		'coremodone': {
			'target': {
				'type': 'CLASS',
				'name': 'net.minecraft.client.renderer.GameRenderer'
			},
			'transformer': function(classNode) {
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
				var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
				var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
				var methods = classNode.methods;
				
				for(x in methods) {
					var method = methods[x];
					
					if(method.name.equals("orientCamera") || method.name.equals("func_78467_g")) {
						var code = method.instructions;
						var offset1 = code.get(219);
						
						// net/minecraft/client/renderer/GameRenderer#orientCamera:480
						// f1 += InjectionDelegation.getShoulderRotationYaw();
						
						code.insertBefore(offset1, new VarInsnNode(Opcodes.FLOAD, 12));
						code.insertBefore(offset1, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getShoulderRotationYaw", "()F", false));
						code.insertBefore(offset1, new InsnNode(Opcodes.FADD));
						code.insertBefore(offset1, new VarInsnNode(Opcodes.FSTORE, 12));
						
						// net/minecraft/client/renderer/GameRenderer#orientCamera:480
						// f2 += InjectionDelegation.getShoulderRotationPitch();
						
						code.insertBefore(offset1, new VarInsnNode(Opcodes.FLOAD, 13));
						code.insertBefore(offset1, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getShoulderRotationPitch", "()F", false));
						code.insertBefore(offset1, new InsnNode(Opcodes.FADD));
						code.insertBefore(offset1, new VarInsnNode(Opcodes.FSTORE, 13));
						
						// net/minecraft/client/renderer/GameRenderer#orientCamera:480
						// d3 *= InjectionDelegation.getShoulderZoomMod();
						
						code.insertBefore(offset1, new VarInsnNode(Opcodes.DLOAD, 10));
						code.insertBefore(offset1, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getShoulderZoomMod", "()F", false));
						code.insertBefore(offset1, new InsnNode(Opcodes.F2D));
						code.insertBefore(offset1, new InsnNode(Opcodes.DMUL));
						code.insertBefore(offset1, new VarInsnNode(Opcodes.DSTORE, 10));
						
						var offset2 = code.get(382 + 13);
						
						// net/minecraft/client/renderer/GameRenderer#orientCamera:496
						// RayTraceResult raytraceresult = InjectionDelegation.getRayTraceResult(...);
						
						code.set(offset2, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "rayTraceBlocks", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;", false));
						
						var offset3 = code.get(402 + 13);
						
						// net/minecraft/client/renderer/GameRenderer#orientCamera:498
						// InjectionDelegation.verifyReverseBlockDist(d7);
						
						code.insertBefore(offset3, new VarInsnNode(Opcodes.DLOAD, 25));
						code.insertBefore(offset3, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "verifyReverseBlockDist", "(D)V", false));
						
						var offset4 = code.get(418 + 15);
						
						// net/minecraft/client/renderer/GameRenderer#orientCamera:504
						// d3 = InjectionDelegation.checkDistance(d3, f1, d0, d1, d2, d4, d6, d5);
						
						code.insertBefore(offset4, new VarInsnNode(Opcodes.DLOAD, 10));
						code.insertBefore(offset4, new VarInsnNode(Opcodes.FLOAD, 12));
						code.insertBefore(offset4, new VarInsnNode(Opcodes.DLOAD, 4));
						code.insertBefore(offset4, new VarInsnNode(Opcodes.DLOAD, 6));
						code.insertBefore(offset4, new VarInsnNode(Opcodes.DLOAD, 8));
						code.insertBefore(offset4, new VarInsnNode(Opcodes.DLOAD, 14));
						code.insertBefore(offset4, new VarInsnNode(Opcodes.DLOAD, 18));
						code.insertBefore(offset4, new VarInsnNode(Opcodes.DLOAD, 16));
						code.insertBefore(offset4, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "checkDistance", "(DFDDDDDD)D", false));
						code.insertBefore(offset4, new VarInsnNode(Opcodes.DSTORE, 10));
					} else if((method.name.equals("updateCameraAndRender") || method.name.equals("func_181560_a")) && method.desc.equals("(FJ)V")) {
						var code = method.instructions;
						var offset = code.get(94);
						
						// net/minecraft/client/renderer/GameRenderer#updateCameraAndRender:788
						// InjectionDelegation.calculateRayTraceProjection();
						
						code.insertBefore(offset, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "calculateRayTraceProjection", "()V", false));
					}
				}
				
				return classNode;
			}
		}
	}
}