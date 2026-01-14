package com.leon.saintsdragons.neoforge.client;

import com.leon.saintsdragons.client.DragonStatusUIManager;
import com.leon.saintsdragons.client.input.DragonRideInputHandler;
import com.leon.saintsdragons.client.ui.DragonStatusUI;
import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.neoforge.client.event.NeoForgeClientEventHandler;
import com.leon.saintsdragons.server.entity.dragons.cindervane.Cindervane;
import com.leon.saintsdragons.server.entity.dragons.ignivorus.Ignivorus;
import com.leon.saintsdragons.server.entity.dragons.nulljaw.Nulljaw;
import com.leon.saintsdragons.server.entity.dragons.raevyx.Raevyx;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

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
        double targetFOVMultiplier = 1.0;

        if (mc.player != null && mc.player.getVehicle() != null) {
            boolean isAccelerating = false;
            boolean isFlying = false;
            double currentSpeed = 0;
            double maxSpeed = 0;
            Ignivorus ignivorusVehicle = mc.player.getVehicle() instanceof Ignivorus iv ? iv : null;

            if (mc.player.getVehicle() instanceof Raevyx raevyx) {
                isAccelerating = raevyx.isAccelerating();
                isFlying = raevyx.isFlying();

                if (isAccelerating) {
                    if (isFlying) {
                        currentSpeed = raevyx.getDeltaMovement().horizontalDistance();
                        maxSpeed = raevyx.getAttributeValue(Attributes.FLYING_SPEED) * 50.0;
                    } else {
                        currentSpeed = raevyx.getDeltaMovement().horizontalDistance();
                        maxSpeed = raevyx.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.7;
                    }
                }
            } else if (mc.player.getVehicle() instanceof Cindervane cindervane) {
                isAccelerating = cindervane.isAccelerating();
                isFlying = cindervane.isFlying();

                if (isAccelerating) {
                    if (isFlying) {
                        currentSpeed = cindervane.getDeltaMovement().horizontalDistance();
                        maxSpeed = cindervane.getAttributeValue(Attributes.FLYING_SPEED) * 20.0;
                    } else {
                        currentSpeed = cindervane.getDeltaMovement().horizontalDistance();
                        maxSpeed = cindervane.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.6;
                    }
                }
            } else if (mc.player.getVehicle() instanceof Nulljaw nulljaw) {
                isAccelerating = nulljaw.isAccelerating();
                isFlying = false;

                if (isAccelerating) {
                    currentSpeed = nulljaw.getDeltaMovement().horizontalDistance();
                    maxSpeed = nulljaw.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.0;
                }
            } else if (mc.player.getVehicle() instanceof Ignivorus ignivorus) {
                isAccelerating = ignivorus.isAccelerating();
                isFlying = ignivorus.isFlying();

                if (isAccelerating) {
                    if (isFlying) {
                        currentSpeed = ignivorus.getDeltaMovement().horizontalDistance();
                        maxSpeed = ignivorus.getAttributeValue(Attributes.FLYING_SPEED) * 20.5;
                    } else {
                        currentSpeed = ignivorus.getDeltaMovement().horizontalDistance();
                        maxSpeed = ignivorus.getAttributeValue(Attributes.MOVEMENT_SPEED) * 2.2;
                    }
                }
            } else {
                currentFOVMultiplier = 1.0;
                return;
            }

            // Ignivorus ultimate camera zoom FOV effect
            if (ignivorusVehicle != null) {
                float zoom = ignivorusVehicle.getUltimateCameraZoom((float) event.getPartialTick());
                if (zoom > 0.001F) {
                    double cinematicMultiplier = 1.0 + (zoom * 0.35);
                    targetFOVMultiplier = Math.max(targetFOVMultiplier, cinematicMultiplier);
                }
            }

            if (isAccelerating && maxSpeed > 0) {
                double speedRatio = Math.min(1.0, currentSpeed / maxSpeed);

                if (isFlying) {
                    // Flying sprint - dramatic but cinematic FOV effect
                    targetFOVMultiplier = 1.0 + speedRatio; // Up to 100% wider FOV at max speed
                } else {
                    // Ground sprint - more subtle FOV effect
                    targetFOVMultiplier = 1.0 + (0.15 * speedRatio); // Up to 15% wider FOV at max speed
                }
            }

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

        DragonStatusUI ui = DragonStatusUIManager.getInstance().getDragonStatusUI();
        if (!ui.isRidingDragon() || ui.shouldShowPlayerStats()) {
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
