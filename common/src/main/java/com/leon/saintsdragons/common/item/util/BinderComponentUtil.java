package com.leon.saintsdragons.common.item.util;

import com.leon.saintsdragons.common.component.BinderData;
import com.leon.saintsdragons.common.registry.ModDataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public final class BinderComponentUtil {
    private BinderComponentUtil() {
    }

    public static boolean isBound(ItemStack stack) {
        return getData(stack).isBound();
    }

    public static BinderData getData(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.BINDER_DATA.get(), BinderData.EMPTY);
    }

    public static void setData(ItemStack stack, BinderData data) {
        stack.set(ModDataComponents.BINDER_DATA.get(), data);
    }

    public static UUID getBoundDragonUUID(ItemStack stack) {
        return getData(stack).dragonUuid().orElse(null);
    }

    public static String getBoundDragonName(ItemStack stack) {
        return getData(stack).dragonName().orElse(null);
    }
}
