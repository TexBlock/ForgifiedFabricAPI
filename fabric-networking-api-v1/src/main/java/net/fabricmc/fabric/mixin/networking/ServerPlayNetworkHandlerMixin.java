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

package net.fabricmc.fabric.mixin.networking;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.sinytra.fabric.networking_api.NeoListenableNetworkHandler;
import org.sinytra.fabric.networking_api.server.NeoServerConfigurationNetworking;
import org.sinytra.fabric.networking_api.server.NeoServerPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

// We want to apply a bit earlier than other mods which may not use us in order to prevent refCount issues
@Mixin(value = ServerGamePacketListenerImpl.class, priority = 999)
abstract class ServerPlayNetworkHandlerMixin extends ServerCommonPacketListenerImpl implements NeoListenableNetworkHandler {

	ServerPlayNetworkHandlerMixin(MinecraftServer server, Connection connection, CommonListenerCookie arg) {
		super(server, connection, arg);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void initAddon(CallbackInfo ci) {
		ServerPlayConnectionEvents.INIT.invoker().onPlayInit((ServerGamePacketListenerImpl) (Object) this, server);
	}

	@WrapOperation(method = "handleConfigurationAcknowledged", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setupInboundProtocol(Lnet/minecraft/network/ProtocolInfo;Lnet/minecraft/network/PacketListener;)V"))
	private <T extends PacketListener> void onAcknowledgeReconfiguration(Connection instance, ProtocolInfo<T> state, T packetListener, Operation<Void> original) {
        original.call(instance, state, packetListener);

        ServerConfigurationPacketListenerImpl networkHandler = (ServerConfigurationPacketListenerImpl) packetListener;
        NeoServerConfigurationNetworking.setReconfiguring(networkHandler);

        if (NeoServerPlayNetworking.requestedReconfigure()) {
            networkHandler.startConfiguration();
        }
    }

    @Override
    public void handleDisconnect() {
        ServerPlayConnectionEvents.DISCONNECT.invoker().onPlayDisconnect((ServerGamePacketListenerImpl) (Object) this, server);
    }
}
