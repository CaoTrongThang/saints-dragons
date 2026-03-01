package com.leon.saintsdragons.fabric.mixin;

import com.leon.saintsdragons.fabric.entity.part.FabricDragonPart;
import com.leon.saintsdragons.fabric.entity.part.IgnivorusPartProvider;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MultipartInteractionBridgeMixin {

    @Unique
    private static final double ATTACK_REACH = 16.0;

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleInteract", at = @At("HEAD"), cancellable = true)
    private void saintsdragons$onHandleInteract(ServerboundInteractPacket packet, CallbackInfo ci) {
        ServerLevel level = this.player.serverLevel();
        if (!level.getServer().isSameThread()) {
            // Vanilla will hand this packet off to the main server thread.
            // Do not consume it here, otherwise normal entity interactions are dropped.
            return;
        }

        if (this.saintsdragons$handleMultipartInteract(packet)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean saintsdragons$handleMultipartInteract(ServerboundInteractPacket packet) {
        ServerLevel level = this.player.serverLevel();
        int entityId = ((ServerboundInteractPacketIdAccessor) packet).getEntityId();
        Entity vanillaEntity = level.getEntity(entityId);

        if (vanillaEntity instanceof FabricDragonPart directPart) {
            packet.dispatch(new ServerboundInteractPacket.Handler() {
                @Override
                public void onInteraction(InteractionHand hand) {
                    directPart.interact(player, hand);
                }

                @Override
                public void onInteraction(InteractionHand hand, Vec3 pos) {
                    directPart.interactAt(player, pos, hand);
                }

                @Override
                public void onAttack() {
                    player.attack(directPart);
                }
            });
            return true;
        }

        if (vanillaEntity == null || !saintsdragons$isPlausibleVanillaTarget(vanillaEntity)) {
            FabricDragonPart hitPart = saintsdragons$findHitPartEntity(level);

            if (hitPart != null) {
                packet.dispatch(new ServerboundInteractPacket.Handler() {
                    @Override
                    public void onInteraction(InteractionHand hand) {
                        hitPart.interact(player, hand);
                    }

                    @Override
                    public void onInteraction(InteractionHand hand, Vec3 pos) {
                        hitPart.interactAt(player, pos, hand);
                    }
                    @Override
                    public void onAttack() {
                        player.attack(hitPart);
                    }
                });
                return true;
            }
        }

        return false;
    }

    @Unique
    private boolean saintsdragons$isPlausibleVanillaTarget(Entity entity) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 reachPos = eyePos.add(lookVec.scale(ATTACK_REACH));
        AABB targetBox = entity.getBoundingBox().inflate(0.35D);
        return targetBox.clip(eyePos, reachPos).isPresent();
    }

    @Unique
    private FabricDragonPart saintsdragons$findHitPartEntity(ServerLevel level) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 reachPos = eyePos.add(lookVec.scale(ATTACK_REACH));

        FabricDragonPart closestPart = null;
        double closestDistance = Double.MAX_VALUE;
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof IgnivorusPartProvider provider) {
                FabricDragonPart[] providerParts = provider.saintsdragons$getParts();
                for (FabricDragonPart part : providerParts) {
                    if (part == null) {
                        continue;
                    }
                    AABB box = part.getBoundingBox();
                    var clipResult = box.clip(eyePos, reachPos);
                    if (clipResult.isPresent()) {
                        double distance = eyePos.distanceToSqr(clipResult.get());
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestPart = part;
                        }
                    }
                }
            }
        }

        return closestPart;
    }

}
