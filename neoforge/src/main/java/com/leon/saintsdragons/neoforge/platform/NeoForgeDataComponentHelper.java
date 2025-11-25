package com.leon.saintsdragons.neoforge.platform;

import com.leon.saintsdragons.platform.DataComponentHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class NeoForgeDataComponentHelper implements DataComponentHelper {
    private final DeferredRegister<DataComponentType<?>> deferred =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, com.leon.saintsdragons.common.SaintsDragonsCommon.MOD_ID);

    public NeoForgeDataComponentHelper() {
        // Register deferred data components onto the mod event bus once.
        com.leon.saintsdragons.neoforge.NeoForgeModContext.getModEventBus().register(deferred);
    }

    @Override
    public <T> Supplier<DataComponentType<T>> register(ResourceLocation id, DataComponentType.Builder<T> builder) {
        return deferred.register(id.getPath(), () -> builder.build());
    }
}
