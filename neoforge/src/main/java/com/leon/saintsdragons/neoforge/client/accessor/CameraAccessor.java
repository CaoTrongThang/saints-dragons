package com.leon.saintsdragons.neoforge.client.accessor;

import net.minecraft.client.Camera;

/**
 * Helper to access Camera methods.
 * In NeoForge, we use access wideners to make Camera.move() accessible.
 */
public final class CameraAccessor {
    private CameraAccessor() {}

    public static void invokeMove(Camera camera, double x, double y, double z) {
        // Access widener makes this method accessible
        // Cast to float as Camera.move uses floats
        camera.move((float) x, (float) y, (float) z);
    }
}
