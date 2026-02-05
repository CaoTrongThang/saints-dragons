package com.leon.saintsdragons.neoforge;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.common.config.dragon.DragonAttributeConfigLoader;
import com.leon.saintsdragons.neoforge.loot.ModLootModifiers;
import com.leon.saintsdragons.neoforge.world.AddDragonsBiomeModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.minecraft.world.phys.AABB;

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

        // Register loot modifiers
        ModLootModifiers.register(modEventBus);

        // Initialize common config (spawning config)
        com.leon.saintsdragons.common.config.SaintsDragonsConfig.bootstrap();

        // Register spawn config (saintsdragonsspawning.toml)
        modContainer.registerConfig(ModConfig.Type.COMMON,
                com.leon.saintsdragons.neoforge.platform.NeoForgeConfigHelper.SPAWN_SPEC,
                "saintsdragonsspawning.toml");

        // Register dragon attributes config (saintsdragons-attributes.toml)
        modContainer.registerConfig(ModConfig.Type.COMMON,
                com.leon.saintsdragons.neoforge.platform.NeoForgeDragonAttributesConfig.ATTRIBUTES_SPEC,
                "saintsdragons-attributes.toml");

        // Register config screen for in-game editing
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // Refresh dragon attributes when the NeoForge config loads/changes
        modEventBus.addListener(this::onModConfigEvent);

        // Register dragon attributes reload listener
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);

        SaintsDragonsCommon.init();
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(DragonAttributeConfigLoader.getInstance());
    }

    private void onModConfigEvent(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (!SaintsDragonsCommon.MOD_ID.equals(config.getModId())) {
            return;
        }
        if (config.getType() != ModConfig.Type.COMMON) {
            return;
        }
        if (!"saintsdragons-attributes.toml".equals(config.getFileName())) {
            return;
        }

        DragonAttributeConfigLoader.getInstance().refreshFromForgeConfig();
        applyAttributesToLoadedDragons();
    }

    private void applyAttributesToLoadedDragons() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }

        for (var level : server.getAllLevels()) {
            AABB bounds = new AABB(
                    level.getWorldBorder().getMinX(),
                    level.getMinBuildHeight(),
                    level.getWorldBorder().getMinZ(),
                    level.getWorldBorder().getMaxX(),
                    level.getMaxBuildHeight(),
                    level.getWorldBorder().getMaxZ()
            );

            for (var dragon : level.getEntitiesOfClass(com.leon.saintsdragons.server.entity.base.DragonEntity.class, bounds)) {
                if (dragon instanceof com.leon.saintsdragons.server.entity.dragons.cindervane.Cindervane cindervane) {
                    cindervane.applyConfiguredAttributes();
                } else if (dragon instanceof com.leon.saintsdragons.server.entity.dragons.raevyx.Raevyx raevyx) {
                    raevyx.applyConfiguredAttributes();
                } else if (dragon instanceof com.leon.saintsdragons.server.entity.dragons.nulljaw.Nulljaw nulljaw) {
                    nulljaw.applyConfiguredAttributes();
                } else if (dragon instanceof com.leon.saintsdragons.server.entity.dragons.ignivorus.Ignivorus ignivorus) {
                    ignivorus.applyConfiguredAttributes();
                } else if (dragon instanceof com.leon.saintsdragons.server.entity.dragons.stegonaut.Stegonaut stegonaut) {
                    stegonaut.applyConfiguredAttributes();
                }
            }
        }
    }
}
