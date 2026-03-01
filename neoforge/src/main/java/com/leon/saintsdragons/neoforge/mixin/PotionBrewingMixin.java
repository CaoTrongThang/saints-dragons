package com.leon.saintsdragons.neoforge.mixin;

import com.leon.saintsdragons.common.registry.ModPotions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public final class PotionBrewingMixin {

    private static boolean isSaintsCustomPotion(ItemStack stack) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return contents.is(BuiltInRegistries.POTION.wrapAsHolder(ModPotions.NULLJAW_TIDEGUARD.get()))
                || contents.is(BuiltInRegistries.POTION.wrapAsHolder(ModPotions.SEARING.get()));
    }

    private static boolean isVanillaContainerConversionIngredient(ItemStack ingredient) {
        return ingredient.is(Items.GUNPOWDER) || ingredient.is(Items.DRAGON_BREATH);
    }

    @Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
    private void saintsdragons$blockSplashAndLingeringForCustomPotions(
            ItemStack input,
            ItemStack ingredient,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (isSaintsCustomPotion(input) && isVanillaContainerConversionIngredient(ingredient)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private void saintsdragons$preserveCustomPotionContainer(
            ItemStack ingredient,
            ItemStack input,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        if (isSaintsCustomPotion(input) && isVanillaContainerConversionIngredient(ingredient)) {
            cir.setReturnValue(input.copy());
        }
    }
}
