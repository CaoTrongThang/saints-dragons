package com.leon.saintsdragons.neoforge.world;

import com.leon.saintsdragons.common.config.SaintsDragonsConfig;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/**
 * Forge biome modifier that conditionally adds configured features based on runtime spawning config.
 */
public final class AddConditionalFeaturesBiomeModifier implements BiomeModifier {
    public static final MapCodec<AddConditionalFeaturesBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(m -> m.biomes),
                    PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(m -> m.features),
                    GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(m -> m.step),
                    net.minecraft.util.StringRepresentable.fromEnum(Key::values).fieldOf("config_key").forGetter(m -> m.configKey)
            ).apply(instance, AddConditionalFeaturesBiomeModifier::new)
    );

    private final HolderSet<Biome> biomes;
    private final HolderSet<PlacedFeature> features;
    private final GenerationStep.Decoration step;
    private final Key configKey;

    public AddConditionalFeaturesBiomeModifier(HolderSet<Biome> biomes,
                                               HolderSet<PlacedFeature> features,
                                               GenerationStep.Decoration step,
                                               Key configKey) {
        this.biomes = biomes;
        this.features = features;
        this.step = step;
        this.configKey = configKey;
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.MODIFY || !isEnabled(configKey) || !biomes.contains(biome)) {
            return;
        }
        var generation = builder.getGenerationSettings();
        for (Holder<PlacedFeature> feature : features) {
            generation.addFeature(step, feature);
        }
    }

    private static boolean isEnabled(Key configKey) {
        return switch (configKey) {
            case CINDERVANE_EGG_BLOCK_WORLDGEN -> SaintsDragonsConfig.CINDERVANE_EGG_BLOCK_WORLDGEN.get();
            case NULLJAW_EGG_BLOCK_WORLDGEN -> SaintsDragonsConfig.NULLJAW_EGG_BLOCK_WORLDGEN.get();
        };
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }

    public enum Key implements net.minecraft.util.StringRepresentable {
        CINDERVANE_EGG_BLOCK_WORLDGEN("cindervaneEggBlockWorldgen"),
        NULLJAW_EGG_BLOCK_WORLDGEN("nulljawEggBlockWorldgen");

        private final String id;

        Key(String id) {
            this.id = id;
        }

        @Override
        public String getSerializedName() {
            return id;
        }
    }
}
