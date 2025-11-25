package com.leon.saintsdragons.neoforge.platform;

import com.leon.saintsdragons.platform.ConfigHelper;
import com.leon.saintsdragons.platform.DataComponentHelper;
import com.leon.saintsdragons.platform.NetworkHelper;
import com.leon.saintsdragons.platform.PlatformHelper;
import com.leon.saintsdragons.platform.RegistryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;

import java.nio.file.Path;
import java.util.function.Supplier;

public final class NeoForgePlatformHelper implements PlatformHelper {
    private NeoForgeRegistryHelper registryHelper;
    private NeoForgeNetworkHelper networkHelper;
    private NeoForgeConfigHelper configHelper;
    private NeoForgeDataComponentHelper dataComponentHelper;

    @Override
    public RegistryHelper getRegistryHelper() {
        if (registryHelper == null) {
            registryHelper = new NeoForgeRegistryHelper();
        }
        return registryHelper;
    }

    @Override
    public NetworkHelper getNetworkHelper() {
        if (networkHelper == null) {
            networkHelper = new NeoForgeNetworkHelper();
        }
        return networkHelper;
    }

    @Override
    public ConfigHelper getConfigHelper() {
        if (configHelper == null) {
            configHelper = new NeoForgeConfigHelper();
        }
        return configHelper;
    }

    @Override
    public DataComponentHelper getDataComponentHelper() {
        if (dataComponentHelper == null) {
            dataComponentHelper = new NeoForgeDataComponentHelper();
        }
        return dataComponentHelper;
    }

    @Override
    public void runOnClient(Runnable runnable) {
        if (FMLEnvironment.dist.isClient()) {
            runnable.run();
        }
    }

    @Override
    public <T> T callOnClient(Supplier<T> supplier) {
        if (FMLEnvironment.dist.isClient()) {
            return supplier.get();
        }
        return null;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }

    @Override
    public Item createSpawnEgg(Supplier<? extends EntityType<? extends Mob>> entityType,
                               int primaryColor,
                               int secondaryColor,
                               Item.Properties properties) {
        return new SpawnEggItem(entityType.get(), primaryColor, secondaryColor, properties);
    }

    @Override
    public net.minecraft.core.particles.SimpleParticleType createSimpleParticle(boolean overrideLimiter) {
        return new SimpleParticleTypeImpl(overrideLimiter);
    }

    private static final class SimpleParticleTypeImpl extends net.minecraft.core.particles.SimpleParticleType {
        private SimpleParticleTypeImpl(boolean overrideLimiter) {
            super(overrideLimiter);
        }
    }

    @Override
    public Path getConfigDirectory() {
        return FMLLoader.getGamePath().resolve("config");
    }
}
