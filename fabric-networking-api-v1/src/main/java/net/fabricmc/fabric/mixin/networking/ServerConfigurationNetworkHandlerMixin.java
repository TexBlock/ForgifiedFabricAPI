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

import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.impl.networking.FabricRegistryByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.common.extensions.IServerConfigurationPacketListenerExtension;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.sinytra.fabric.networking_api.NeoListenableNetworkHandler;
import org.sinytra.fabric.networking_api.server.NeoServerConfigurationNetworking;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.fabricmc.fabric.api.networking.v1.FabricServerConfigurationNetworkHandler;

// We want to apply a bit earlier than other mods which may not use us in order to prevent refCount issues
@Mixin(value = ServerConfigurationPacketListenerImpl.class, priority = 900)
public abstract class ServerConfigurationNetworkHandlerMixin implements FabricServerConfigurationNetworkHandler, NeoListenableNetworkHandler {
    @Shadow
    @Final
    private Queue<ConfigurationTask> configurationTasks;

    @Override
    public void addTask(ConfigurationTask task) {
        configurationTasks.add(task);
    }

    @Override
    public void completeTask(ConfigurationTask.Type key) {
        ((IServerConfigurationPacketListenerExtension) this).finishCurrentTask(key);
    }

    @Inject(method = "runConfiguration", at = @At("HEAD"))
    private void onPreConfiguration(CallbackInfo ci) {
        ServerConfigurationConnectionEvents.BEFORE_CONFIGURE.invoker().onSendConfiguration((ServerConfigurationPacketListenerImpl) (Object) this, ((ServerConfigurationPacketListenerImpl) (Object) this).server);
    }
    
    @Override
    public void handleDisconnect() {
        ServerConfigurationConnectionEvents.DISCONNECT.invoker().onConfigureDisconnect((ServerConfigurationPacketListenerImpl) (Object) this, ((ServerConfigurationPacketListenerImpl) (Object) this).server);
    }

	@WrapOperation(method = "handleConfigurationFinished", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/RegistryFriendlyByteBuf;decorator(Lnet/minecraft/core/RegistryAccess;Lnet/neoforged/neoforge/network/connection/ConnectionType;)Ljava/util/function/Function;"))
	private Function<ByteBuf, RegistryFriendlyByteBuf> bindChannelInfo(RegistryAccess registryManager, ConnectionType connectionType, Operation<Function<ByteBuf, RegistryFriendlyByteBuf>> original) {
		return original.call(registryManager, connectionType).andThen(registryByteBuf -> {
			FabricRegistryByteBuf fabricRegistryByteBuf = (FabricRegistryByteBuf) registryByteBuf;
			fabricRegistryByteBuf.fabric_setSendableConfigurationChannels(Set.copyOf(NeoServerConfigurationNetworking.getSendable((ServerConfigurationPacketListenerImpl) (Object) this)));
			return registryByteBuf;
		});
	}
}
