package com.leon.saintsdragons.neoforge.platform;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.neoforge.NeoForgeModContext;
import com.leon.saintsdragons.platform.DataComponentHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class NeoForgeDataComponentHelper implements DataComponentHelper {
    private final DeferredRegister<DataComponentType<?>> deferred =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, SaintsDragonsCommon.MOD_ID);

    public NeoForgeDataComponentHelper() {
        // Attach the deferred register to the mod event bus.
        deferred.register(NeoForgeModContext.getModEventBus());
    }

    @Override
    public <T> Supplier<DataComponentType<T>> register(ResourceLocation id, DataComponentType.Builder<T> builder) {
        return deferred.register(id.getPath(), () -> builder.build());
    }
}
