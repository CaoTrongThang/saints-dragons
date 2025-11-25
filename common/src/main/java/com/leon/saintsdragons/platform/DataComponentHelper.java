package com.leon.saintsdragons.platform;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Abstraction for registering data components on each loader.
 * Currently minimal; extended components can be added as needed.
 */
public interface DataComponentHelper {
    <T> Supplier<DataComponentType<T>> register(ResourceLocation id, DataComponentType.Builder<T> builder);
}
