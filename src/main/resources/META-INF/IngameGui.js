function initializeCoreMod() {
	return {
		'coremodone': {
			'target': {
				'type': 'CLASS',
				'name': 'net.minecraft.client.gui.IngameGui'
			},
			'transformer': function(classNode) {
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
				var methods = classNode.methods;
				
				for(x in methods) {
					var method = methods[x];
					
					if(method.name.equals("renderAttackIndicator") || method.name.equals("func_194798_c")) {
						var code = method.instructions;
						var offset = code.get(9);
						
						// net/minecraft/client/gui/GuiIngame#renderAttackIndicator:336
						// if(InjectionDelegation.doRenderCrosshair() == 0)
						
						code.remove(code.get(8));
						code.set(offset, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/teamderpy/shouldersurfing/asm/InjectionDelegation", "doRenderCrosshair", "()I", false));
					}
				}
				
				return classNode;
			}
		}
	}
}