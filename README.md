# Shoulder Surfing Reloaded</br> [![CurseForge Downloads](https://img.shields.io/curseforge/dt/243190?style=flat-square&logo=curseforge&label=CurseForge&color=%23F16436)](https://www.curseforge.com/minecraft/mc-mods/shoulder-surfing-reloaded) [![Modrinth Downloads](https://img.shields.io/modrinth/dt/kepjj2sy?style=flat-square&logo=modrinth&label=Modrinth&color=%2300AF5C)](https://modrinth.com/mod/shoulder-surfing-reloaded) #
Shoulder Surfing Reloaded is a highly configurable third person camera mod for minecraft.

# Features #
* Over-the-shoulder camera - see what you are interacting with in 3rd person
* Decoupled camera - walk left and right without turning your mouse
* Free look (default: `left alt`) - keep walking in a direction while looking in a different one
* Hotkeys to move the camera (default: `arrow keys`, `page up` and `page down`, `o` to switch shoulders)
* Corrective cross-hair positioning when using adaptive or dynamic crosshair
* Adaptive player transparency - fades the player model to transparent when view is obstructed
* Highly configurable - customize your 3rd person experience with many configuration options
* Client side only - no server mods required

# Installation #
Download Shoulder Surfing Reloaded from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/shoulder-surfing-reloaded/files/) or [Modrinth](https://modrinth.com/mod/shoulder-surfing-reloaded) or [build from source](#building-from-source).

## Forge ##
### Prerequisites ###
1. Make sure [Minecraft Forge](http://files.minecraftforge.net/) is installed

### Manual ###
1. Navigate to `.minecraft/mods`
2. Move `ShoulderSurfing-Forge-[VERSION].jar` into the folder
3. Follow the steps below

### Launcher Settings ###
1. Select Forge as your profile
2. Launch Minecraft
3. Join a world or server
4. Use `arrow keys`, `page up` and `page down` to adjust the perspective, `o` to switch shoulders

## Fabric ##
### Prerequisites ###
1. Make sure [Fabric](https://fabricmc.net/) is installed
2. Make sure Fabric API ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api), [Modrinth](https://modrinth.com/mod/fabric-api)) is installed
3. Make sure Forge Config API Port ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/forge-config-api-port-fabric), [Modrinth](https://modrinth.com/mod/forge-config-api-port)) is installed

### Manual ###
1. Navigate to `.minecraft/mods`
2. Move `ShoulderSurfing-Fabric-[VERSION].jar` into the folder
3. Follow the steps below

### Launcher Settings ###
1. Select Fabric as your profile
2. Launch Minecraft
3. Join a world or server
4. Use `arrow keys`, `page up` and `page down` to adjust the perspective, `o` to switch shoulders

## NeoForge ##
### Prerequisites ###
1. Make sure [NeoForge](https://neoforged.net/) is installed
2. Make sure Forge Config API Port ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/forge-config-api-port-fabric), [Modrinth](https://modrinth.com/mod/forge-config-api-port)) is installed

### Manual ###
1. Navigate to `.minecraft/mods`
2. Move `ShoulderSurfing-NeoForge-[VERSION].jar` into the folder
3. Follow the steps below

### Launcher Settings ###
1. Select NeoForge as your profile
2. Launch Minecraft
3. Join a world or server
4. Use `arrow keys`, `page up` and `page down` to adjust the perspective, `o` to switch shoulders

# Configuration #
### Version 3.0.0 and later ###
Manual: `.minecraft/config/shouldersurfing-client.toml`  
Ingame: Install Forge Config Screens ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/config-menus-forge), [Modrinth](https://modrinth.com/mod/forge-config-screens))

### Version 2.9.7 and older ###
≥ 1.13: Manual: `.minecraft/config/shouldersurfing.toml`  
≤ 1.12: Ingame: Mods → ShoulderSurfing → Config (Manual: `.minecraft/config/shouldersurfing.cfg`)

# API Documentation #
The API Documentation can be found in the [wiki](https://github.com/Exopandora/ShoulderSurfing/wiki).

# Building From Source #
Clone the repository and run the following command in the root directory of this repository:
```bash
gradlew build
```
The binaries for each platform can be found in the following directories:

| Platform | Path                    |
|----------|-------------------------|
| Forge    | `./forge/build/libs`    |
| Fabric   | `./fabric/build/libs`   |
| NeoForge | `./neoforge/build/libs` |

# FAQ #
**Q:** Can you port feature x to version y?  
**A:** No.

**Q:** Can you port this to quilt, liteloader, rift, etc.?  
**A:** No.

**Q:** Can I add this to my mod pack?  
**A:** Yes.

# Incompatibility #
Shoulder Surfing Reloaded is known to be incompatible with the following mods (a-z):
- Better Third Person
- CameraOverhaul
- Nimble

# License #
MIT License, Original project by Sabar: [Minecraft Forum](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1287308-shoulder-surfing-modded-third-person-camera), [GitHub](https://github.com/sabarjp/ShoulderSurfing)
