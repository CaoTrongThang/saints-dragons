package com.leon.saintsdragons.neoforge.platform;

import com.leon.saintsdragons.platform.ConfigHelper;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * NeoForge config helper - builds spawn configuration using ModConfigSpec.
 * The spec is built here but registered in SaintsDragonsNeoForge.
 */
public final class NeoForgeConfigHelper implements ConfigHelper {
    public static ModConfigSpec SPAWN_SPEC;

    @Override
    public ConfigBuilder commonBuilder(String fileName) {
        return new NeoForgeBuilder();
    }

    private static final class NeoForgeBuilder implements ConfigBuilder {
        private final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        @Override
        public void push(String category) {
            builder.push(category);
        }

        @Override
        public void pop() {
            builder.pop();
        }

        @Override
        public void comment(String comment) {
            builder.comment(comment);
        }

        @Override
        public void translation(String translationKey) {
            builder.translation(translationKey);
        }

        @Override
        public IntValue defineInt(String key, int defaultValue, int min, int max) {
            ModConfigSpec.IntValue value = builder.defineInRange(key, defaultValue, min, max);
            return value::get;
        }

        @Override
        public ListValue defineList(String key, List<String> defaultValue) {
            ModConfigSpec.ConfigValue<List<? extends String>> value = builder.defineListAllowEmpty(
                    key,
                    defaultValue,
                    () -> "",  // Default value when adding new entry - empty string for player to type
                    obj -> obj instanceof String
            );
            return () -> (List<String>) value.get();
        }

        @Override
        public void build() {
            SPAWN_SPEC = builder.build();
        }
    }
}
