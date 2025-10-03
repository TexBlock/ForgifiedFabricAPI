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

package net.fabricmc.fabric.mixin.networking.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import org.sinytra.fabric.networking_api.NeoListenableNetworkHandler;
import org.sinytra.fabric.networking_api.client.NeoClientConfigurationNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;

// We want to apply a bit earlier than other mods which may not use us in order to prevent refCount issues
@Mixin(value = ClientConfigurationPacketListenerImpl.class, priority = 999)
public abstract class ClientConfigurationNetworkHandlerMixin extends ClientCommonPacketListenerImpl implements NeoListenableNetworkHandler {

	protected ClientConfigurationNetworkHandlerMixin(Minecraft client, Connection connection, CommonListenerCookie connectionState) {
		super(client, connection, connectionState);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void initAddon(CallbackInfo ci) {
		NeoClientConfigurationNetworking.setClientConfigurationAddon(this);
		ClientConfigurationConnectionEvents.INIT.invoker().onConfigurationInit((ClientConfigurationPacketListenerImpl) (Object) this, this.minecraft);
	}

	@Inject(method = "handleConfigurationFinished", at = @At(value = "NEW", target = "(Lnet/minecraft/client/Minecraft;Lnet/minecraft/network/Connection;Lnet/minecraft/client/multiplayer/CommonListenerCookie;)Lnet/minecraft/client/multiplayer/ClientPacketListener;"))
	public void handleComplete(ClientboundFinishConfigurationPacket packet, CallbackInfo ci) {
		ClientConfigurationConnectionEvents.COMPLETE.invoker().onConfigurationComplete((ClientConfigurationPacketListenerImpl) (Object) this, this.minecraft);
		ClientConfigurationConnectionEvents.READY.invoker().onConfigurationReady((ClientConfigurationPacketListenerImpl) (Object) this, this.minecraft);
		NeoClientConfigurationNetworking.setClientConfigurationAddon(null);
	}

	@Override
	public void handleDisconnect() {
		ClientConfigurationConnectionEvents.DISCONNECT.invoker().onConfigurationDisconnect((ClientConfigurationPacketListenerImpl) (Object) this, this.minecraft);
	}
}
