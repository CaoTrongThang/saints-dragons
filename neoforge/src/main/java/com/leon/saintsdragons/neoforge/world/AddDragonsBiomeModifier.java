package com.leon.saintsdragons.neoforge.world;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.common.config.SaintsDragonsConfig;
import com.leon.saintsdragons.common.util.BiomeConfigHelper;
import com.leon.saintsdragons.common.world.DragonSpawnRegistry;
import com.leon.saintsdragons.platform.ConfigHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.function.Supplier;

/**
 * NeoForge biome modifier that mirrors Fabric's runtime spawn registration.
 * The JSON file only needs to point at this serializer; all spawn weights come from config.
 */
public final class AddDragonsBiomeModifier implements BiomeModifier {
    public static final MapCodec<AddDragonsBiomeModifier> CODEC = MapCodec.unit(AddDragonsBiomeModifier::new);

    private static final TagKey<Biome> HAS_CINDERVANE =
            TagKey.create(Registries.BIOME, SaintsDragonsCommon.rl("has_cindervane"));
    private static final TagKey<Biome> HAS_NULLJAW_EGGS =
            TagKey.create(Registries.BIOME, SaintsDragonsCommon.rl("has_nulljaw_eggs"));
    private static final ResourceKey<PlacedFeature> CINDERVANE_EGG_PATCH =
            ResourceKey.create(Registries.PLACED_FEATURE, SaintsDragonsCommon.rl("cindervane_egg_patch"));
    private static final ResourceKey<PlacedFeature> NULLJAW_EGG_PATCH =
            ResourceKey.create(Registries.PLACED_FEATURE, SaintsDragonsCommon.rl("nulljaw_egg_patch"));

    private AddDragonsBiomeModifier() {
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) {
            return;
        }

        try {
            for (DragonSpawnRegistry.DragonSpawnEntry entry : DragonSpawnRegistry.getAll()) {
                int weight = entry.weight().getAsInt();
                int minGroupSize = entry.minGroupSize().getAsInt();
                int maxGroupSize = entry.maxGroupSize().getAsInt();
                ConfigHelper.ListValue additionalBiomes = resolveConfigList(entry.additionalBiomes());
                ConfigHelper.ListValue excludedBiomes = resolveConfigList(entry.excludedBiomes());

                if (weight <= 0) {
                    continue;
                }

                if (shouldSpawnInBiome(biome, entry.biomeTag(), additionalBiomes, excludedBiomes)) {
                    addSpawn(
                            builder,
                            entry.category(),
                            entry.entityType().get(),
                            weight,
                            minGroupSize,
                            maxGroupSize
                    );
                }
            }

            if (SaintsDragonsConfig.CINDERVANE_EGG_BLOCK_WORLDGEN.get()
                    && shouldSpawnInBiome(
                    biome,
                    HAS_CINDERVANE,
                    SaintsDragonsConfig.CINDERVANE_ADDITIONAL_BIOMES,
                    SaintsDragonsConfig.CINDERVANE_EXCLUDED_BIOMES
            )) {
                addFeature(builder, CINDERVANE_EGG_PATCH);
            }

            if (SaintsDragonsConfig.NULLJAW_EGG_BLOCK_WORLDGEN.get() && biome.is(HAS_NULLJAW_EGGS)) {
                addFeature(builder, NULLJAW_EGG_PATCH);
            }
        } catch (IllegalStateException e) {
            // Config not loaded yet during datagen or early worldgen, skip spawn modification
        }
    }

    private static boolean isInConfigBiomes(Holder<Biome> biome, ConfigHelper.ListValue configList) {
        if (configList == null) {
            return false;
        }
        try {
            ResourceLocation biomeId = biome.unwrapKey()
                    .map(net.minecraft.resources.ResourceKey::location)
                    .orElse(null);
            if (biomeId == null) {
                return false;
            }
            return configList.get().stream()
                    .map(BiomeConfigHelper::normalizeBiomeId)
                    .anyMatch(id -> id != null && biomeId.equals(id));
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isInConfigBiomeTags(Holder<Biome> biome, ConfigHelper.ListValue configList) {
        if (configList == null) {
            return false;
        }
        try {
            return configList.get().stream()
                    .map(BiomeConfigHelper::normalizeBiomeTag)
                    .anyMatch(tag -> tag != null && biome.is(tag));
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean shouldSpawnInBiome(Holder<Biome> biome,
                                              TagKey<Biome> defaultTag,
                                              ConfigHelper.ListValue additionalBiomes,
                                              ConfigHelper.ListValue excludedBiomes) {
        boolean explicitlyIncluded = isInConfigBiomes(biome, additionalBiomes)
                || isInConfigBiomeTags(biome, additionalBiomes);
        boolean explicitlyExcluded = isInConfigBiomes(biome, excludedBiomes)
                || isInConfigBiomeTags(biome, excludedBiomes);
        boolean defaultAllowed = biome.is(defaultTag) && !explicitlyExcluded;
        return explicitlyIncluded || defaultAllowed;
    }

    private static void addSpawn(ModifiableBiomeInfo.BiomeInfo.Builder builder,
                                 MobCategory category,
                                 EntityType<?> entityType,
                                 int weight,
                                 int minGroupSize,
                                 int maxGroupSize) {
        if (weight <= 0 || minGroupSize <= 0 || maxGroupSize <= 0) {
            return;
        }
        if (minGroupSize > maxGroupSize) {
            minGroupSize = maxGroupSize;
        }

        @SuppressWarnings("unchecked")
        EntityType<? extends Mob> mobType = (EntityType<? extends Mob>) entityType;
        MobSpawnSettings.SpawnerData spawnerData = new MobSpawnSettings.SpawnerData(mobType, weight, minGroupSize, maxGroupSize);

        var spawnSettings = builder.getMobSpawnSettings();
        boolean alreadyPresent = spawnSettings.getSpawner(category).stream()
                .anyMatch(existing -> existing.type == entityType);

        if (!alreadyPresent) {
            spawnSettings.addSpawn(category, spawnerData);
        }
    }

    private static void addFeature(ModifiableBiomeInfo.BiomeInfo.Builder builder,
                                   ResourceKey<PlacedFeature> featureKey) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        RegistryAccess registryAccess = server.registryAccess();
        registryAccess.registry(Registries.PLACED_FEATURE)
                .flatMap(registry -> registry.getHolder(featureKey))
                .ifPresent(feature ->
                        builder.getGenerationSettings()
                                .getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION)
                                .add(feature));
    }

    private static ConfigHelper.ListValue resolveConfigList(Supplier<ConfigHelper.ListValue> supplier) {
        if (supplier == null) {
            return null;
        }
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
