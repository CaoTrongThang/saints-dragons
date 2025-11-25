package com.leon.saintsdragons.neoforge.client;

import com.leon.saintsdragons.client.input.DragonRideInputHandler;
import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.neoforge.client.event.NeoForgeClientEventHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SaintsDragonsCommon.MOD_ID, value = Dist.CLIENT)
public final class NeoForgeClientGameEvents {
    private NeoForgeClientGameEvents() {}

    @SubscribeEvent
    public static void onClientTick(PlayerTickEvent.Post event) {
        // Handle dragon ride input on client tick
        if (event.getEntity().level().isClientSide()) {
            DragonRideInputHandler.clientTick();
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        // Drive client-only sound controllers each tick
        NeoForgeClientEventHandler.onClientTick(Minecraft.getInstance());
    }

    // Removed - using mixin instead for proper timing
    // ViewportEvent fires too late, camera position is already locked
}
