package org.sinytra.fabric.networking_api;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.server.commands.DebugConfigCommand;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import org.sinytra.fabric.networking_api.generated.GeneratedEntryPoint;
import org.sinytra.fabric.networking_api.server.NeoServerPlayNetworking;

@Mod(GeneratedEntryPoint.MOD_ID)
public class NetworkingEventHooks {

    public NetworkingEventHooks(IEventBus bus) {
        bus.addListener(NetworkingEventHooks::onConfiguration);
        NeoForge.EVENT_BUS.addListener(NetworkingEventHooks::registerCommands);
        NeoForge.EVENT_BUS.addListener(NetworkingEventHooks::onPlayerReady);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            // Command is registered when isDevelopment is set.
            return;
        }

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            // Only register this command in a dev env
            return;
        }

        DebugConfigCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    private static void onPlayerReady(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            NeoServerPlayNetworking.onClientReady(event.getPlayer());
        }
    }

    private static void onConfiguration(RegisterConfigurationTasksEvent event) {
        ServerConfigurationPacketListenerImpl listener = (ServerConfigurationPacketListenerImpl) event.getListener();
        ServerConfigurationConnectionEvents.CONFIGURE.invoker().onSendConfiguration(listener, listener.server);
    }
}
