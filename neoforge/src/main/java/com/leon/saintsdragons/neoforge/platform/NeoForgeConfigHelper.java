package com.leon.saintsdragons.neoforge.platform;

import com.leon.saintsdragons.platform.ConfigHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal config helper; extend with real persistence if needed.
 */
public final class NeoForgeConfigHelper implements ConfigHelper {
    @Override
    public ConfigBuilder commonBuilder(String fileName) {
        return new DefaultBuilder();
    }

    private static final class DefaultBuilder implements ConfigBuilder {
        @Override
        public void push(String category) {
            // No-op
        }

        @Override
        public void pop() {
            // No-op
        }

        @Override
        public void comment(String comment) {
            // No-op
        }

        @Override
        public IntValue defineInt(String key, int defaultValue, int min, int max) {
            int clamped = Math.max(min, Math.min(max, defaultValue));
            return () -> clamped;
        }

        @Override
        public ListValue defineList(String key, List<String> defaultValue) {
            return () -> new ArrayList<>(defaultValue);
        }

        @Override
        public void build() {
            // No persistence in this minimal implementation
        }
    }
}
