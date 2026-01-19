package com.runningbird.fabricbungeeforwarding.mixin;

import com.runningbird.fabricbungeeforwarding.BungeeForwardingMod;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMessageChainMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "getSignedMessage", at = @At("RETURN"), cancellable = true)
    private void bff$forceUnsignedChat(ServerboundChatPacket packet, LastSeenMessages lastSeenMessages, CallbackInfoReturnable<PlayerChatMessage> cir) {
        if (!BungeeForwardingMod.CONFIG.isForceSystemChat()) {
            return;
        }
        cir.setReturnValue(PlayerChatMessage.system(packet.message()));
    }
}
