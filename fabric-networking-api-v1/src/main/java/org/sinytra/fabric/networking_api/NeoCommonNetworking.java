package org.sinytra.fabric.networking_api;

import net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.Identifier;

public class NeoCommonNetworking {
    public static final NeoNetworkRegistrar CONFIGURATION_REGISTRY = new NeoNetworkRegistrar(ConnectionProtocol.CONFIGURATION);
    public static final NeoNetworkRegistrar PLAY_REGISTRY = new NeoNetworkRegistrar(ConnectionProtocol.PLAY);

    public static final int DEFAULT_CHANNEL_NAME_MAX_LENGTH = 128;

    public static void assertPayloadType(PayloadTypeRegistryImpl<?> payloadTypeRegistry, Identifier channelName, PacketFlow side, ConnectionProtocol phase) {
        if (payloadTypeRegistry == null) {
            return;
        }

        if (payloadTypeRegistry.get(channelName) == null) {
            throw new IllegalArgumentException(String.format("Cannot register handler as no payload type has been registered with name \"%s\" for %s %s", channelName, side, phase));
        }

        if (channelName.toString().length() > DEFAULT_CHANNEL_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("Cannot register handler for channel with name \"%s\" as it exceeds the maximum length of 128 characters", channelName));
        }
    }
}
