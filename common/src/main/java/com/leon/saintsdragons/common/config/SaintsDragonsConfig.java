package com.leon.saintsdragons.common.config;

import com.leon.saintsdragons.platform.ConfigHelper;
import com.leon.saintsdragons.platform.Services;

import java.util.Collections;

public final class SaintsDragonsConfig {

    public static ConfigHelper.IntValue RAEVYX_SPAWN_WEIGHT;
    public static ConfigHelper.IntValue RAEVYX_MIN_GROUP_SIZE;
    public static ConfigHelper.IntValue RAEVYX_MAX_GROUP_SIZE;
    public static ConfigHelper.ListValue RAEVYX_ADDITIONAL_BIOMES;

    public static ConfigHelper.IntValue STEGONAUT_SPAWN_WEIGHT;
    public static ConfigHelper.IntValue STEGONAUT_MIN_GROUP_SIZE;
    public static ConfigHelper.IntValue STEGONAUT_MAX_GROUP_SIZE;
    public static ConfigHelper.ListValue STEGONAUT_ADDITIONAL_BIOMES;

    public static ConfigHelper.IntValue CINDERVANE_SPAWN_WEIGHT;
    public static ConfigHelper.IntValue CINDERVANE_MIN_GROUP_SIZE;
    public static ConfigHelper.IntValue CINDERVANE_MAX_GROUP_SIZE;
    public static ConfigHelper.ListValue CINDERVANE_ADDITIONAL_BIOMES;

    public static ConfigHelper.IntValue NULLJAW_SPAWN_WEIGHT;
    public static ConfigHelper.IntValue NULLJAW_MIN_GROUP_SIZE;
    public static ConfigHelper.IntValue NULLJAW_MAX_GROUP_SIZE;
    public static ConfigHelper.ListValue NULLJAW_ADDITIONAL_BIOMES;

    public static ConfigHelper.IntValue IGNIVORUS_SPAWN_WEIGHT;
    public static ConfigHelper.IntValue IGNIVORUS_MIN_GROUP_SIZE;
    public static ConfigHelper.IntValue IGNIVORUS_MAX_GROUP_SIZE;
    public static ConfigHelper.ListValue IGNIVORUS_ADDITIONAL_BIOMES;

    private static volatile boolean initialized = false;

    public static void bootstrap() {
        // Double-checked locking to ensure safe initialization and prevent race conditions
        if (!initialized) {
            synchronized (SaintsDragonsConfig.class) {
                if (!initialized) {
                    initializeConfig();
                    initialized = true;
                }
            }
        }
    }

