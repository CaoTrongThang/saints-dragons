package com.leon.saintsdragons.fabric;

import com.leon.saintsdragons.client.ClientProxy;
import com.leon.saintsdragons.client.init.CommonClientModEvents;
import com.leon.saintsdragons.client.ui.StegonautInventoryScreen;
import com.leon.saintsdragons.common.registry.ModMenus;
import com.leon.saintsdragons.fabric.client.ClientPartCleanupHandler;
import com.leon.saintsdragons.fabric.client.FabricDragonRideKeybinds;
import com.leon.saintsdragons.fabric.client.FabricDragonUI;
import com.leon.saintsdragons.fabric.client.event.FabricClientEventHandler;
import com.leon.saintsdragons.fabric.client.particle.FabricParticleRegistry;
import com.leon.saintsdragons.fabric.client.renderer.FabricDragonPartRenderer;
import com.leon.saintsdragons.fabric.entity.part.FabricPartEntities;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public final class SaintsDragonsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CommonClientModEvents.registerEntityRenderers(EntityRendererRegistry::register);
        MenuRegistry.registerScreenFactory(ModMenus.STEGONAUT_INVENTORY.get(), StegonautInventoryScreen::new);
        EntityRendererRegistry.register(FabricPartEntities.DRAGON_PART, FabricDragonPartRenderer::new);
        FabricParticleRegistry.registerParticleFactories();
        FabricDragonRideKeybinds.init();
        FabricDragonUI.init();
        FabricClientEventHandler.init();
        ClientPartCleanupHandler.register();
        new ClientProxy().clientInit();
    }
}
