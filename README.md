# Fabric Bungee Forwarding

Fabric server-side mod that enables BungeeCord/Velocity *legacy* forwarding (IP + UUID + profile data) on your backend servers.

- Only allows logins that include the legacy forwarding payload (`host\0ip\0uuid\0profileJson`).
- Rejects plain/offline logins before authentication with a clear disconnect message.

## Supported versions
- Minecraft: 1.20.4–1.21.10 (Fabric)
- Java: 21

## Quick start (proxy + backend)
1) Remove any other forwarding mods from the backend (`mods/`), e.g. FabricProxy-Lite.  
2) Drop the latest `fabric-bungee-forwarding-<mod>+<mc>.jar` into the backend `mods/` folder.  
3) Proxy: enable legacy/“bungeecord” forwarding (Velocity: `player-info-forwarding-mode = legacy`; Bungee/Waterfall: standard IP forwarding).  
4) Connect through the proxy; direct connections will be rejected unless they include forwarding data.

No config file is required. Keep one forwarding mod per backend to avoid conflicts.

## Building from source
```bash
./gradlew build
```
The remapped jar will be in `build/libs/fabric-bungee-forwarding-<mod_version>+<mc_version>.jar`.
