package com.leon.saintsdragons.neoforge;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.common.config.dragon.DragonAttributeConfigLoader;
import com.leon.saintsdragons.neoforge.world.AddDragonsBiomeModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@Mod(SaintsDragonsCommon.MOD_ID)
public class SaintsDragonsNeoForge {
    private static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, SaintsDragonsCommon.MOD_ID);

    private static final Supplier<MapCodec<AddDragonsBiomeModifier>> ADD_DRAGONS_CODEC =
            BIOME_MODIFIERS.register("add_dragons", () -> AddDragonsBiomeModifier.CODEC);

    public SaintsDragonsNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        NeoForgeModContext.setModEventBus(modEventBus);

        // Register BiomeModifier codec
        BIOME_MODIFIERS.register(modEventBus);

        // Initialize common config (spawning config)
        com.leon.saintsdragons.common.config.SaintsDragonsConfig.bootstrap();

        // Register spawn config (saintsdragonsspawning.toml)
        modContainer.registerConfig(ModConfig.Type.COMMON,
                com.leon.saintsdragons.neoforge.platform.NeoForgeConfigHelper.SPAWN_SPEC,
                "saintsdragonsspawning.toml");

        // Register config screen for in-game editing
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // Register dragon attributes reload listener
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);

        SaintsDragonsCommon.init();
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(DragonAttributeConfigLoader.getInstance());
    }
}
