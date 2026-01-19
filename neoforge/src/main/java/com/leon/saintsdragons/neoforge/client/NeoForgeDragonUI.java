package com.leon.saintsdragons.neoforge.client;

import com.leon.saintsdragons.client.ui.DragonRideHealthBar;
import com.leon.saintsdragons.client.ui.DragonUIRegistry;
import com.leon.saintsdragons.client.ui.FireballChargeIndicator;
import com.leon.saintsdragons.client.ui.IgnivorusFireBreathMeterIndicator;
import com.leon.saintsdragons.client.ui.MeleeModeNotification;
import com.leon.saintsdragons.client.ui.RaevyxBeamMeterIndicator;
import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.server.entity.base.DragonEntity;
import com.leon.saintsdragons.server.entity.dragons.ignivorus.Ignivorus;
import com.leon.saintsdragons.server.entity.dragons.raevyx.Raevyx;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

/**
 * NeoForge-specific wiring for the dragon UI hotkey and overlay rendering.
 */
@EventBusSubscriber(modid = SaintsDragonsCommon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class NeoForgeDragonUI {
    private static final MeleeModeNotification meleeModeNotification = new MeleeModeNotification();
    private static final FireballChargeIndicator fireballChargeIndicator = new FireballChargeIndicator();
    private static final RaevyxBeamMeterIndicator raevyxBeamMeterIndicator = new RaevyxBeamMeterIndicator();
    private static final IgnivorusFireBreathMeterIndicator ignivorusFireBreathMeterIndicator = new IgnivorusFireBreathMeterIndicator();
    private static final DragonRideHealthBar rideHealthBar = new DragonRideHealthBar();

    private static final KeyMapping TOGGLE_DRAGON_UI = new KeyMapping(
            "key.saintsdragons.toggle_dragon_ui",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F4,
            "key.categories.saintsdragons"
    );

    static {
        // Initialize the UI registry so other classes can access the melee mode notification
        DragonUIRegistry.init(meleeModeNotification);
    }

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

        if (client.screen == null) {
            while (TOGGLE_DRAGON_UI.consumeClick()) {
                DragonUIRegistry.toggleUIVisibility();
            }
        } else {
            // Clear queued clicks so the key isn't processed when returning to game
            TOGGLE_DRAGON_UI.consumeClick();
        }

        // Tick all UI elements for smooth animations
        meleeModeNotification.tick();
        fireballChargeIndicator.tick();
        raevyxBeamMeterIndicator.tick();
        ignivorusFireBreathMeterIndicator.tick();
    }

    /**
     * Called from NeoForgeClientGameEvents to render the UI overlay.
     */
    public static void renderHud(RenderGuiLayerEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }

        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(client.isPaused());

        // Get current dragon if riding
        DragonEntity currentDragon = null;
        if (client.player.getVehicle() instanceof DragonEntity dragon) {
            currentDragon = dragon;
            rideHealthBar.setDragon(dragon);
        }

        // Always render melee mode notification (independent of UI visibility toggle)
        meleeModeNotification.render(event.getGuiGraphics(), width, height);

        // Only render dragon UI elements if UI is visible
        if (!DragonUIRegistry.isUIVisible()) {
            return;
        }

        // Render dragon-specific UI when riding
        if (currentDragon instanceof Ignivorus ignivorus) {
            // Fireball charge indicator
            fireballChargeIndicator.setChargeLevel(ignivorus.getFireballChargeLevel());
            fireballChargeIndicator.render(event.getGuiGraphics(), width, height, partialTick);

            // Fire breath meter
            ignivorusFireBreathMeterIndicator.setBreathEnergy(ignivorus.getFireBreathEnergy());
            ignivorusFireBreathMeterIndicator.setBreathing(ignivorus.isBreathingFire());
            ignivorusFireBreathMeterIndicator.render(event.getGuiGraphics(), width, height, partialTick);
        } else if (currentDragon instanceof Raevyx raevyx) {
            // Beam meter for Raevyx
            raevyxBeamMeterIndicator.setBeamEnergy(raevyx.getBeamEnergy());
            raevyxBeamMeterIndicator.setBeaming(raevyx.isBeaming());
            raevyxBeamMeterIndicator.render(event.getGuiGraphics(), width, height, partialTick);
        }

        // Render dragon ride health bar when riding any dragon
        if (currentDragon != null) {
            rideHealthBar.render(event.getGuiGraphics(), width, height, partialTick);
        }
    }
}
