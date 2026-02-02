package com.leon.saintsdragons.neoforge.mixin;

import com.leon.saintsdragons.neoforge.entity.part.NeoForgeIgnivorusPartManager;
import com.leon.saintsdragons.server.entity.dragons.ignivorus.Ignivorus;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import net.neoforged.neoforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to add NeoForge multi-part entity support to Ignivorus.
 * Implements IEntityExtension methods for multi-part entity handling.
 */
@Mixin(Ignivorus.class)
public abstract class IgnivorusMultipartMixin implements IEntityExtension {

    @Unique
    private NeoForgeIgnivorusPartManager saintsdragons$neoforgePartManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(EntityType<?> type, Level level, CallbackInfo ci) {
        this.saintsdragons$neoforgePartManager = new NeoForgeIgnivorusPartManager((Ignivorus) (Object) this);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Ignivorus ignivorus = (Ignivorus) (Object) this;
        if (ignivorus.isRemoved()) {
            if (this.saintsdragons$neoforgePartManager != null) {
                this.saintsdragons$neoforgePartManager.removeAllParts();
                this.saintsdragons$neoforgePartManager = null;
            }
            return;
        }

        if (ignivorus.isBaby()) {
            if (this.saintsdragons$neoforgePartManager != null) {
                this.saintsdragons$neoforgePartManager.removeAllParts();
                this.saintsdragons$neoforgePartManager = null;
            }
            return;
        }

        if (this.saintsdragons$neoforgePartManager == null) {
            this.saintsdragons$neoforgePartManager = new NeoForgeIgnivorusPartManager(ignivorus);
        }
        this.saintsdragons$neoforgePartManager.updatePartPositions();
    }

    @Override
    public boolean isMultipartEntity() {
        return this.saintsdragons$neoforgePartManager != null;
    }

    @Override
    public PartEntity<?>[] getParts() {
        if (this.saintsdragons$neoforgePartManager == null) {
            return new PartEntity[0];
        }
        return this.saintsdragons$neoforgePartManager.getParts();
    }
}
