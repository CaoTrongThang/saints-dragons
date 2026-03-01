package com.leon.saintsdragons.neoforge;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.common.init.CommonModEvents;
import com.leon.saintsdragons.common.registry.ModPotions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = SaintsDragonsCommon.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class NeoForgeEvents {
    private NeoForgeEvents() {}

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        CommonModEvents.registerEntityAttributes((entityType, builder) ->
                event.put(entityType, builder.build()));
    }

    @SubscribeEvent
    public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        CommonModEvents.registerSpawnPlacements(new CommonModEvents.SpawnPlacementRegistrar() {
            @Override
            public <T extends net.minecraft.world.entity.Mob> void register(
                    net.minecraft.world.entity.EntityType<T> type,
                    net.minecraft.world.entity.SpawnPlacementType placementType,
                    net.minecraft.world.level.levelgen.Heightmap.Types heightmap,
                    net.minecraft.world.entity.SpawnPlacements.SpawnPredicate<T> predicate) {
                event.register(type, placementType, heightmap, predicate, RegisterSpawnPlacementsEvent.Operation.AND);
            }
        });
    }

    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        CommonModEvents.registerCreativeTabEntries((tab, itemSupplier) -> {
            if (event.getTabKey() == tab) {
                event.accept(itemSupplier.get());
            }
        });

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES
                || event.getTabKey() == CreativeModeTabs.SEARCH) {
            event.getParentEntries().removeIf(NeoForgeEvents::isHiddenVanillaPotionVariant);
            event.getSearchEntries().removeIf(NeoForgeEvents::isHiddenVanillaPotionVariant);
        }
    }

    private static boolean isHiddenVanillaPotionVariant(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        if (!stack.is(Items.POTION) && !stack.is(Items.SPLASH_POTION) && !stack.is(Items.LINGERING_POTION)) {
            return false;
        }

        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return contents.is(BuiltInRegistries.POTION.wrapAsHolder(ModPotions.NULLJAW_TIDEGUARD.get()))
                || contents.is(BuiltInRegistries.POTION.wrapAsHolder(ModPotions.SEARING.get()));
    }
}
