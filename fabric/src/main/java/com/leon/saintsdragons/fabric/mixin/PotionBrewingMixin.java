package com.leon.saintsdragons.fabric.mixin;

import com.leon.saintsdragons.common.registry.ModItems;
import com.leon.saintsdragons.common.registry.ModPotions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public final class PotionBrewingMixin {
    private boolean isCustomRecipeIngredient(ItemStack ingredient) {
        return ingredient.is(ModItems.NULLJAW_SCALE.get()) || ingredient.is(ModItems.IGNIVORUS_TOOTH.get());
    }

    private boolean isSaintsCustomPotion(ItemStack stack) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return contents.is(BuiltInRegistries.POTION.wrapAsHolder(ModPotions.NULLJAW_TIDEGUARD.get()))
                || contents.is(BuiltInRegistries.POTION.wrapAsHolder(ModPotions.SEARING.get()));
    }

    private boolean isVanillaContainerConversionIngredient(ItemStack ingredient) {
        return ingredient.is(Items.GUNPOWDER) || ingredient.is(Items.DRAGON_BREATH);
    }

    @Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
    private void saintsdragons$allowCustomPotionIngredients(
            ItemStack ingredient,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (isCustomRecipeIngredient(ingredient)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
    private void saintsdragons$blockAwkwardSplashAndLingering(
            ItemStack input,
            ItemStack ingredient,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (isSaintsCustomPotion(input) && isVanillaContainerConversionIngredient(ingredient)) {
            cir.setReturnValue(false);
            return;
        }

        if (!isCustomRecipeIngredient(ingredient)) {
            return;
        }

        PotionContents potionContents = input.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (!potionContents.is(Potions.AWKWARD)) {
            cir.setReturnValue(false);
            return;
        }

        cir.setReturnValue(input.is(Items.POTION));
    }

    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private void saintsdragons$customPotionItemOutput(
            ItemStack ingredient,
            ItemStack input,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        if (isSaintsCustomPotion(input) && isVanillaContainerConversionIngredient(ingredient)) {
            cir.setReturnValue(input.copy());
            return;
        }

        PotionContents potionContents = input.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (!potionContents.is(Potions.AWKWARD) || !input.is(Items.POTION)) {
            return;
        }

        if (ingredient.is(ModItems.NULLJAW_SCALE.get())) {
            ItemStack output = PotionContents.createItemStack(
                    ModItems.POTION_OF_TIDEGUARD.get(),
                    BuiltInRegistries.POTION.wrapAsHolder(ModPotions.NULLJAW_TIDEGUARD.get())
            );
            cir.setReturnValue(output);
            return;
        }

        if (ingredient.is(ModItems.IGNIVORUS_TOOTH.get())) {
            ItemStack output = PotionContents.createItemStack(
                    ModItems.POTION_OF_SEARING.get(),
                    BuiltInRegistries.POTION.wrapAsHolder(ModPotions.SEARING.get())
            );
            cir.setReturnValue(output);
        }
    }
}
