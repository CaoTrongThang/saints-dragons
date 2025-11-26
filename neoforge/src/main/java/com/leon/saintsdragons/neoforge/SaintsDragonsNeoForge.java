package com.leon.saintsdragons.neoforge;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.common.config.dragon.DragonAttributeConfig;
import com.leon.saintsdragons.common.config.dragon.DragonAttributeConfigLoader;
import com.leon.saintsdragons.neoforge.config.SaintsDragonsNeoForgeConfig;
import com.leon.saintsdragons.neoforge.world.AddDragonsBiomeModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;
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

        // Initialize common config first (spawning config)
        com.leon.saintsdragons.common.config.SaintsDragonsConfig.bootstrap();

        // Register spawn config (saintsdragonsspawning.toml)
        modContainer.registerConfig(ModConfig.Type.COMMON,
                com.leon.saintsdragons.neoforge.platform.NeoForgeConfigHelper.SPAWN_SPEC,
                "saintsdragonsspawning.toml");

        // Register dragon attributes config (saintsdragons-common.toml)
        modContainer.registerConfig(ModConfig.Type.COMMON, SaintsDragonsNeoForgeConfig.COMMON_SPEC, "saintsdragons-common.toml");

        // Register built-in config screen (accessible via Mods menu → Select Saints Dragons → Config button)
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // Listen for config load and reload events to sync to DragonAttributeConfigLoader
        modEventBus.addListener(this::onConfigLoad);
        modEventBus.addListener(this::onConfigReload);

        SaintsDragonsCommon.init();
    }

    private void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            syncConfigToAttributeLoader();
        }
    }

    private void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            syncConfigToAttributeLoader();
        }
    }

    private void syncConfigToAttributeLoader() {
        DragonAttributeConfigLoader loader = DragonAttributeConfigLoader.getInstance();

        // Force reload defaults with new config values by re-creating the configs
        // This will read the updated NeoForge config values via reflection
        loader.overwriteConfig(DragonAttributeConfigLoader.CINDERVANE_ID, createCindervaneConfig());
        loader.overwriteConfig(DragonAttributeConfigLoader.RAEVYX_ID, createRaevyxConfig());
        loader.overwriteConfig(DragonAttributeConfigLoader.NULLJAW_ID, createNulljawConfig());
        loader.overwriteConfig(DragonAttributeConfigLoader.IGNIVORUS_ID, createIgnivorusConfig());
        loader.overwriteConfig(DragonAttributeConfigLoader.STEGONAUT_ID, createStegonautConfig());
    }

    private DragonAttributeConfig createCindervaneConfig() {
        DragonAttributeConfig defaults = DragonAttributeConfigLoader.getInstance().getDefaultConfig(DragonAttributeConfigLoader.CINDERVANE_ID);
        return new DragonAttributeConfig(
                SaintsDragonsNeoForgeConfig.CINDERVANE_MAX_HEALTH.get(),
                SaintsDragonsNeoForgeConfig.CINDERVANE_ATTACK_DAMAGE.get(),
                0.0D,  // Movement speed not configurable
                SaintsDragonsNeoForgeConfig.CINDERVANE_FLYING_SPEED.get(),
                defaults.abilities(),  // Keep existing abilities
                Map.of(
                        "run_speed", defaults.extraDouble("run_speed", 0.27D),
                        "walk_speed", defaults.extraDouble("walk_speed", 0.225D),
                        "taming_chance_base", SaintsDragonsNeoForgeConfig.CINDERVANE_TAMING_CHANCE_BASE.get(),
                        "taming_chance_hearty", SaintsDragonsNeoForgeConfig.CINDERVANE_TAMING_CHANCE_HEARTY.get()
                ),
                defaults.extraBooleans()  // Keep existing booleans
        );
    }

    private DragonAttributeConfig createRaevyxConfig() {
        DragonAttributeConfig defaults = DragonAttributeConfigLoader.getInstance().getDefaultConfig(DragonAttributeConfigLoader.RAEVYX_ID);
        return new DragonAttributeConfig(
                SaintsDragonsNeoForgeConfig.RAEVYX_MAX_HEALTH.get(),
                SaintsDragonsNeoForgeConfig.RAEVYX_ATTACK_DAMAGE.get(),
                0.0D,  // Movement speed not configurable
                SaintsDragonsNeoForgeConfig.RAEVYX_FLYING_SPEED.get(),
                defaults.abilities(),  // Keep existing abilities
                Map.of(
                        "run_speed", defaults.extraDouble("run_speed", 0.45D),
                        "walk_speed", defaults.extraDouble("walk_speed", 0.25D),
                        "taming_chance_base", SaintsDragonsNeoForgeConfig.RAEVYX_TAMING_CHANCE_BASE.get(),
                        "taming_chance_hearty", SaintsDragonsNeoForgeConfig.RAEVYX_TAMING_CHANCE_HEARTY.get()
                ),
                Map.of(
                        "legacy_taming", SaintsDragonsNeoForgeConfig.RAEVYX_LEGACY_TAMING.get()
                )
        );
    }

    private DragonAttributeConfig createNulljawConfig() {
        DragonAttributeConfig defaults = DragonAttributeConfigLoader.getInstance().getDefaultConfig(DragonAttributeConfigLoader.NULLJAW_ID);
        return new DragonAttributeConfig(
                SaintsDragonsNeoForgeConfig.NULLJAW_MAX_HEALTH.get(),
                SaintsDragonsNeoForgeConfig.NULLJAW_ATTACK_DAMAGE.get(),
                0.0D,  // Movement speed not configurable
                0.0D,  // No flying speed
                defaults.abilities(),  // Keep existing abilities
                Map.of(
                        "run_speed", defaults.extraDouble("run_speed", 0.28D),
                        "walk_speed", defaults.extraDouble("walk_speed", 0.14D),
                        "swim_speed", SaintsDragonsNeoForgeConfig.NULLJAW_SWIM_SPEED.get(),
                        "taming_chance", SaintsDragonsNeoForgeConfig.NULLJAW_TAMING_CHANCE.get()
                ),
                Map.of(
                        "legacy_taming", SaintsDragonsNeoForgeConfig.NULLJAW_LEGACY_TAMING.get()
                )
        );
    }

    private DragonAttributeConfig createIgnivorusConfig() {
        DragonAttributeConfig defaults = DragonAttributeConfigLoader.getInstance().getDefaultConfig(DragonAttributeConfigLoader.IGNIVORUS_ID);
        return new DragonAttributeConfig(
                SaintsDragonsNeoForgeConfig.IGNIVORUS_MAX_HEALTH.get(),
                SaintsDragonsNeoForgeConfig.IGNIVORUS_ATTACK_DAMAGE.get(),
                0.0D,  // Movement speed not configurable
                SaintsDragonsNeoForgeConfig.IGNIVORUS_FLYING_SPEED.get(),
                defaults.abilities(),  // Keep existing abilities
                Map.of(
                        "run_speed", defaults.extraDouble("run_speed", 0.60D),
                        "walk_speed", defaults.extraDouble("walk_speed", 0.225D),
                        "ultimate_penalty_health", defaults.extraDouble("ultimate_penalty_health", 50.0D),
                        "taming_chance_base", SaintsDragonsNeoForgeConfig.IGNIVORUS_TAMING_CHANCE_BASE.get(),
                        "taming_chance_hearty", SaintsDragonsNeoForgeConfig.IGNIVORUS_TAMING_CHANCE_HEARTY.get()
                ),
                Map.of(
                        "legacy_taming", SaintsDragonsNeoForgeConfig.IGNIVORUS_LEGACY_TAMING.get()
                )
        );
    }

    private DragonAttributeConfig createStegonautConfig() {
        DragonAttributeConfig defaults = DragonAttributeConfigLoader.getInstance().getDefaultConfig(DragonAttributeConfigLoader.STEGONAUT_ID);
        return new DragonAttributeConfig(
                SaintsDragonsNeoForgeConfig.STEGONAUT_MAX_HEALTH.get(),
                SaintsDragonsNeoForgeConfig.STEGONAUT_ATTACK_DAMAGE.get(),
                0.0D,  // Movement speed not configurable
                0.0D,  // No flying speed
                defaults.abilities(),  // Keep existing abilities
                Map.of(
                        "run_speed", defaults.extraDouble("run_speed", 0.27D),
                        "walk_speed", defaults.extraDouble("walk_speed", 0.18D),
                        "armor", SaintsDragonsNeoForgeConfig.STEGONAUT_ARMOR.get()
                ),
                defaults.extraBooleans()  // Keep existing booleans
        );
    }
}
