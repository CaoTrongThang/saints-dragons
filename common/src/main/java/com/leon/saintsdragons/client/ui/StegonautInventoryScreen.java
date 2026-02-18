package com.leon.saintsdragons.client.ui;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.server.entity.dragons.stegonaut.Stegonaut;
import com.leon.saintsdragons.server.menu.StegonautInventoryMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jetbrains.annotations.Nullable;

public class StegonautInventoryScreen extends AbstractContainerScreen<StegonautInventoryMenu> {
    private static final int BASE_TEX_W = 256;
    private static final int BASE_TEX_H = 256;
    private static final int CHEST_SLOT_TEX_W = 18;
    private static final int CHEST_SLOT_TEX_H = 18;
    private static final int CHEST_SLOTS_TEX_W = 90;
    private static final int CHEST_SLOTS_TEX_H = 54;
    private static final int PREVIEW_OFFSET_X = 51;
    private static final int PREVIEW_OFFSET_Y = 60;
    private static final int PREVIEW_SCALE = 10;
    private static final int PREVIEW_VERTICAL_LIFT = 8;

    private static final ResourceLocation BASE_TEXTURE =
            SaintsDragonsCommon.rl("textures/gui/stegonaut/stegonaut_inventory_gui.png");
    private static final ResourceLocation CHEST_SLOT_TEXTURE =
            SaintsDragonsCommon.rl("textures/gui/stegonaut/chest_slot.png");
    private static final ResourceLocation CHEST_SLOTS_TEXTURE =
            SaintsDragonsCommon.rl("textures/gui/stegonaut/chest_slots.png");
    @Nullable
    private final Stegonaut stegonaut;

    public StegonautInventoryScreen(StegonautInventoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
        Entity vehicle = inventory.player.getVehicle();
        this.stegonaut = vehicle instanceof Stegonaut s ? s : null;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.blit(BASE_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, BASE_TEX_W, BASE_TEX_H);

        guiGraphics.blit(CHEST_SLOT_TEXTURE, x + 7, y + 17, 0, 0, 18, 18, CHEST_SLOT_TEX_W, CHEST_SLOT_TEX_H);
        if (this.menu.hasChestInstalled()) {
            guiGraphics.blit(CHEST_SLOTS_TEXTURE, x + 79, y + 17, 0, 0,
                    this.menu.getChestColumns() * 18, 54, CHEST_SLOTS_TEX_W, CHEST_SLOTS_TEX_H);
        }

        if (this.stegonaut != null) {
            int centerX = x + PREVIEW_OFFSET_X;
            int centerY = y + PREVIEW_OFFSET_Y - PREVIEW_VERTICAL_LIFT;

            // Mirror codex portrait behavior so the inventory preview follows the mouse.
            DraconicCodexScreen.RENDERING_IN_GUI.set(true);
            try {
                float yaw = (float) Math.atan((centerX - mouseX) / 40.0F);
                float pitch = (float) Math.atan((centerY - mouseY) / 40.0F);

                float oldBodyRot = stegonaut.yBodyRot;
                float oldYRot = stegonaut.getYRot();
                float oldXRot = stegonaut.getXRot();
                float oldHeadRot = stegonaut.yHeadRot;
                float oldHeadRotO = stegonaut.yHeadRotO;

                stegonaut.yBodyRot = 180.0F + yaw * 20.0F;
                stegonaut.setYRot(180.0F + yaw * 40.0F);
                stegonaut.setXRot(-pitch * 20.0F);
                stegonaut.yHeadRot = stegonaut.getYRot();
                stegonaut.yHeadRotO = stegonaut.getYRot();

                Quaternionf bodyRotation = new Quaternionf().rotateZ((float) Math.PI);
                Quaternionf headRotation = new Quaternionf().rotateX(pitch * 20.0F * 0.017453292F);
                bodyRotation.mul(headRotation);
                Vector3f translate = new Vector3f(0.0F, stegonaut.getBbHeight() / 2.0F, 0.0F);
                InventoryScreen.renderEntityInInventory(
                        guiGraphics,
                        centerX,
                        centerY,
                        PREVIEW_SCALE,
                        translate,
                        bodyRotation,
                        headRotation,
                        this.stegonaut
                );

                stegonaut.yBodyRot = oldBodyRot;
                stegonaut.setYRot(oldYRot);
                stegonaut.setXRot(oldXRot);
                stegonaut.yHeadRot = oldHeadRot;
                stegonaut.yHeadRotO = oldHeadRotO;
            } finally {
                DraconicCodexScreen.RENDERING_IN_GUI.set(false);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
