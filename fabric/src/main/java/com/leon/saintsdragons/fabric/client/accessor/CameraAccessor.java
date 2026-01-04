package com.leon.saintsdragons.fabric.client.accessor;

/**
 * Interface to access Camera mixin methods.
 * The Camera class will implement this interface via the CameraMixin.
 */
public interface CameraAccessor {
    void saintsdragons$invokeMove(double x, double y, double z);
    void saintsdragons$invokeSetRotation(float yaw, float pitch);
    float saintsdragons$invokeGetXRot();
    float saintsdragons$invokeGetYRot();
}
