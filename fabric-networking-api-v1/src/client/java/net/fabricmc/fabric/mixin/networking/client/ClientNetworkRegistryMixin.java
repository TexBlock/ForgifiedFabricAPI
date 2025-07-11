package net.fabricmc.fabric.mixin.networking.client;

import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.neoforged.neoforge.client.network.registration.ClientNetworkRegistry;
import org.sinytra.fabric.networking_api.NeoNetworkRegistrar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientNetworkRegistry.class)
public class ClientNetworkRegistryMixin {
    @Inject(method = "handleModdedPayload(Lnet/minecraft/network/protocol/common/ClientCommonPacketListener;Lnet/minecraft/network/protocol/common/ClientboundCustomPayloadPacket;)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V", ordinal = 1), cancellable = true)
    private static void preventDisconnectOnUnknownFabricPacketClient(ClientCommonPacketListener listener, ClientboundCustomPayloadPacket packet, CallbackInfo info) {
        if (NeoNetworkRegistrar.hasCodecFor(listener.protocol(), packet.type().flow(), packet.payload().type().id())) {
            info.cancel();
        }
    }
}
