package com.leon.saintsdragons.neoforge;

import net.neoforged.bus.api.IEventBus;

public final class NeoForgeModContext {
    private static IEventBus modEventBus;

    private NeoForgeModContext() {
    }

    public static void setModEventBus(IEventBus bus) {
        if (modEventBus != null) {
            throw new IllegalStateException("Mod event bus already set!");
        }
        modEventBus = bus;
    }

    public static IEventBus getModEventBus() {
        if (modEventBus == null) {
            throw new IllegalStateException("Mod event bus not yet initialized!");
        }
        return modEventBus;
    }
}
