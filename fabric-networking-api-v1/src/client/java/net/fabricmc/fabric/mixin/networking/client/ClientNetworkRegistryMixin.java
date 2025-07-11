package net.fabricmc.fabric.mixin.networking.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.configuration.ClientConfigurationPacketListener;
import net.neoforged.neoforge.client.network.registration.ClientNetworkRegistry;
import net.neoforged.neoforge.network.registration.NetworkPayloadSetup;
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

    @Inject(method = "initializeNeoForgeConnection(Lnet/minecraft/network/protocol/configuration/ClientConfigurationPacketListener;Lnet/neoforged/neoforge/network/registration/NetworkPayloadSetup;)V", at = @At("TAIL"))
    private static void startConfiguration(ClientConfigurationPacketListener listener, NetworkPayloadSetup setup, CallbackInfo ci) {
        ClientConfigurationConnectionEvents.START.invoker().onConfigurationStart((ClientConfigurationPacketListenerImpl) listener, Minecraft.getInstance());
    }
}
