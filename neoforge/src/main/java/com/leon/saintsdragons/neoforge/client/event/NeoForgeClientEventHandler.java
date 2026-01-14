package com.leon.saintsdragons.neoforge.client.event;

import com.leon.saintsdragons.client.sound.ignivorus.IgnivorusFireBreathSoundController;
import com.leon.saintsdragons.client.sound.raevyx.RaevyxLightningBeamSoundController;
import com.leon.saintsdragons.neoforge.client.accessor.CameraAccessor;
import com.leon.saintsdragons.server.entity.dragons.cindervane.Cindervane;
import com.leon.saintsdragons.server.entity.dragons.ignivorus.Ignivorus;
import com.leon.saintsdragons.server.entity.dragons.nulljaw.Nulljaw;
import com.leon.saintsdragons.server.entity.dragons.raevyx.Raevyx;
import com.leon.saintsdragons.server.entity.interfaces.ShakesScreen;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

/**
 * NeoForge client event handler for camera adjustments and screen shake effects.
 */
public class NeoForgeClientEventHandler {
    private static final double[] randomTremorOffsets = new double[3];

    // Raevyx takeoff camera zoom transition
    private static float raevyxCameraZoom = 18F; // Base zoom
    private static float raevyxCameraZoomTarget = 10F;

    // Raevyx camera shift smoothing (banking response)
    private static double raevyxCameraShift = 0.0;

    // Cindervane takeoff camera zoom transition
    private static float cindervaneCameraZoom = 5F; // Base zoom
    private static float cindervaneCameraZoomTarget = 15F;

    // Cindervane camera shift smoothing (banking response)
    private static double cindervaneCameraShift = 0.0;

    // Ignivorus camera zoom transition
    private static float ignivorusCameraZoom = 15F; // Base zoom
    private static float ignivorusCameraZoomTarget = 15F;

    // Ignivorus camera shift smoothing (banking response)
    private static double ignivorusCameraShift = 0.0;

    // Shared vertical camera shift for ascending/descending (both dragons)
    private static double verticalCameraShift = 0.0;
    // Camera pitch smoothing (per dragon)
    private static float raevyxCameraPitch = 0.0f;
    private static float cindervaneCameraPitch = 0.0f;
    private static float ignivorusCameraPitch = 0.0f;
    private static float nulljawCameraPitch = 0.0f;

    public static void onClientTick(Minecraft minecraft) {
        RaevyxLightningBeamSoundController.tick(minecraft);
        IgnivorusFireBreathSoundController.tick(minecraft);
    }

