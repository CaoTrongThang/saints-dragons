package com.leon.saintsdragons.fabric.platform;

import com.leon.saintsdragons.platform.DataComponentHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public final class FabricDataComponentHelper implements DataComponentHelper {
    @Override
    public <T> Supplier<DataComponentType<T>> register(ResourceLocation id, DataComponentType.Builder<T> builder) {
        DataComponentType<T> type = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, builder.build());
        return () -> type;
    }
}
