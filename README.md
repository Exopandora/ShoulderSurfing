# Shoulder Surfing Reloaded #
[![CurseForge Downloads](https://img.shields.io/curseforge/dt/243190?style=flat-square&logo=curseforge&label=CurseForge&color=%23F16436)](https://www.curseforge.com/minecraft/mc-mods/shoulder-surfing-reloaded) [![Modrinth Downloads](https://img.shields.io/modrinth/dt/kepjj2sy?style=flat-square&logo=modrinth&label=Modrinth&color=%2300AF5C)](https://modrinth.com/mod/shoulder-surfing-reloaded) ![GitHub License](https://img.shields.io/github/license/Exopandora/ShoulderSurfing?style=flat-square&label=License)

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

<details>
  <summary>Forge</summary>

### Prerequisites ###
1. Make sure [Minecraft Forge](http://files.minecraftforge.net/) is installed
2. Make sure Forge Config API Port ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/forge-config-api-port-fabric), [Modrinth](https://modrinth.com/mod/forge-config-api-port)) is installed

### Manual ###
1. Navigate to `.minecraft/mods`
2. Move `ShoulderSurfing-Forge-[VERSION].jar` into the folder
3. Follow the steps below

### Launcher Settings ###
1. Select Forge as your profile
2. Launch Minecraft
3. Join a world or server
4. Use `arrow keys`, `page up` and `page down` to adjust the perspective, `o` to switch shoulders

</details>

<details>
  <summary>Fabric</summary>

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

</details>

<details>
  <summary>NeoForge</summary>

### Prerequisites ###
1. Make sure [NeoForge](https://neoforged.net/) is installed

### Manual ###
1. Navigate to `.minecraft/mods`
2. Move `ShoulderSurfing-NeoForge-[VERSION].jar` into the folder
3. Follow the steps below

### Launcher Settings ###
1. Select NeoForge as your profile
2. Launch Minecraft
3. Join a world or server
4. Use `arrow keys`, `page up` and `page down` to adjust the perspective, `o` to switch shoulders

</details>

# Configuration #
≥ 1.21: Ingame: Mods → ShoulderSurfing → Config (Manual: `.minecraft/config/shouldersurfing-client.cfg`)  
≤ 1.20: Install Forge Config Screens ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/config-menus-forge), [Modrinth](https://modrinth.com/mod/forge-config-screens)) (Manual: `.minecraft/config/shouldersurfing-client.cfg`)

<details>
  <summary>Version 2.9.7 and older</summary>

≥ 1.13: Manual: `.minecraft/config/shouldersurfing.toml`  
≤ 1.12: Ingame: Mods → ShoulderSurfing → Config (Manual: `.minecraft/config/shouldersurfing.cfg`)

</details>

# API Documentation #
The API Documentation can be found in the [wiki](https://github.com/Exopandora/ShoulderSurfing/wiki).
It currently allows other mod developers to define custom rules for when and what items should have adaptive crosshair functionality.

# Building From Source #
Clone the repository and run the following command in the root directory of this repository:
```bash
gradlew build
```
The binaries for each platform can be found in the following directories:

| Platform | Path                                  |
|----------|---------------------------------------|
| Forge    | `ShoulderSurfing/forge/build/libs`    |
| Fabric   | `ShoulderSurfing/fabric/build/libs`   |
| NeoForge | `ShoulderSurfing/neoforge/build/libs` |

# FAQ #
**Q:** Why is the player not looking at the position of the crosshair when holding item x?  
**A:** This needs to be configured using the config options `adaptive_crosshair_hold_items` or `adaptive_crosshair_use_items`.

**Q:** Why is the player not rendered correctly when using shader pack x?  
**A:** This is caused by player transparency effects of Shoulder Surfing Reloaded. Either disable them in the config (`adjust_player_transparency`) and restart your game or report this to the authors of the shader pack.

**Q:** Can you port feature x to version y?  
**A:** No.

**Q:** Can you port this to quilt, liteloader, rift, etc.?  
**A:** No.

**Q:** Can I add this to my mod pack?  
**A:** Yes.

# Compatibility #
<details>
  <summary>Builtin compatibility*</summary>

- 3D Skin Layers
- Cobblemon
- Create
- Curios
- Entity Model Features
- Iris
- Oculus
- OptiFine
- Sodium
- TslatEntityStatus
- Wildfire Gender
</details>

<details>
  <summary>Incompatible mods*</summary>

- Better Third Person
- Camera Utils
- Nimble
- Valkyrien Skies
- YDM's Custom Camera View

</details>

<details>
  <summary>Minor incompatibility issues*</summary>

- Clutter (hats do not render transparent)
- Epic Fight (player does not render transparent in battle mode, player keeps punching in the same direction)
- EssentialClient (player look script hooks do not work)
- Inmis (backpack does not render transparent)
- MedievalWeapons (player is permanently in aiming mode)
  - Workaround: Remove `minecraft:throwing` from `adaptive_crosshair_use_item_properties` in the config
- MrCrayfish's Gun Mod (crosshair does not render when using ads, fov does not update when using ads)
- Tweakeroo (freecam movement)
- YDM's Weapon Master (weapons do not render transparent)

</details>

*Mods are listed from a-z.

# License #
MIT License, Original project by Sabar: [Minecraft Forum](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1287308-shoulder-surfing-modded-third-person-camera), [GitHub](https://github.com/sabarjp/ShoulderSurfing)