    /**
     * Called from CameraMixin at the end of Camera.setup() to apply camera adjustments.
     */
    public static void onCameraSetup(Camera camera, float partialTicks) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player == null) return;

        // Dragon riding camera adjustments - Raevyx
        if (player.isPassenger() && player.getVehicle() instanceof Raevyx raevyx && camera.isDetached()) {
            // Determine target zoom based on flight state
            boolean isFlying = raevyx.isFlying();

            // Flying: zoom to 18F, grounded: 18F base
            raevyxCameraZoomTarget = isFlying ? 13F : 15F;

            // Smooth transition (slower blend rate for more gradual zoom)
            float blendRate = 0.05F;
            raevyxCameraZoom += (raevyxCameraZoomTarget - raevyxCameraZoom) * blendRate;

            // Calculate camera shift based on banking (only when flying)
            double targetCameraShift = 0.0;
            if (isFlying) {
                // Get interpolated bank angle (-90 to +90 degrees)
                float bankAngle = raevyx.getBankAngleDegrees(partialTicks);

                // Calculate lateral shift magnitude based on bank angle and velocity
                double velocity = raevyx.getDeltaMovement().horizontalDistance();
                double velocityFactor = Math.min(velocity * 2.0, 1.5); // Cap at 1.5x

                // Convert bank angle to shift
                // Scale: at 45° bank with full velocity, shift ~5.5 blocks (more aggressive than Cindervane)
                targetCameraShift = (bankAngle / 45.0) * 5.5 * velocityFactor;
            }

            // Smooth the camera shift for gradual, natural movement
            double shiftBlendRate = 0.15;
            raevyxCameraShift += (targetCameraShift - raevyxCameraShift) * shiftBlendRate;

            // Calculate vertical camera shift based on ascending/descending
            double targetVerticalShift = isFlying ? 50.0 : 0.0;
            // Smooth vertical shift
            double verticalBlendRate = 0.12; // Slightly slower than lateral for smoother feel
            verticalCameraShift += (targetVerticalShift - verticalCameraShift) * verticalBlendRate;

            // Apply the smoothed zoom and lateral shift using the accessor
            CameraAccessor.invokeMove(camera, -raevyxCameraZoom, 0, 0);
            // Apply lateral and vertical shifts
            CameraAccessor.invokeMove(camera, 0, verticalCameraShift, raevyxCameraShift);
            // Slight downward tilt for better forward visibility
            float raevyxTargetPitch = isFlying ? 6.0f : 0.0f;
            float raevyxPitchBlendRate = 0.15f;
            raevyxCameraPitch += (raevyxTargetPitch - raevyxCameraPitch) * raevyxPitchBlendRate;
            float raevyxYaw = CameraAccessor.invokeGetYRot(camera);
            float raevyxPitch = CameraAccessor.invokeGetXRot(camera);
            CameraAccessor.invokeSetRotation(
                    camera,
                    raevyxYaw,
                    Mth.clamp(raevyxPitch + raevyxCameraPitch, -90.0f, 90.0f)
            );
        } else {
            // Reset zoom and shift when not riding Raevyx
            raevyxCameraZoom = 15F;
            raevyxCameraZoomTarget = 15F;
            raevyxCameraShift = 0.0;
            verticalCameraShift = 0.0;
            raevyxCameraPitch = 0.0f;
        }

        // Dragon riding camera adjustments - Cindervane
        if (player.isPassenger() && player.getVehicle() instanceof Cindervane cindervane && camera.isDetached()) {
            // Determine target zoom based on flight state
            boolean isFlying = cindervane.isFlying();

            // Flying: zoom to 30F, grounded: 15F base
            cindervaneCameraZoomTarget = isFlying ? 30F : 15F;

            // Smooth transition
            float blendRate = 0.05F;
            cindervaneCameraZoom += (cindervaneCameraZoomTarget - cindervaneCameraZoom) * blendRate;

            // Calculate camera shift based on banking (only when flying)
            double targetCameraShift = 0.0;
            if (isFlying) {
                // Get interpolated bank angle (-90 to +90 degrees)
                float bankAngle = cindervane.getBankAngleDegrees(partialTicks);

                // Calculate lateral shift magnitude based on bank angle and velocity
                // More banking = more shift. Scale by velocity for dynamic feel.
                double velocity = cindervane.getDeltaMovement().horizontalDistance();
                double velocityFactor = Math.min(velocity * 2.0, 1.5); // Cap at 1.5x

                targetCameraShift = (bankAngle / 45.0) * 5.5 * velocityFactor;
            }

            // Smooth the camera shift for gradual, natural movement
            double shiftBlendRate = 0.15; // Faster response than zoom for snappy feel
            cindervaneCameraShift += (targetCameraShift - cindervaneCameraShift) * shiftBlendRate;

            // Calculate vertical camera shift based on ascending/descending
            double targetVerticalShift = isFlying ? 50.0 : 0.0;
            // Smooth vertical shift
            double verticalBlendRate = 0.12; // Slightly slower than lateral for smoother feel
            verticalCameraShift += (targetVerticalShift - verticalCameraShift) * verticalBlendRate;

            // Apply the smoothed zoom and lateral shift using the accessor
            // Move camera: back (zoom), no vertical, lateral shift based on banking
            CameraAccessor.invokeMove(camera, -cindervaneCameraZoom, 0, 0);
            // Apply lateral and vertical shifts
            CameraAccessor.invokeMove(camera, 0, verticalCameraShift, cindervaneCameraShift);
            // Slight downward tilt for better forward visibility
            float cindervaneTargetPitch = isFlying ? 10.0f : 0.0f;
            float cindervanePitchBlendRate = 0.15f;
            cindervaneCameraPitch += (cindervaneTargetPitch - cindervaneCameraPitch) * cindervanePitchBlendRate;
            float cindervaneYaw = CameraAccessor.invokeGetYRot(camera);
            float cindervanePitch = CameraAccessor.invokeGetXRot(camera);
            CameraAccessor.invokeSetRotation(
                    camera,
                    cindervaneYaw,
                    Mth.clamp(cindervanePitch + cindervaneCameraPitch, -90.0f, 90.0f)
            );
        } else if (!(player.getVehicle() instanceof Cindervane)) {
            // Reset zoom and shift when not riding Cindervane
            cindervaneCameraZoom = 5F;
            cindervaneCameraZoomTarget = 5F;
            cindervaneCameraShift = 0.0;
            verticalCameraShift = 0.0;
            cindervaneCameraPitch = 0.0f;
        }

        // Dragon riding camera adjustments - Ignivorus
        if (player.isPassenger() && player.getVehicle() instanceof Ignivorus ignivorus && camera.isDetached()) {
            // Determine target zoom based on flight state
            boolean isFlying = ignivorus.isFlying();
            boolean isPhase2 = ignivorus.isPhase2Active();

            // Phase 2 only affects grounded camera zoom
            if (isFlying) {
                ignivorusCameraZoomTarget = 30F;
            } else if (isPhase2) {
                ignivorusCameraZoomTarget = 25F; // Phase 2 grounded = zoom out more
            } else {
                ignivorusCameraZoomTarget = 15F; // Normal grounded
            }

            // Smooth transition
            float blendRate = 0.05F;
            ignivorusCameraZoom += (ignivorusCameraZoomTarget - ignivorusCameraZoom) * blendRate;

            // Calculate camera shift based on banking (only when flying)
            double targetCameraShift = 0.0;
            if (isFlying) {
                // Get interpolated bank angle (-90 to +90 degrees)
                float bankAngle = ignivorus.getBankAngleDegrees(partialTicks);

                // Calculate lateral shift magnitude based on bank angle and velocity
                double velocity = ignivorus.getDeltaMovement().horizontalDistance();
                double velocityFactor = Math.min(velocity * 2.0, 1.5); // Cap at 1.5x

                // Convert bank angle to shift
                // Scale: at 45° bank with full velocity, shift ~4.5 blocks (between Cindervane and Raevyx)
                targetCameraShift = (bankAngle / 45.0) * 6.5 * velocityFactor;
            }

            // Smooth the camera shift for gradual, natural movement
            double shiftBlendRate = 0.15;
            ignivorusCameraShift += (targetCameraShift - ignivorusCameraShift) * shiftBlendRate;

            // Calculate vertical camera shift based on ascending/descending
            double targetVerticalShift = isFlying ? 70.0 : 0.0;
            // Smooth vertical shift for Ignivorus
            double verticalBlendRate = 0.12;
            verticalCameraShift += (targetVerticalShift - verticalCameraShift) * verticalBlendRate;

            // Apply the smoothed zoom using the accessor
            CameraAccessor.invokeMove(camera, -ignivorusCameraZoom, 0, 0);
            // Apply lateral and vertical shifts
            CameraAccessor.invokeMove(camera, 0, verticalCameraShift, ignivorusCameraShift);
            // Slight downward tilt for better forward visibility
            float ignivorusTargetPitch = isFlying ? 10.0f : 0.0f;
            float ignivorusPitchBlendRate = 0.15f;
            ignivorusCameraPitch += (ignivorusTargetPitch - ignivorusCameraPitch) * ignivorusPitchBlendRate;
            float ignivorusYaw = CameraAccessor.invokeGetYRot(camera);
            float ignivorusPitch = CameraAccessor.invokeGetXRot(camera);
            CameraAccessor.invokeSetRotation(
                    camera,
                    ignivorusYaw,
                    Mth.clamp(ignivorusPitch + ignivorusCameraPitch, -90.0f, 90.0f)
            );
        } else if (!(player.getVehicle() instanceof Ignivorus)) {
            // Reset zoom and shift when not riding Ignivorus
            ignivorusCameraZoom = 15F;
            ignivorusCameraZoomTarget = 15F;
            ignivorusCameraShift = 0.0;
            verticalCameraShift = 0.0;
            ignivorusCameraPitch = 0.0f;
        }

        // Nulljaw camera zoom
        if (player.isPassenger() && player.getVehicle() instanceof Nulljaw nulljaw && camera.isDetached()) {
            boolean isSwimming = nulljaw.isInWaterOrBubble();
            if (isSwimming) {
                float blendRate = 0.05F;
                raevyxCameraZoomTarget = 18F;
                raevyxCameraZoom += (raevyxCameraZoomTarget - raevyxCameraZoom) * blendRate;

                double targetCameraShift = 0.0;
                float bankAngle = nulljaw.getSwimRollAngleDegrees(partialTicks);
                double velocity = nulljaw.getDeltaMovement().horizontalDistance();
                double velocityFactor = Math.min(velocity * 2.0, 1.5);
                targetCameraShift = -(bankAngle / 45.0) * 5.5 * velocityFactor;

                double shiftBlendRate = 0.15;
                raevyxCameraShift += (targetCameraShift - raevyxCameraShift) * shiftBlendRate;

                double targetVerticalShift = 30.0;
                double verticalBlendRate = 0.12;
                verticalCameraShift += (targetVerticalShift - verticalCameraShift) * verticalBlendRate;

                CameraAccessor.invokeMove(camera, -raevyxCameraZoom, 0, 0);
                CameraAccessor.invokeMove(camera, 0, verticalCameraShift, raevyxCameraShift);

                float nulljawTargetPitch = 15.0f;
                float pitchBlendRate = 0.15f;
                nulljawCameraPitch += (nulljawTargetPitch - nulljawCameraPitch) * pitchBlendRate;
                float nulljawYaw = CameraAccessor.invokeGetYRot(camera);
                float nulljawPitch = CameraAccessor.invokeGetXRot(camera);
                CameraAccessor.invokeSetRotation(
                        camera,
                        nulljawYaw,
                        Mth.clamp(nulljawPitch + nulljawCameraPitch, -90.0f, 90.0f)
                );
            } else {
                CameraAccessor.invokeMove(camera, -15F, 0, 0);
                raevyxCameraShift = 0.0;
                verticalCameraShift = 0.0;
                nulljawCameraPitch = 0.0f;
            }
        }

        // Screen shake detection and application
        applyScreenShake(camera, player, partialTicks);
    }

    private static void applyScreenShake(Camera camera, Entity player, float partialTicks) {
    double shakeDistanceScale = 64.0;
    double distance = Double.MAX_VALUE;
    float tremorAmount = 0.0F; // Reset tremor amount each frame

    AABB aabb = player.getBoundingBox().inflate(shakeDistanceScale);
    var level = Minecraft.getInstance().level;
    if (level == null) return;

    for (Mob screenShaker : level.getEntitiesOfClass(Mob.class, aabb, (mob -> mob instanceof ShakesScreen))) {
        ShakesScreen shakesScreen = (ShakesScreen) screenShaker;
        if (shakesScreen.canFeelShake(player) && screenShaker.distanceTo(player) < distance) {
            distance = screenShaker.distanceTo(player);
            float shakeAmount = shakesScreen.getScreenShakeAmount(partialTicks);
            tremorAmount = Math.min((1F - (float) Math.min(1, distance / shakesScreen.getShakeDistance())) * Math.max(shakeAmount, 0F), 2.0F);
        }
    }

    if (tremorAmount > 0) {
        // Generate random offsets for camera movement
        double intensity = tremorAmount * Minecraft.getInstance().options.screenEffectScale().get();

        CameraAccessor.invokeMove(camera,
                randomTremorOffsets[0] * 0.2F * intensity,
                randomTremorOffsets[1] * 0.2F * intensity,
                randomTremorOffsets[2] * 0.5F * intensity
        );

        // Update random offsets for next frame
        randomTremorOffsets[0] = (Math.random() - 0.5) * 2.0;
        randomTremorOffsets[1] = (Math.random() - 0.5) * 2.0;
        randomTremorOffsets[2] = (Math.random() - 0.5) * 2.0;
    }
    }
}
