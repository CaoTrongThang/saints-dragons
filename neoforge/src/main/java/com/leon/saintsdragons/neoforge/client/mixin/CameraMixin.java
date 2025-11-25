package com.leon.saintsdragons.neoforge.client.mixin;

import com.leon.saintsdragons.neoforge.client.event.NeoForgeClientEventHandler;
import net.minecraft.client.Camera;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    /**
     * Hook into camera setup to apply custom camera adjustments.
     * This is called at the END of camera setup, allowing us to modify the final position.
     */
    @Inject(method = "setup", at = @At("RETURN"))
    private void saintsdragons$onCameraSetup(
            BlockGetter area,
            net.minecraft.world.entity.Entity focusedEntity,
            boolean thirdPerson,
            boolean inverseView,
            float partialTick,
            CallbackInfo ci) {
        NeoForgeClientEventHandler.onCameraSetup((Camera) (Object) this, partialTick);
    }
}
