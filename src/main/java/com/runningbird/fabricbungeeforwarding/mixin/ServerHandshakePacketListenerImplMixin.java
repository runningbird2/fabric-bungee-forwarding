package com.runningbird.fabricbungeeforwarding.mixin;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UndashedUuid;
import com.runningbird.fabricbungeeforwarding.net.ForwardedDataHolder;
import com.runningbird.fabricbungeeforwarding.BungeeForwardingMod;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.regex.Pattern;

@Mixin(ServerHandshakePacketListenerImpl.class)
public abstract class ServerHandshakePacketListenerImplMixin {
    private static final Gson BFF_GSON = new Gson();
    private static final Pattern BFF_HOST_PATTERN = Pattern.compile("[0-9a-fA-F\\.:]{0,45}");
    private static final Pattern BFF_PROP_PATTERN = Pattern.compile("\\w{0,16}");

    @Shadow
    @Final
    MinecraftServer server;

    @Shadow
    @Final
    Connection connection;

    @Inject(method = "beginLogin", at = @At("HEAD"), cancellable = true)
    private void bff$parseForwarding(ClientIntentionPacket packet, boolean transferred, CallbackInfo ci) {
        if (packet.intention() != ClientIntent.LOGIN) {
            return;
        }

        String rawHost = packet.hostName();
        BungeeForwardingMod.LOGGER.debug("[BFF] Incoming host string: {}", rawHost);
        String[] parts = rawHost.split("\u0000");
        boolean hasForwarding = parts.length == 3 || parts.length == 4;
        if (!hasForwarding || !BFF_HOST_PATTERN.matcher(parts[1]).matches()) {
            Component message = Component.literal("If you wish to use IP forwarding, please enable it in your proxy config as well!");
            this.connection.send(new ClientboundLoginDisconnectPacket(message));
            this.connection.disconnect(message);
            BungeeForwardingMod.LOGGER.warn("[BFF] Rejecting connection: invalid forwarding payload parts={} host={}", parts.length, rawHost);
            ci.cancel();
            return;
        }

        UUID uuid;
        try {
            uuid = UndashedUuid.fromStringLenient(parts[2]);
        } catch (IllegalArgumentException ex) {
            Component message = Component.literal("Invalid forwarded UUID from proxy.");
            this.connection.send(new ClientboundLoginDisconnectPacket(message));
            this.connection.disconnect(message);
            BungeeForwardingMod.LOGGER.warn("[BFF] Rejecting connection: bad UUID {}, error={}", parts[2], ex.toString());
            ci.cancel();
            return;
        }

        Property[] properties = new Property[0];
        if (parts.length == 4) {
            try {
                Property[] decoded = BFF_GSON.fromJson(parts[3], Property[].class);
                properties = decoded == null ? new Property[0] : decoded;
            } catch (Exception ex) {
                Component message = Component.literal("Unable to read player profile from proxy.");
                this.connection.send(new ClientboundLoginDisconnectPacket(message));
                this.connection.disconnect(message);
                BungeeForwardingMod.LOGGER.warn("[BFF] Rejecting connection: profile parse failed, error={}", ex.toString());
                ci.cancel();
                return;
            }
        }

        InetSocketAddress remote = this.connection.getRemoteAddress() instanceof InetSocketAddress inet ? inet : null;
        if (remote == null) {
            Component message = Component.literal("Unable to determine remote address for forwarding.");
            this.connection.send(new ClientboundLoginDisconnectPacket(message));
            this.connection.disconnect(message);
            BungeeForwardingMod.LOGGER.warn("[BFF] Rejecting connection: remote address missing");
            ci.cancel();
            return;
        }

        Property[] filtered = properties.length == 0 ? properties : java.util.Arrays.stream(properties)
            .filter(prop -> prop != null && BFF_PROP_PATTERN.matcher(prop.name()).matches())
            .toArray(Property[]::new);

        ((ForwardedDataHolder) this.connection).bff$setForwardedData(uuid, filtered, parts[0], new InetSocketAddress(parts[1], remote.getPort()));
        BungeeForwardingMod.LOGGER.debug("[BFF] Forwarded data accepted: realHost={} forwardedIp={} uuid={} props={}", parts[0], parts[1], uuid, filtered.length);
    }
}