    private static void initializeConfig() {
        ConfigHelper.ConfigBuilder builder = Services.PLATFORM.getConfigHelper()
                .commonBuilder("saintsdragonsspawning.toml");

        builder.push("spawning");
        builder.comment("Dragon spawn configuration - control where and how often dragons spawn");

        builder.comment("Raevyx spawn settings");
        builder.translation("saintsdragons.config.raevyxSpawnWeight");
        RAEVYX_SPAWN_WEIGHT = builder.defineInt("raevyxSpawnWeight", 1, 0, 100);
        builder.translation("saintsdragons.config.raevyxMinGroupSize");
        RAEVYX_MIN_GROUP_SIZE = builder.defineInt("raevyxMinGroupSize", 1, 1, 10);
        builder.translation("saintsdragons.config.raevyxMaxGroupSize");
        RAEVYX_MAX_GROUP_SIZE = builder.defineInt("raevyxMaxGroupSize", 2, 1, 10);
        builder.comment("Additional biomes where Raevyx can spawn (e.g., \"minecraft:desert\", \"terralith:volcanic_crater\")");
        builder.translation("saintsdragons.config.raevyxAdditionalBiomes");
        RAEVYX_ADDITIONAL_BIOMES = builder.defineList("raevyxAdditionalBiomes", Collections.emptyList());

        builder.comment("Stegonaut spawn settings");
        builder.translation("saintsdragons.config.stegonautSpawnWeight");
        STEGONAUT_SPAWN_WEIGHT = builder.defineInt("stegonautSpawnWeight", 5, 0, 100);
        builder.translation("saintsdragons.config.stegonautMinGroupSize");
        STEGONAUT_MIN_GROUP_SIZE = builder.defineInt("stegonautMinGroupSize", 1, 1, 10);
        builder.translation("saintsdragons.config.stegonautMaxGroupSize");
        STEGONAUT_MAX_GROUP_SIZE = builder.defineInt("stegonautMaxGroupSize", 4, 1, 10);
        builder.comment("Additional biomes where Stegonaut can spawn (e.g., \"minecraft:desert\", \"terralith:volcanic_crater\")");
        builder.translation("saintsdragons.config.stegonautAdditionalBiomes");
        STEGONAUT_ADDITIONAL_BIOMES = builder.defineList("stegonautAdditionalBiomes", Collections.emptyList());

        builder.comment("Cindervane spawn settings");
        builder.translation("saintsdragons.config.cindervaneSpawnWeight");
        CINDERVANE_SPAWN_WEIGHT = builder.defineInt("cindervaneSpawnWeight", 3, 0, 100);
        builder.translation("saintsdragons.config.cindervaneMinGroupSize");
        CINDERVANE_MIN_GROUP_SIZE = builder.defineInt("cindervaneMinGroupSize", 1, 1, 10);
        builder.translation("saintsdragons.config.cindervaneMaxGroupSize");
        CINDERVANE_MAX_GROUP_SIZE = builder.defineInt("cindervaneMaxGroupSize", 3, 1, 10);
        builder.comment("Additional biomes where Cindervane can spawn (e.g., \"minecraft:desert\", \"terralith:volcanic_crater\")");
        builder.translation("saintsdragons.config.cindervaneAdditionalBiomes");
        CINDERVANE_ADDITIONAL_BIOMES = builder.defineList("cindervaneAdditionalBiomes", Collections.emptyList());

        builder.comment("Nulljaw spawn settings");
        builder.translation("saintsdragons.config.nulljawSpawnWeight");
        NULLJAW_SPAWN_WEIGHT = builder.defineInt("nulljawSpawnWeight", 2, 0, 100);
        builder.translation("saintsdragons.config.nulljawMinGroupSize");
        NULLJAW_MIN_GROUP_SIZE = builder.defineInt("nulljawMinGroupSize", 1, 1, 10);
        builder.translation("saintsdragons.config.nulljawMaxGroupSize");
        NULLJAW_MAX_GROUP_SIZE = builder.defineInt("nulljawMaxGroupSize", 2, 1, 10);
        builder.comment("Additional biomes where Nulljaw can spawn (e.g., \"minecraft:desert\", \"terralith:volcanic_crater\")");
        builder.translation("saintsdragons.config.nulljawAdditionalBiomes");
        NULLJAW_ADDITIONAL_BIOMES = builder.defineList("nulljawAdditionalBiomes", Collections.emptyList());

        builder.comment("Ignivorus spawn settings");
        builder.translation("saintsdragons.config.ignivorusSpawnWeight");
        IGNIVORUS_SPAWN_WEIGHT = builder.defineInt("ignivorusSpawnWeight", 1, 0, 100);
        builder.translation("saintsdragons.config.ignivorusMinGroupSize");
        IGNIVORUS_MIN_GROUP_SIZE = builder.defineInt("ignivorusMinGroupSize", 1, 1, 10);
        builder.translation("saintsdragons.config.ignivorusMaxGroupSize");
        IGNIVORUS_MAX_GROUP_SIZE = builder.defineInt("ignivorusMaxGroupSize", 2, 1, 10);
        builder.comment("Additional biomes where Ignivorus can spawn (e.g., \"minecraft:desert\", \"terralith:volcanic_crater\")");
        builder.translation("saintsdragons.config.ignivorusAdditionalBiomes");
        IGNIVORUS_ADDITIONAL_BIOMES = builder.defineList("ignivorusAdditionalBiomes", Collections.emptyList());

        builder.pop();
        builder.build();
    }

    private SaintsDragonsConfig() {
    }
}
