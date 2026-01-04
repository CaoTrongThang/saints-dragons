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
    private static Method setRotationMethod;
    private static Method getXRotMethod;
    private static Method getYRotMethod;
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

    public static void invokeSetRotation(Camera camera, float yaw, float pitch) {
        if (!initialized) {
            initializeReflection();
        }

        try {
            if (setRotationMethod != null) {
                setRotationMethod.invoke(camera, yaw, pitch);
            } else {
                LOGGER.warn("Camera.setRotation() is not accessible; skipping rotation update");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to invoke Camera.setRotation()", e);
        }
    }

    public static float invokeGetXRot(Camera camera) {
        if (!initialized) {
            initializeReflection();
        }

        try {
            if (getXRotMethod != null) {
                return (float) getXRotMethod.invoke(camera);
            }
            return camera.getXRot();
        } catch (IllegalAccessException e) {
            try {
                return camera.getXRot();
            } catch (Exception e2) {
                LOGGER.error("Failed to invoke Camera.getXRot()", e2);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to invoke Camera.getXRot()", e);
        }
        return 0.0f;
    }

    public static float invokeGetYRot(Camera camera) {
        if (!initialized) {
            initializeReflection();
        }

        try {
            if (getYRotMethod != null) {
                return (float) getYRotMethod.invoke(camera);
            }
            return camera.getYRot();
        } catch (IllegalAccessException e) {
            try {
                return camera.getYRot();
            } catch (Exception e2) {
                LOGGER.error("Failed to invoke Camera.getYRot()", e2);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to invoke Camera.getYRot()", e);
        }
        return 0.0f;
    }

    private static void initializeReflection() {
        initialized = true;
        try {
            moveMethod = Camera.class.getDeclaredMethod("move", float.class, float.class, float.class);
            moveMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            LOGGER.warn("Could not find Camera.move() method, will try direct access");
        }
        try {
            setRotationMethod = Camera.class.getDeclaredMethod("setRotation", float.class, float.class);
            setRotationMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            LOGGER.warn("Could not find Camera.setRotation() method, will try direct access");
        }
        try {
            getXRotMethod = Camera.class.getDeclaredMethod("getXRot");
            getXRotMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            LOGGER.warn("Could not find Camera.getXRot() method, will try direct access");
        }
        try {
            getYRotMethod = Camera.class.getDeclaredMethod("getYRot");
            getYRotMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            LOGGER.warn("Could not find Camera.getYRot() method, will try direct access");
        }
    }
}
