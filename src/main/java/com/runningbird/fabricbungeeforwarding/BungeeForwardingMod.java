package com.runningbird.fabricbungeeforwarding;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BungeeForwardingMod implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("fabric-bungee-forwarding");
    public static final BungeeForwardingConfig CONFIG = BungeeForwardingConfig.load();

    @Override
    public void onInitializeServer() {
        LOGGER.info("Fabric Bungee Forwarding loaded; plain logins without proxy data will be rejected.");
        if (CONFIG.isForceSystemChat()) {
            LOGGER.info("Chat replacement enabled (disguised system chat).");
        } else {
            LOGGER.info("Chat replacement disabled.");
        }
        if (CONFIG.isSuppressBackendChat()) {
            LOGGER.info("Backend chat broadcast suppression enabled.");
        }
    }
}
