package com.runningbird.fabricbungeeforwarding.mixin;

import com.runningbird.fabricbungeeforwarding.BungeeForwardingMod;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerSuppressChatMixin {
    @Inject(method = "broadcastChatMessage", at = @At("HEAD"), cancellable = true)
    private void bff$suppressBackendChat(PlayerChatMessage message, CallbackInfo ci) {
        if (!BungeeForwardingMod.CONFIG.isSuppressBackendChat()) {
            return;
        }
        ci.cancel();
    }
}
