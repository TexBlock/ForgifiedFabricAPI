package org.sinytra.fabric.networking_api.client;

import io.netty.channel.ChannelFutureListener;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record NeoClientPacketSender(Connection connection) implements PacketSender {
    @Override
    public Packet<?> createPacket(CustomPacketPayload packet) {
        return ClientPlayNetworking.createC2SPacket(packet);
    }

    @Override
    public void sendPacket(Packet<?> packet, @Nullable ChannelFutureListener callback) {
        Objects.requireNonNull(packet, "Packet cannot be null");

        connection.send(packet, callback);
    }

    @Override
    public void disconnect(Component disconnectReason) {
        Objects.requireNonNull(disconnectReason, "Disconnect reason cannot be null");

        connection.disconnect(disconnectReason);
    }
}