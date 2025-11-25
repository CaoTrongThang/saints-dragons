package com.leon.saintsdragons.neoforge.client.accessor;

import net.minecraft.client.Camera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Helper to access Camera methods.
 * Uses reflection to access Camera.move() since access wideners may not work in dev environment.
 */
public final class CameraAccessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CameraAccessor.class);
    private static Method moveMethod;
    private static boolean initialized = false;

    private CameraAccessor() {}

    public static void invokeMove(Camera camera, double x, double y, double z) {
        if (!initialized) {
            initializeReflection();
        }

        try {
            if (moveMethod != null) {
                moveMethod.invoke(camera, (float) x, (float) y, (float) z);
            } else {
                // Fallback: try direct access (works if access widener is applied)
                camera.move((float) x, (float) y, (float) z);
            }
        } catch (IllegalAccessException e) {
            // Try direct call as fallback
            try {
                camera.move((float) x, (float) y, (float) z);
            } catch (Exception e2) {
                LOGGER.error("Failed to invoke Camera.move()", e2);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to invoke Camera.move()", e);
        }
    }

    private static void initializeReflection() {
        initialized = true;
        try {
            moveMethod = Camera.class.getDeclaredMethod("move", float.class, float.class, float.class);
            moveMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            LOGGER.warn("Could not find Camera.move() method, will try direct access");
        }
    }
}
