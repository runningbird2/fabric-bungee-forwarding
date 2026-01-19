# FabricProxy Bungee 

Fabric server-side mod that enables BungeeCord/Velocity *legacy* forwarding (IP + UUID + profile data) on your backend servers.

- Only allows logins that include the bungee forwarding payload (`host\0ip\0uuid\0profileJson`). Please esnure that your backend server is not accesible from the internet! (https://www.spigotmc.org/wiki/firewall-guide/)

## Supported versions
- Minecraft: 1.20.5–1.21.11 (Fabric)
- Java: 21

## Quick start (proxy + backend)
1) Remove any other forwarding mods from the backend (`mods/`), e.g. FabricProxy-Lite.  
2) Drop the latest `fabric-bungee-forwarding-<mod>+<mc>.jar` into the backend `mods/` folder.  
3) Proxy: enable legacy/“bungeecord” forwarding (Velocity: `player-info-forwarding-mode = legacy`; Bungee/Waterfall: standard IP forwarding).  
4) Connect through the proxy; direct connections will be rejected unless they include forwarding data.

No config file is required. Keep one forwarding mod per backend to avoid conflicts.

## Chat handling
Some proxies/backends can log warnings like `Received chat packet with missing or invalid signature` after server switches.  
This mod can force chat messages to be sent as disguised system chat to avoid those errors (and related chat failures), and it can suppress backend chat broadcasts to prevent duplicates when the proxy already relays chat.

Configure in `config/fabric-bungee-forwarding.toml` (default: enabled):
```toml
hackMessageChain = true
skipLastSeenValidation = false
forceSystemChat = true
suppressBackendChat = true
```

Note: This disables signed chat verification on the backend.

## Building from source
```bash
./gradlew build
```
The remapped jar will be in `build/libs/fabric-bungee-forwarding-<mod_version>+<mc_version>.jar`.
