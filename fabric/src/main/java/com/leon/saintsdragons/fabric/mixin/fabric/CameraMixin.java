package com.leon.saintsdragons.fabric.mixin.fabric;

import com.leon.saintsdragons.fabric.client.accessor.CameraAccessor;
import com.leon.saintsdragons.fabric.client.event.FabricClientEventHandler;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraAccessor {

    @Shadow
    protected abstract void move(float x, float y, float z);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Shadow
    protected abstract float getXRot();

    @Shadow
    protected abstract float getYRot();

    @Shadow
    protected abstract float getMaxZoom(float desiredDistance);

    /**
     * Accessor methods for other parts of the mod to call.
     */
    @Override
    public void saintsdragons$invokeMove(double x, double y, double z) {
        // In 1.21.1, Camera.move uses floats (double signature was removed)
        this.move((float) x, (float) y, (float) z);
    }

    @Override
    public void saintsdragons$invokeSetRotation(float yaw, float pitch) {
        this.setRotation(yaw, pitch);
    }

    @Override
    public float saintsdragons$invokeGetXRot() {
        return this.getXRot();
    }

    @Override
    public float saintsdragons$invokeGetYRot() {
        return this.getYRot();
    }

    @Override
    public float saintsdragons$invokeGetMaxZoom(float desiredDistance) {
        return this.getMaxZoom(desiredDistance);
    }

    /**
     * Hook into camera setup to apply custom camera adjustments.
     * This is called after the camera position is set up, allowing us to modify it.
     */
    @Inject(method = "setup", at = @At("RETURN"))
    private void saintsdragons$onCameraSetup(
            net.minecraft.world.level.BlockGetter area,
            net.minecraft.world.entity.Entity focusedEntity,
            boolean thirdPerson,
            boolean inverseView,
            float partialTick,
            CallbackInfo ci) {
        FabricClientEventHandler.onComputeCamera((Camera) (Object) this, partialTick);
    }
}
