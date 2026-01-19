package com.runningbird.fabricbungeeforwarding;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BungeeForwardingConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("fabric-bungee-forwarding");

    private boolean forceSystemChat = true;
    private boolean suppressBackendChat = true;

    public static BungeeForwardingConfig load() {
        Path configPath = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("fabric-bungee-forwarding.toml");

        BungeeForwardingConfig config;
        if (Files.exists(configPath)) {
            config = new Toml().read(configPath.toFile()).to(BungeeForwardingConfig.class);
        } else {
            config = new BungeeForwardingConfig();
        }

        File configFile = configPath.toFile();
        if (!configFile.exists() || configFile.canWrite()) {
            try {
                new TomlWriter().write(config, configFile);
            } catch (IOException e) {
                LOGGER.error("Failed to write config {}", configPath, e);
            }
        } else {
            LOGGER.warn("Config file is not writable: {}", configPath);
        }

        return config;
    }

    public boolean isForceSystemChat() {
        return forceSystemChat;
    }

    public boolean isSuppressBackendChat() {
        return suppressBackendChat;
    }
}
