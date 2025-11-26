package com.leon.saintsdragons.neoforge.platform;

import com.leon.saintsdragons.platform.ConfigHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * NeoForge config helper - minimal implementation since we're using direct ModConfigSpec access.
 * Spawn configuration is handled via JSON biome modifiers, not the config system.
 */
public final class NeoForgeConfigHelper implements ConfigHelper {
    @Override
    public ConfigBuilder commonBuilder(String fileName) {
        return new NeoForgeBuilder();
    }

    private static final class NeoForgeBuilder implements ConfigBuilder {
        @Override
        public void push(String category) {
            // No-op - not used
        }

        @Override
        public void pop() {
            // No-op - not used
        }

        @Override
        public void comment(String comment) {
            // No-op - not used
        }

        @Override
        public IntValue defineInt(String key, int defaultValue, int min, int max) {
            // Return default values - spawn config is not used for NeoForge
            return () -> defaultValue;
        }

        @Override
        public ListValue defineList(String key, List<String> defaultValue) {
            // Return default values - spawn config is not used for NeoForge
            return () -> new ArrayList<>(defaultValue);
        }

        @Override
        public void build() {
            // No-op - not used
        }
    }
}
