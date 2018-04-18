ShoulderSurfing
===============

Mod for Minecraft that adds a revamped over-the-shoulder F5 third-person camera view

This source code is provided _as-is_.

Nearly all the source is dependent on the Forge API.

Features
========

* Over-the-shoulder camera
* Hotkeys to move the camera a bit (Default: Arrow keys, O to swap shoulders)
* Corrective cross-hair positioning if dynamic crosshair is enabled
* Hide player if the camera gets too close to it

Download
========
Compiled and signed versions can be found here:
https://minecraft.curseforge.com/projects/shoulder-surfing-reloaded

Required: Forge universal files for your minecraft version (http://files.minecraftforge.net/)

Compatibility
=============
Mods that heavily modify certain base classes may break this mod. Your forge client log will show if the code injection fails or not.
This mod modifies:
* EntityRenderer#orientCamera
* EntityRenderer#renderWorldPass
* Minecraft#processKeyBinds

Install
=======
First install Forge universal files from files.minecraftforge.net. Make sure it is an up-to-date version for your version on minecraft.

Download ShoulderSurfing or build from source

The JAR file needs to go into the MODS folder under Minecraft. Make sure to remove any previous versions from the COREMODS and MODS folder. This mod will not work if placed anywhere but the MODS folder!
