package com.leon.saintsdragons.neoforge.client;

import com.leon.saintsdragons.neoforge.platform.NeoForgeDragonAttributesConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

/**
 * NeoForge config screen for miscellaneous settings (NPCs, etc.)
 */
@OnlyIn(Dist.CLIENT)
public final class ForgeOthersScreen extends ForgePagedConfigScreen {

    public ForgeOthersScreen(Screen parent) {
        super(parent, Component.translatable("saintsdragons.config_screen.others"));
    }

    @Override
    protected void buildEntries(List<ConfigEntry> entries) {
        // Ivy the Dragon Merchant
        entries.add(new SectionEntry(Component.translatable("saintsdragons.config_screen.others.ivy")));
        entries.add(new IntEntry(
                Component.translatable("saintsdragons.config_screen.others.ivy.restock_interval"),
                () -> NeoForgeDragonAttributesConfig.IVY_RESTOCK_INTERVAL.get(),
                val -> NeoForgeDragonAttributesConfig.IVY_RESTOCK_INTERVAL.set(val),
                null
        ));
    }

    @Override
    protected void onSave() {
        NeoForgeDragonAttributesConfig.ATTRIBUTES_SPEC.save();
    }
}
