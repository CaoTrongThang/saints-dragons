package com.leon.saintsdragons.fabric.client;

import com.leon.saintsdragons.fabric.entity.part.FabricDragonPart;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles cleanup of orphaned dragon parts on the client side.
 * In 1.21.1, manually-added entities need extra cleanup logic.
 */
@Environment(EnvType.CLIENT)
public class ClientPartCleanupHandler {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientPartCleanupHandler::onClientTick);
    }

    private static void onClientTick(Minecraft client) {
        ClientLevel level = client.level;
        if (level == null) {
            return;
        }

        // Find orphaned parts (parts whose parent is removed or doesn't exist)
        List<FabricDragonPart> orphanedParts = new ArrayList<>();

        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof FabricDragonPart part) {
                if (part.parent == null || part.parent.isRemoved()) {
                    orphanedParts.add(part);
                }
            }
        }

        // Remove orphaned parts
        for (FabricDragonPart part : orphanedParts) {
            part.discard();
        }
    }
}
