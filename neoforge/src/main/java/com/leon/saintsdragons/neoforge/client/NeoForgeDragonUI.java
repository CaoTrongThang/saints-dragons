package com.leon.saintsdragons.neoforge.client;

import com.leon.saintsdragons.client.DragonStatusUIManager;
import com.leon.saintsdragons.client.ui.DragonStatusUI;
import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.client.ui.FireballChargeIndicator;
import com.leon.saintsdragons.server.entity.dragons.ignivorus.Ignivorus;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * NeoForge-specific wiring for the dragon status UI hotkey and overlay rendering.
 */
@EventBusSubscriber(modid = SaintsDragonsCommon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class NeoForgeDragonUI {
    private static final KeyMapping TOGGLE_DRAGON_UI = new KeyMapping(
            "key.saintsdragons.toggle_dragon_ui",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F4,
            "key.categories.saintsdragons"
    );

    private NeoForgeDragonUI() {
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_DRAGON_UI);
    }

    /**
     * Called from NeoForgeClientGameEvents to handle UI ticking.
     */
    public static void tick(Minecraft client) {
        if (client.player == null) {
            return;
        }

        DragonStatusUIManager manager = DragonStatusUIManager.getInstance();
        manager.update();

        if (client.screen == null) {
            while (TOGGLE_DRAGON_UI.consumeClick()) {
                manager.getDragonStatusUI().toggleVisibility();
            }
        } else {
            // Clear queued clicks so the key isn't processed when returning to game
            TOGGLE_DRAGON_UI.consumeClick();
        }

        manager.getDragonStatusUI().getMeleeModeNotification().tick();
        manager.getDragonStatusUI().getFireballChargeIndicator().tick();
    }

    /**
     * Called from NeoForgeClientEvents to render the UI overlay.
     */
    public static void renderHud(RenderGuiLayerEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }

        DragonStatusUIManager manager = DragonStatusUIManager.getInstance();
        DragonStatusUI ui = manager.getDragonStatusUI();

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(client.isPaused());

        if (ui.isVisible()) {
            ui.render(event.getGuiGraphics(), -1, -1, partialTick);
        }

        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();
        ui.getMeleeModeNotification().render(event.getGuiGraphics(), width, height);

        if (ui.getCurrentDragon() instanceof Ignivorus ignivorus) {
            FireballChargeIndicator chargeIndicator = ui.getFireballChargeIndicator();
            chargeIndicator.setChargeLevel(ignivorus.getFireballChargeLevel());
            chargeIndicator.render(event.getGuiGraphics(), width, height);
        }
    }
}
