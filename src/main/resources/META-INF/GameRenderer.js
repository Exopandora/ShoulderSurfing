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
				var methods = classNode.methods;
				
				for(x in methods) {
					var method = methods[x];
					
					if((method.name.equals("updateCameraAndRender") || method.name.equals("func_181560_a")) && method.desc.equals("(FJ)V")) {
						var code = method.instructions;
						var offset = code.get(89);
						
						// net/minecraft/client/renderer/GameRenderer#updateCameraAndRender:668
						// InjectionDelegation.calculateRayTraceProjection();
						
						code.insertBefore(offset, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "calculateRayTraceProjection", "()V", false));
					}
				}
				
				return classNode;
			}
		}
	}
}