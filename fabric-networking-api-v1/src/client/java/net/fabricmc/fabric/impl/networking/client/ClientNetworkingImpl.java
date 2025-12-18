/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.networking.client;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.impl.networking.GlobalReceiverRegistry;
import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.mixin.networking.client.accessor.ConnectScreenAccessor;
import net.fabricmc.fabric.mixin.networking.client.accessor.MinecraftAccessor;

public final class ClientNetworkingImpl {
	public static final GlobalReceiverRegistry<ClientLoginNetworking.LoginQueryRequestHandler> LOGIN = new GlobalReceiverRegistry<>(PacketFlow.CLIENTBOUND, ConnectionProtocol.LOGIN, null);

	public static ClientLoginNetworkAddon getAddon(ClientHandshakePacketListenerImpl handler) {
		return (ClientLoginNetworkAddon) ((NetworkHandlerExtensions) handler).getAddon();
	}

	public static Packet<ServerCommonPacketListener> createC2SPacket(CustomPacketPayload payload) {
		Objects.requireNonNull(payload, "Payload cannot be null");
		Objects.requireNonNull(payload.type(), "CustomPayload#getId() cannot return null for payload class: " + payload.getClass());

		return new ServerboundCustomPayloadPacket(payload);
	}

	/**
	 * Due to the way logging into an integrated or remote dedicated server will differ, we need to obtain the login client connection differently.
	 */
	@Nullable
	public static Connection getLoginConnection() {
		final Connection connection = ((MinecraftAccessor) Minecraft.getInstance()).getConnection();

		// Check if we are connecting to an integrated server. This will set the field on MinecraftClient
		if (connection != null) {
			return connection;
		} else {
			// We are probably connecting to a remote server.
			// Check if the ConnectScreen is the currentScreen to determine that:
			if (Minecraft.getInstance().screen instanceof ConnectScreen) {
				return ((ConnectScreenAccessor) Minecraft.getInstance().screen).getConnection();
			}
		}

		// We are not connected to a server at all.
		return null;
	}
}
