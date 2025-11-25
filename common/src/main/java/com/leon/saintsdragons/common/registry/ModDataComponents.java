package com.leon.saintsdragons.common.registry;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.common.component.BinderData;
import com.leon.saintsdragons.platform.DataComponentHelper;
import com.leon.saintsdragons.platform.Services;
import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;

public final class ModDataComponents {
    private static final DataComponentHelper HELPER = Services.PLATFORM.getDataComponentHelper();

    public static final Supplier<DataComponentType<BinderData>> BINDER_DATA =
            register("binder_data", DataComponentType.<BinderData>builder()
                    .persistent(BinderData.CODEC)
                    .networkSynchronized(BinderData.STREAM_CODEC));

    private ModDataComponents() {
    }

    public static void register() {
        // No-op; registration is handled via platform helpers during static init.
    }

    private static <T> Supplier<DataComponentType<T>> register(String name, DataComponentType.Builder<T> builder) {
        return HELPER.register(SaintsDragonsCommon.rl(name), builder);
    }
}
