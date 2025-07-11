package org.sinytra.fabric.networking_api.server;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import org.sinytra.fabric.networking_api.NeoCommonNetworking;

import java.util.Set;

public class NeoServerConfigurationNetworking {
    private static boolean isReconfiguring = false;

    public static <T extends CustomPacketPayload> boolean registerGlobalReceiver(CustomPacketPayload.Type<T> type, ServerConfigurationNetworking.ConfigurationPacketHandler<T> handler) {
        NeoCommonNetworking.assertPayloadType(PayloadTypeRegistryImpl.CONFIGURATION_C2S, type.id(), PacketFlow.SERVERBOUND, ConnectionProtocol.CONFIGURATION);
        return NeoCommonNetworking.CONFIGURATION_REGISTRY.registerGlobalReceiver(type, PacketFlow.SERVERBOUND, handler, ServerConfigNeoContextWrapper::new, ServerConfigurationNetworking.ConfigurationPacketHandler::receive);
    }

    public static ServerConfigurationNetworking.ConfigurationPacketHandler<?> unregisterGlobalReceiver(ResourceLocation id) {
        return NeoCommonNetworking.CONFIGURATION_REGISTRY.unregisterGlobalReceiver(id, PacketFlow.SERVERBOUND);
    }

    public static Set<ResourceLocation> getGlobalReceivers() {
        return NeoCommonNetworking.CONFIGURATION_REGISTRY.getGlobalReceivers(PacketFlow.SERVERBOUND);
    }

    public static <T extends CustomPacketPayload> boolean registerReceiver(ServerConfigurationPacketListenerImpl networkHandler, CustomPacketPayload.Type<T> type, ServerConfigurationNetworking.ConfigurationPacketHandler<T> handler) {
        NeoCommonNetworking.assertPayloadType(PayloadTypeRegistryImpl.CONFIGURATION_C2S, type.id(), PacketFlow.SERVERBOUND, ConnectionProtocol.CONFIGURATION);
        return NeoCommonNetworking.CONFIGURATION_REGISTRY.registerLocalReceiver(type, networkHandler, handler, ServerConfigNeoContextWrapper::new, ServerConfigurationNetworking.ConfigurationPacketHandler::receive);
    }

    public static ServerConfigurationNetworking.ConfigurationPacketHandler<?> unregisterReceiver(ServerConfigurationPacketListenerImpl networkHandler, ResourceLocation id) {
        return NeoCommonNetworking.CONFIGURATION_REGISTRY.unregisterLocalReceiver(id, networkHandler);
    }

    public static Set<ResourceLocation> getReceived(ServerConfigurationPacketListenerImpl handler) throws IllegalStateException {
        return NeoCommonNetworking.CONFIGURATION_REGISTRY.getLocalReceivers(handler);
    }

    public static Set<ResourceLocation> getSendable(ServerConfigurationPacketListenerImpl handler) throws IllegalStateException {
        return NeoCommonNetworking.CONFIGURATION_REGISTRY.getLocalSendable(handler);
    }

    public static boolean canSend(ServerConfigurationPacketListenerImpl handler, ResourceLocation channelName) throws IllegalArgumentException {
        return NetworkRegistry.hasChannel(handler, channelName);
    }

    public static PacketSender getSender(ServerConfigurationPacketListenerImpl handler) {
        return new NeoServerPacketSender(handler.getConnection());
    }

    public static boolean isReconfiguring(ServerConfigurationPacketListenerImpl handler) {
        return isReconfiguring;
    }

    public static void setReconfiguring(ServerConfigurationPacketListenerImpl handler) {
        isReconfiguring = true;
    }

    private record ServerConfigNeoContextWrapper(IPayloadContext context) implements ServerConfigurationNetworking.Context {
        @Override
        public MinecraftServer server() {
            return networkHandler().server;
        }

        @Override
        public ServerConfigurationPacketListenerImpl networkHandler() {
            return (ServerConfigurationPacketListenerImpl) context.listener();
        }

        @Override
        public PacketSender responseSender() {
            return new NeoServerPacketSender(context.connection());
        }
    }
}
