package com.leon.saintsdragons.neoforge.platform;

import com.leon.saintsdragons.platform.DataComponentHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class NeoForgeDataComponentHelper implements DataComponentHelper {
    @Override
    public <T> Supplier<DataComponentType<T>> register(ResourceLocation id, DataComponentType.Builder<T> builder) {
        DataComponentType<T> type = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, builder.build());
        return () -> type;
    }
}
