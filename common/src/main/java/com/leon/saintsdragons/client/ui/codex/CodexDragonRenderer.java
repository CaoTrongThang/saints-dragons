package com.leon.saintsdragons.client.ui.codex;

import com.leon.saintsdragons.client.ui.DraconicCodexScreen;
import com.leon.saintsdragons.common.registry.ModEntities;
import com.leon.saintsdragons.server.entity.base.DragonEntity;
import com.leon.saintsdragons.server.entity.base.DragonGender;
import com.leon.saintsdragons.server.entity.dragons.ignivorus.Ignivorus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CodexDragonRenderer {
    private static final int IGNIVORUS_SCALE = 8;
    private static final int RAEVYX_SCALE = 13;
    private static final int NULLJAW_SCALE = 15;
    private static final int CINDERVANE_SCALE = 12;
    private static final int STEGONAUT_SCALE = 23;

    private static final int IGNIVORUS_OFFSET_X = 0;
    private static final int IGNIVORUS_OFFSET_Y = -10;
    private static final int RAEVYX_OFFSET_X = 0;
    private static final int RAEVYX_OFFSET_Y = -30;
    private static final int NULLJAW_OFFSET_X = 0;
    private static final int NULLJAW_OFFSET_Y = -35;
    private static final int CINDERVANE_OFFSET_X = 0;
    private static final int CINDERVANE_OFFSET_Y = -30;
    private static final int STEGONAUT_OFFSET_X = 0;
    private static final int STEGONAUT_OFFSET_Y = -30;

    public void drawDragonPortrait(GuiGraphics guiGraphics, Minecraft minecraft, CodexDragonEntry selected,
                                   int leftPos, int topPos, int mouseX, int mouseY) {
        if (minecraft == null || minecraft.level == null || selected == null || selected.entityId() == null) {
            return;
        }

        DragonEntity dragon = findDragonEntity(minecraft, selected.entityId());
        if (dragon == null) {
            dragon = createDummyDragon(minecraft, selected);
            if (dragon == null) {
                return;
            }
        }

        int boxX = leftPos + CodexLayout.DRAGON_RENDER_BOX_X;
        int boxY = topPos + CodexLayout.DRAGON_RENDER_BOX_Y;
        int size = getDragonScale(dragon);
        int centerX = boxX + (CodexLayout.DRAGON_RENDER_BOX_SIZE / 2) + getDragonOffsetX(dragon);
        int centerY = boxY + CodexLayout.DRAGON_RENDER_BOX_SIZE + getDragonOffsetY(dragon);

        guiGraphics.enableScissor(boxX, boxY,
                boxX + CodexLayout.DRAGON_RENDER_BOX_SIZE,
                boxY + CodexLayout.DRAGON_RENDER_BOX_SIZE);

        DraconicCodexScreen.RENDERING_IN_GUI.set(true);
        try {
            float yaw = (float) Math.atan((centerX - mouseX) / 40.0F);
            float pitch = (float) Math.atan((centerY - mouseY) / 40.0F);

            Quaternionf bodyRotation = new Quaternionf().rotateZ((float) Math.PI);
            Quaternionf headRotation = new Quaternionf().rotateX(pitch * 20.0F * 0.017453292F);
            bodyRotation.mul(headRotation);

            float entityScale = dragon.getScale();
            Vector3f translate = new Vector3f(0.0F, dragon.getBbHeight() / 2.0F + 0.0625F * entityScale, 0.0F);
            float renderScale = size / entityScale;

            float oldBodyRot = dragon.yBodyRot;
            float oldYRot = dragon.getYRot();
            float oldXRot = dragon.getXRot();
            float oldHeadRotO = dragon.yHeadRotO;
            float oldHeadRot = dragon.yHeadRot;

            dragon.yBodyRot = 180.0F + yaw * 20.0F;
            dragon.setYRot(180.0F + yaw * 40.0F);
            dragon.setXRot(-pitch * 20.0F);
            dragon.yHeadRot = dragon.getYRot();
            dragon.yHeadRotO = dragon.getYRot();

            InventoryScreen.renderEntityInInventory(
                    guiGraphics,
                    centerX,
                    centerY,
                    renderScale,
                    translate,
                    bodyRotation,
                    headRotation,
                    dragon
            );

            dragon.yBodyRot = oldBodyRot;
            dragon.setYRot(oldYRot);
            dragon.setXRot(oldXRot);
            dragon.yHeadRotO = oldHeadRotO;
            dragon.yHeadRot = oldHeadRot;
        } finally {
            DraconicCodexScreen.RENDERING_IN_GUI.set(false);
        }

        guiGraphics.disableScissor();
    }

    private int getDragonScale(DragonEntity dragon) {
        if (dragon.getType() == ModEntities.IGNIVORUS.get()) {
            return IGNIVORUS_SCALE;
        } else if (dragon.getType() == ModEntities.RAEVYX.get()) {
            return RAEVYX_SCALE;
        } else if (dragon.getType() == ModEntities.NULLJAW.get()) {
            return NULLJAW_SCALE;
        } else if (dragon.getType() == ModEntities.CINDERVANE.get()) {
            return CINDERVANE_SCALE;
        } else if (dragon.getType() == ModEntities.STEGONAUT.get()) {
            return STEGONAUT_SCALE;
        }
        return 30;
    }

    private int getDragonOffsetX(DragonEntity dragon) {
        if (dragon.getType() == ModEntities.IGNIVORUS.get()) {
            return IGNIVORUS_OFFSET_X;
        } else if (dragon.getType() == ModEntities.RAEVYX.get()) {
            return RAEVYX_OFFSET_X;
        } else if (dragon.getType() == ModEntities.NULLJAW.get()) {
            return NULLJAW_OFFSET_X;
        } else if (dragon.getType() == ModEntities.CINDERVANE.get()) {
            return CINDERVANE_OFFSET_X;
        } else if (dragon.getType() == ModEntities.STEGONAUT.get()) {
            return STEGONAUT_OFFSET_X;
        }
        return 0;
    }

    private int getDragonOffsetY(DragonEntity dragon) {
        if (dragon.getType() == ModEntities.IGNIVORUS.get()) {
            return IGNIVORUS_OFFSET_Y;
        } else if (dragon.getType() == ModEntities.RAEVYX.get()) {
            return RAEVYX_OFFSET_Y;
        } else if (dragon.getType() == ModEntities.NULLJAW.get()) {
            return NULLJAW_OFFSET_Y;
        } else if (dragon.getType() == ModEntities.CINDERVANE.get()) {
            return CINDERVANE_OFFSET_Y;
        } else if (dragon.getType() == ModEntities.STEGONAUT.get()) {
            return STEGONAUT_OFFSET_Y;
        }
        return 0;
    }

    private DragonEntity findDragonEntity(Minecraft minecraft, java.util.UUID dragonId) {
        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (entity instanceof DragonEntity dragon && dragon.getUUID().equals(dragonId)) {
                return dragon;
            }
        }
        return null;
    }

    private DragonEntity createDummyDragon(Minecraft minecraft, CodexDragonEntry entry) {
        EntityType<? extends DragonEntity> entityType = getDragonEntityType(entry.dragonType());
        if (entityType == null) {
            return null;
        }

        DragonEntity dragon = entityType.create(minecraft.level);
        if (dragon == null) {
            return null;
        }

        if (entry.isBaby()) {
            dragon.setBaby(true);
        }

        dragon.setTextureVariant(entry.variantId());

        dragon.setGender(DragonGender.fromId(entry.genderId()));
        return dragon;
    }

    private EntityType<? extends DragonEntity> getDragonEntityType(String dragonType) {
        return switch (dragonType) {
            case "ignivorus" -> ModEntities.IGNIVORUS.get();
            case "raevyx" -> ModEntities.RAEVYX.get();
            case "nulljaw" -> ModEntities.NULLJAW.get();
            case "cindervane" -> ModEntities.CINDERVANE.get();
            case "stegonaut" -> ModEntities.STEGONAUT.get();
            default -> null;
        };
    }
}
