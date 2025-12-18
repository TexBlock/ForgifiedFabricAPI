package org.sinytra.fabric.networking_api.server;

import net.fabricmc.fabric.api.networking.v1.S2CConfigurationChannelEvents;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;

import java.util.List;
import java.util.Set;

public class NeoServerCommonNetworking {
    public static void onRegisterPacket(ICommonPacketListener listener, Set<Identifier> ids) {
        ConnectionProtocol protocol = listener.protocol();
        MinecraftServer server = ((ServerCommonPacketListenerImpl) listener).server;
        NeoServerPacketSender packetSender = new NeoServerPacketSender(listener.getConnection());

        if (protocol == ConnectionProtocol.CONFIGURATION) {
            listener.getPacketProcessor().scheduleIfPossible(() -> S2CConfigurationChannelEvents.REGISTER.invoker().onChannelRegister((ServerConfigurationPacketListenerImpl) listener, packetSender, server, List.copyOf(ids)));
        } else if (protocol == ConnectionProtocol.PLAY) {
            listener.getPacketProcessor().scheduleIfPossible(() -> S2CPlayChannelEvents.REGISTER.invoker().onChannelRegister((ServerGamePacketListenerImpl) listener, packetSender, server, List.copyOf(ids)));
        }
    }

    public static void onUnregisterPacket(ICommonPacketListener listener, Set<Identifier> ids) {
        ConnectionProtocol protocol = listener.protocol();
        MinecraftServer server = ((ServerCommonPacketListenerImpl) listener).server;
        NeoServerPacketSender packetSender = new NeoServerPacketSender(listener.getConnection());

        if (protocol == ConnectionProtocol.CONFIGURATION) {
            listener.getPacketProcessor().scheduleIfPossible(() -> S2CConfigurationChannelEvents.UNREGISTER.invoker().onChannelUnregister((ServerConfigurationPacketListenerImpl) listener, packetSender, server, List.copyOf(ids)));
        } else if (protocol == ConnectionProtocol.PLAY) {
            listener.getPacketProcessor().scheduleIfPossible(() -> S2CPlayChannelEvents.UNREGISTER.invoker().onChannelUnregister((ServerGamePacketListenerImpl) listener, packetSender, server, List.copyOf(ids)));
        }
    }
}
