function initializeCoreMod() {
	return {
		'coremodone': {
			'target': {
				'type': 'CLASS',
				'name': 'net.minecraft.entity.player.PlayerEntity'
			},
			'transformer': function(classNode) {
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
				var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode');
				var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
				var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
				var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
				var Label = Java.type('org.objectweb.asm.Label');
				var methods = classNode.methods;
				var name = null;
				
				for(x in methods) {
					var method = methods[x];
					
					if(method.name.equals("isPlayer")) {
						name = "getEyePosition";
						break;
					} else if(method.name.equals("func_70684_aJ")) {
						name = "func_174824_e";
						break;
					}
				}
				
				//	public Vec3d getEyePosition(float paritalTicks)
				//	{
				//		return InjectionDelegation.getEyePosition(this, super.getEyePosition(paritalTicks));
				//	}
				
				//	public getEyePosition(F)Lnet/minecraft/util/math/Vec3d;
				//  	L0
				//			LINENUMBER 187 L0
				//			ALOAD 0
				//			ALOAD 0
				//			FLOAD 1
				//			INVOKESPECIAL com/teamderpy/shouldersurfing/asm/InjectionDelegation$A.getEyePosition(F)Lnet/minecraft/util/math/Vec3d;
				//			INVOKESTATIC com/teamderpy/shouldersurfing/asm/InjectionDelegation.getEyePosition(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;
				//			ARETURN
				
				var method = new MethodNode(Opcodes.ACC_PUBLIC, name, "(F)Lnet/minecraft/util/math/Vec3d;", null, null);
				
				method.instructions.add(new LabelNode(new Label()));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new VarInsnNode(Opcodes.FLOAD, 1));
				method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/entity/Entity", name, "(F)Lnet/minecraft/util/math/Vec3d;", false));
				method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getEyePosition", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", false));
				method.instructions.add(new InsnNode(Opcodes.ARETURN));
				
				methods.add(method);
				
				return classNode;
			}
		}
	}
}