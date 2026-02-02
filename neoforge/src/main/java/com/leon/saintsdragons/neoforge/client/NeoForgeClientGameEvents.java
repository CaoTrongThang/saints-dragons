package com.leon.saintsdragons.neoforge.client;

import com.leon.saintsdragons.client.input.DragonRideInputHandler;
import com.leon.saintsdragons.client.ui.DragonUIRegistry;
import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.neoforge.client.event.NeoForgeClientEventHandler;
import com.leon.saintsdragons.client.camera.DragonFovHelper;
import com.leon.saintsdragons.server.entity.base.DragonEntity;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.world.entity.Entity;

@EventBusSubscriber(modid = SaintsDragonsCommon.MOD_ID, value = Dist.CLIENT)
public final class NeoForgeClientGameEvents {
    private NeoForgeClientGameEvents() {}

    // Smooth FOV transition state
    private static double currentFOVMultiplier = 1.0;
    private static final double FOV_TRANSITION_SPEED = 0.05; // How fast FOV changes

    @SubscribeEvent
    public static void onClientTick(PlayerTickEvent.Post event) {
        // Handle dragon ride input on client tick
        if (event.getEntity().level().isClientSide()) {
            DragonRideInputHandler.clientTick();
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        // Drive client-only sound controllers each tick
        NeoForgeClientEventHandler.onClientTick(mc);
        // Handle dragon UI toggle and updates
        NeoForgeDragonUI.tick(mc);
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() != null) {
            Entity vehicle = mc.player.getVehicle();
            if (!DragonFovHelper.shouldApply(vehicle)) {
                currentFOVMultiplier = 1.0;
                return;
            }

            double targetFOVMultiplier = DragonFovHelper.getTargetMultiplier(vehicle);

            // Smooth interpolation between current and target FOV multiplier
            double diff = targetFOVMultiplier - currentFOVMultiplier;
            if (Math.abs(diff) > 0.001) {
                currentFOVMultiplier += diff * FOV_TRANSITION_SPEED;
            } else {
                currentFOVMultiplier = targetFOVMultiplier;
            }

            // Apply the smoothly interpolated FOV multiplier
            event.setFOV(event.getFOV() * currentFOVMultiplier);
        } else {
            currentFOVMultiplier = 1.0;
        }
    }

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        // Render dragon status UI overlay
        NeoForgeDragonUI.renderHud(event);
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        // Only hide vanilla HUD if riding a dragon AND dragon UI is visible (F4 toggle)
        // When dragon UI is hidden, show vanilla HUD instead
        if (!(minecraft.player.getVehicle() instanceof DragonEntity) || !DragonUIRegistry.isUIVisible()) {
            return;
        }

        if (event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)
                || event.getName().equals(VanillaGuiLayers.ARMOR_LEVEL)
                || event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)
                || event.getName().equals(VanillaGuiLayers.VEHICLE_HEALTH)
                || event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)
                || event.getName().equals(VanillaGuiLayers.AIR_LEVEL)) {
            event.setCanceled(true);
        }
    }
}
