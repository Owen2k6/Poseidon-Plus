# Poseidon Plus

Designed to help your server with full customisation of any server message. wether its a whitelist kick message or a generic ban message. Plus.YML will assist you in making your server **YOUR** server

### Features of Poseidon Plus
 - Customisable server messages.
   - Kick Messages (base server kick message only)
   - Whitelist Kick messages
   - Ban messages (base server ban message only)
   - Server /stop message
   - Server /say format (instead of \[server\] you can make it anything you want and even use the &colour code system from essentials.)
 - Super Event System
   - Great for Authentication plugins as this runs at the highest priority in the event system. This prevents any other plugin messing with your authentication plugin.
   - Great for logging things. As this is ran above all other event listeners, you can listen to events even if theyre cancelled. so you can make chat loggers if you want to catch mute evaders!
 - Plus Commands
   - /restart So you can stop the server with a message of restart instead of "shutting down". Configurable in plus.yml
   - /modifyconfig Allowing you to make configuration changes ingame. 
   - /garbagecollect Will force a hard GC on the server to clean memory.
 - Command updates (Vanilla commands)
   - /reload Will warn you about the dangers of reloading everything. Then ask you to issue /reload confirm
   - /save-all will always remind you that you can just do /save instead.
   - /plugins has a bit of colour in its text. Nothing special, just for design.
   - /fakeop Joke command to trick people pretending to be from Planet Minecraft

Below is the Project-Poseidon Description. You may get basic poseidon support there but any Poseidon Plus issues should be reported to Owen#0505 or moderator_man#4356








---
# Project-Poseidon
![](/img/banner.png)
## What's Project Poseidon?
A CraftBukkit CB1060 fork for Beta 1.7.3 fixing bugs and adding basic features.<br>
Discord: https://discord.gg/FwKg676

## Want to use Project Poseidon on your server?
Please read the following article before changing over to Project Poseidon: https://github.com/RhysB/Project-Poseidon/wiki/Implementing-Project-Poseidon-In-Production

## Licensing
CraftBukkit and Bukkit are licensed under GNU General Public License v3.0<br>
Any future commits to this repository will remain under the same GNU General Public License v3.0<br>
Libraries in the compiled .jar files distrusted may contain their own licenses.<br>
This project contains decompiled code that is copyrighted by Mojang AB typically under the `net.minecraft.server` package.<br>

## How To Setup - IntelliJ IDEA

1. Clone this project using Git or a desktop client.
2. Open IntelliJ and create a new project in the same directory as the Project Poseidon folder.

## Compiling

Compiling is done via maven. To compile a JAR, cd into the Project Poseidon directory and run the following command:

```
mvn clean package
```

You should now have a runnable JAR inside the /target folder!

## Regarding the DMCA of CraftBukkit in 2014
The contributor Wolverness who first contributed on CraftBukkit in February 2012 issued a DMCA against CraftBukkit and other major forks of CraftBukkit.
This project is based on the following commits:

* CraftBukkit: [54bcd1c1f36691a714234e5ca2f30a20b3ad2816](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/commits/54bcd1c1f36691a714234e5ca2f30a20b3ad2816) (SpigotMC)
* Bukkit: [3524fde5ffc387ef9e39f6ee7dae83ff4dbf8229](https://github.com/Bukkit/Bukkit/commit/3524fde5ffc387ef9e39f6ee7dae83ff4dbf8229) (GitHub)

The Bukkit and CraftBukkit commits that Project Poseidon is based on are before Wolverness started contributing.

If you were a contributor before these commits please feel free to contact me or open an issue asking for this repository to be taken down.

## MC-DEV
We include files from the mc-dev GitHub repository. This code is automatically generated using minecraft_server.jar and sourced from the Bukkit repositories.
* MC-DEV: [1a792ed860ebe2c6d4c40c52f3bc7b9e0789ca23](https://github.com/Bukkit/mc-dev/commit/1a792ed860ebe2c6d4c40c52f3bc7b9e0789ca23)

If Mojang Studios or someone on their behalf wants to have this repository removed due to including copyrighted Minecraft sources like bukkit/mc-dev, please contact me or make an issue.

## How to setup ModLoaderMP support (NOT WORKING)
Please read the following: https://github.com/RhysB/Project-Poseidon/wiki/Adding-ModLoaderMP
