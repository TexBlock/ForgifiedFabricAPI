package org.sinytra.fabric.networking_api.client;

import net.fabricmc.fabric.api.client.networking.v1.C2SConfigurationChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;

import java.util.List;
import java.util.Set;

public class NeoClientCommonNetworking {
    public static void onRegisterPacket(ICommonPacketListener listener, Set<ResourceLocation> ids) {
        ConnectionProtocol protocol = listener.protocol();
        List<ResourceLocation> listIds = List.copyOf(ids);
        if (protocol == ConnectionProtocol.CONFIGURATION) {
            listener.getPacketProcessor().scheduleIfPossible(() -> C2SConfigurationChannelEvents.REGISTER.invoker().onChannelRegister((ClientConfigurationPacketListenerImpl) listener, new NeoClientPacketSender(listener.getConnection()), Minecraft.getInstance(), listIds));
        } else if (protocol == ConnectionProtocol.PLAY) {
            listener.getPacketProcessor().scheduleIfPossible(() -> C2SPlayChannelEvents.REGISTER.invoker().onChannelRegister((ClientPacketListener) listener, new NeoClientPacketSender(listener.getConnection()), Minecraft.getInstance(), listIds));
        }
    }

    public static void onUnregisterPacket(ICommonPacketListener listener, Set<ResourceLocation> ids) {
        ConnectionProtocol protocol = listener.protocol();
        List<ResourceLocation> listIds = List.copyOf(ids);
        if (protocol == ConnectionProtocol.CONFIGURATION) {
            listener.getPacketProcessor().scheduleIfPossible(() -> C2SConfigurationChannelEvents.UNREGISTER.invoker().onChannelUnregister((ClientConfigurationPacketListenerImpl) listener, new NeoClientPacketSender(listener.getConnection()), Minecraft.getInstance(), listIds));
        } else if (protocol == ConnectionProtocol.PLAY) {
            listener.getPacketProcessor().scheduleIfPossible(() -> C2SPlayChannelEvents.UNREGISTER.invoker().onChannelUnregister((ClientPacketListener) listener, new NeoClientPacketSender(listener.getConnection()), Minecraft.getInstance(), listIds));
        }
    }
}
