package com.leon.saintsdragons.neoforge.platform;

import com.leon.saintsdragons.neoforge.NeoForgeModContext;
import com.leon.saintsdragons.platform.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class NeoForgeRegistryHelper implements RegistryHelper {
    @Override
    public <T> RegistryWrapper<T> create(ResourceKey<? extends Registry<T>> registryKey,
                                         Supplier<Registry<T>> backingRegistry,
                                         String modId) {
        return new Wrapper<>(registryKey, modId);
    }

    private static final class Wrapper<T> implements RegistryWrapper<T> {
        private final DeferredRegister<T> deferredRegister;

        private Wrapper(ResourceKey<? extends Registry<T>> registryKey, String modId) {
            this.deferredRegister = DeferredRegister.create(registryKey, modId);
        }

        @Override
        public <I extends T> Supplier<I> register(String name, Supplier<I> supplier) {
            return deferredRegister.register(name, supplier);
        }

        @Override
        public void register() {
            deferredRegister.register(NeoForgeModContext.getModEventBus());
        }
    }
}
