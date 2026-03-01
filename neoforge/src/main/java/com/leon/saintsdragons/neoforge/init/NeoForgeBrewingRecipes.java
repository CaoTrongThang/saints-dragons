package com.leon.saintsdragons.neoforge.init;

import com.leon.saintsdragons.common.registry.ModItems;
import com.leon.saintsdragons.common.registry.ModPotions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

public final class NeoForgeBrewingRecipes {
    private NeoForgeBrewingRecipes() {
    }

    public static void register(RegisterBrewingRecipesEvent event) {
        ItemStack awkwardPotion = PotionContents.createItemStack(Items.POTION, Potions.AWKWARD);
        ItemStack tideguardPotion = PotionContents.createItemStack(
                ModItems.POTION_OF_TIDEGUARD.get(),
                BuiltInRegistries.POTION.wrapAsHolder(ModPotions.NULLJAW_TIDEGUARD.get())
        );
        ItemStack searingPotion = PotionContents.createItemStack(
                ModItems.POTION_OF_SEARING.get(),
                BuiltInRegistries.POTION.wrapAsHolder(ModPotions.SEARING.get())
        );

        event.getBuilder().addRecipe(
                DataComponentIngredient.of(true, awkwardPotion),
                Ingredient.of(ModItems.NULLJAW_SCALE.get()),
                tideguardPotion
        );
        event.getBuilder().addRecipe(
                DataComponentIngredient.of(true, awkwardPotion),
                Ingredient.of(ModItems.IGNIVORUS_TOOTH.get()),
                searingPotion
        );
    }
}
