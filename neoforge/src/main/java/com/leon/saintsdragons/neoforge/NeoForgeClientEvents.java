package com.leon.saintsdragons.neoforge;

import com.leon.saintsdragons.client.ClientProxy;
import com.leon.saintsdragons.client.init.CommonClientModEvents;
import com.leon.saintsdragons.client.particle.cindervane.FireBreathFlameParticle;
import com.leon.saintsdragons.client.particle.cindervane.FireBreathSmokeParticle;
import com.leon.saintsdragons.client.particle.raevyx.RaevyxLightningChainParticle;
import com.leon.saintsdragons.client.particle.raevyx.RaevyxLightningParticle;
import com.leon.saintsdragons.client.ui.StegonautInventoryScreen;
import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.common.registry.ModMenus;
import com.leon.saintsdragons.common.registry.ModParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = SaintsDragonsCommon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class NeoForgeClientEvents {
    private NeoForgeClientEvents() {}

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        CommonClientModEvents.registerEntityRenderers(event::registerEntityRenderer);
    }

    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.LIGHTNING_STORM.get(), RaevyxLightningParticle.Factory::new);
        event.registerSpriteSet(ModParticles.LIGHTNING_CHAIN.get(), RaevyxLightningChainParticle.Factory::new);
        event.registerSpriteSet(ModParticles.FIRE_BREATH_FLAME.get(), FireBreathFlameParticle.Factory::new);
        event.registerSpriteSet(ModParticles.FIRE_BREATH_SMOKE.get(), FireBreathSmokeParticle.Factory::new);
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.STEGONAUT_INVENTORY.get(), StegonautInventoryScreen::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            new ClientProxy().clientInit();
        });
    }
}
