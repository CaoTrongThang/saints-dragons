package com.leon.saintsdragons.neoforge;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.common.init.CommonModEvents;
import com.leon.saintsdragons.server.entity.base.DragonEntity;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

@EventBusSubscriber(modid = SaintsDragonsCommon.MOD_ID)
public final class NeoForgeGameEvents {
    private NeoForgeGameEvents() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommonModEvents.registerCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerAttackEntity(AttackEntityEvent event) {
        if (event == null || event.getEntity() == null) {
            return;
        }
        if (event.getEntity().level().isClientSide) {
            return;
        }

        var player = event.getEntity();
        var target = event.getTarget();

        if (target instanceof DragonEntity babyDragon && babyDragon.isBaby() && player instanceof ServerPlayer serverPlayer) {
            var advancement = serverPlayer.server.getAdvancements()
                    .get(SaintsDragonsCommon.rl("why"));
            if (advancement != null) {
                serverPlayer.getAdvancements().award(advancement, "hit_baby");
            }
        }

        if (!(player.getVehicle() instanceof DragonEntity dragon)) {
            return;
        }
        if (dragon.isBaby()) {
            return;
        }
        if (dragon.areRiderControlsLocked()) {
            return;
        }
        if (!dragon.isTame() || !dragon.isOwnedBy(player)) {
            return;
        }

        var abilityType = dragon.getPrimaryAttackAbility();
        if (abilityType != null) {
            dragon.combatManager.tryUseAbility(abilityType);
        }

        event.setCanceled(true);
    }
}
