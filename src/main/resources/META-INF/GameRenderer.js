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
				var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
				var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
				var methods = classNode.methods;
				
				for(x in methods) {
					var method = methods[x];
					
					if(method.name.equals("renderWorld") || method.name.equals("func_228378_a_")) {
						var code = method.instructions;
						var offset = code.get(310);
						
						// net/minecraft/client/renderer/GameRenderer#renderWorld:611
						// InjectionDelegation.calculateRayTraceProjection();
						
						//if(!(offset instanceof LabelNode)) {
						//	offset = code.get(150);
						//}
						
						code.insertBefore(offset, new VarInsnNode(Opcodes.ALOAD, 4));
						code.insertBefore(offset, new VarInsnNode(Opcodes.ALOAD, 9));
						code.insertBefore(offset, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "calculateRayTraceProjection", "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/Matrix4f;)V", false));
					}
				}
				
				return classNode;
			}
		}
	}
}