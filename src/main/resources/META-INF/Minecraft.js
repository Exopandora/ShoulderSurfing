function initializeCoreMod() {
	return {
		'coremodone': {
			'target': {
				'type': 'CLASS',
				'name': 'net.minecraft.client.Minecraft'
			},
			'transformer': function(classNode) {
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
				var methods = classNode.methods;
				
				for(x in methods) {
					var method = methods[x];
					
					if(method.name.equals("processKeyBinds") || method.name.equals("func_184117_aA")) {
						var code = method.instructions;
						var offset = code.get(22);
						
						code.set(offset, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "getMax3ppId", "()I", false));
					}
				}
				
				return classNode;
			}
		}
	}
}