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

    /**
     * Accessor methods for other parts of the mod to call.
     */
    @Override
    public void saintsdragons$invokeMove(double x, double y, double z) {
        // Vanilla signature uses floats; cast the higher precision inputs.
        this.move((float) x, (float) y, (float) z);
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
