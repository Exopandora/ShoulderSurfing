# ShoulderSurfing </br> [![CurseForge Downloads](https://img.shields.io/curseforge/dt/243190?style=flat-square&logo=curseforge&label=CurseForge&color=%23F16436)](https://www.curseforge.com/minecraft/mc-mods/shoulder-surfing-reloaded) [![Modrinth Downloads](https://img.shields.io/modrinth/dt/kepjj2sy?style=flat-square&logo=modrinth&label=Modrinth&color=%2300AF5C)](https://modrinth.com/mod/shoulder-surfing-reloaded) #
Revamped over-the-shoulder F5 third-person camera view

# Features #

* Over-the-shoulder camera
* Hotkeys to move the camera (Default: `arrow keys`, `page up` and `page down`, `O` to swap shoulders)
* Corrective cross-hair positioning if dynamic crosshair is enabled
* Hide player if the camera gets too close to it
* Client-side only

# Installation #

Download ShoulderSurfing from [curseforge](https://www.curseforge.com/minecraft/mc-mods/shoulder-surfing-reloaded/files/) or [modrinth](https://modrinth.com/mod/shoulder-surfing-reloaded) or build from source

## Forge ##

### Prerequisites ###

1. Make sure [Minecraft Forge](http://files.minecraftforge.net/) is installed

### Manual ###

1. Navigate to `..\.minecraft\mods`
2. Copy the `ShoulderSurfing-Forge-[VERSION].jar`
3. Follow the steps below

### Launcher Settings ###

1. Select Forge as your profile
2. Launch Minecraft
3. Join a world or server
4. Use `arrow keys`, `page up` and `page down` to adjust the perspective (`O` to swap shoulder)

## Fabric ##

### Prerequisites ###

1. Make sure [Fabric](https://fabricmc.net/) is installed
2. Make sure [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) is installed
3. Make sure [Forge Config API Port](https://www.curseforge.com/minecraft/mc-mods/forge-config-api-port-fabric) is installed

### Manual ###

1. Navigate to `..\.minecraft\mods`
2. Copy the `ShoulderSurfing-Fabric-[VERSION].jar`
3. Follow the steps below

### Launcher Settings ###

1. Select Fabric as your profile
2. Launch Minecraft
3. Join a world or server
4. Use `arrow keys`, `page up` and `page down` to adjust the perspective (`O` to swap shoulder)

## Configuration ##

≥ 1.13: Manual: `..\.minecraft\config\shouldersurfing.toml`  
≤ 1.12: Ingame: Mods -> ShoulderSurfing -> Config (Manual: `..\.minecraft\config\shouldersurfing.cfg`)

# API Documentation #

The API Documentation can be found in the [wiki](https://github.com/Exopandora/ShoulderSurfing/wiki).

# Troubleshooting #

**Optifine:**  
1.15: Shaders will be offset because of compatibility issues  
1.15-1.16.1: The crosshair will not render in 3pp because of compatibility issues  
≤ 1.14: Set "Render Quality" to "1.0x" for Shader Pack "(internal)" and switch back to Shader Pack "OFF"  
**BetterCombat:** Set "Enable 1PP Crosshair" to "false" and "Override Mod Crosshairs" to "true"

# FAQ #

**Q:** Is this client-side only?  
**A:** Yes.

**Q:** Can you port feature x to version y?  
**A:** No.

**Q:** Can you port this to quilt, liteloader, rift, etc.?  
**A:** No.

**Q:** Can you make the player always aim at where the crosshair is pointing at?  
**A:** No, as it would break server compatibility.

**Q:** Can i add this to my modpack?  
**A:** Yes.

# License #

MIT License, Original project by Sabar: [Minecraft Forum](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1287308-shoulder-surfing-modded-third-person-camera), [GitHub](https://github.com/sabarjp/ShoulderSurfing)
