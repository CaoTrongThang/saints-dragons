package com.leon.saintsdragons.client.renderer.ignivorus;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.server.entity.effect.ignivorus.IgnivorusFlameEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * Renders Ignivorus flame projectiles as camera-facing billboards with animated textures.
 */
public class IgnivorusFlameRenderer extends EntityRenderer<IgnivorusFlameEntity> {

    private static final int TOTAL_FRAMES = 16;
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[TOTAL_FRAMES];

    static {
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            TEXTURES[i] = SaintsDragonsCommon.rl("textures/entity/ignivorus/fireparticle" + i + ".png");
        }
    }

    public IgnivorusFlameRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull IgnivorusFlameEntity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {

        // Calculate animation frame based on age
        // Moderate animation speed - 3 ticks per frame = ~7 FPS animation
        int age = entity.getAge();
        int frame = (age / 3) % TOTAL_FRAMES;

        ResourceLocation texture = TEXTURES[frame];

        // Progressive scale growth - starts at base scale, grows to 2x by end
        int lifetime = entity.getLifetime();
        float ageRatio = Mth.clamp((float) age / (float) lifetime, 0.0F, 1.0F);
        float baseScale = entity.getScale();
        float growthMultiplier = 1.0F + ageRatio; // 1.0 at start, 2.0 at end
        float scale = baseScale * growthMultiplier;

        // Fade in at start, fade out at end
        float alpha = 1.0F;
        if (age < 3) {
            alpha = age / 3.0F; // Fade in over first 3 ticks
        } else if (lifetime - age < 5) {
            alpha = (lifetime - age) / 5.0F; // Fade out over last 5 ticks
        }
        alpha = Mth.clamp(alpha, 0.0F, 1.0F);

        poseStack.pushPose();

        // Scale the billboard
        poseStack.scale(scale, scale, scale);

        // Apply camera-facing rotation (billboard effect)
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        // Get matrix for rendering
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        // Get vertex consumer - use entityCutoutNoCull for better visibility during testing
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));

        // Render quad (camera-facing billboard)
        renderBillboard(vertexConsumer, pose, matrix4f, packedLight, alpha);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void renderBillboard(VertexConsumer consumer, PoseStack.Pose pose, Matrix4f matrix4f, int packedLight, float alpha) {
        float size = 1.0F; // Increased from 0.5F for bigger flames

        // Render both front and back faces to ensure visibility
        // Front face (counter-clockwise when viewed from front)
        addVertex(consumer, pose, matrix4f, -size, -size, 0.0F, 0.0F, 1.0F, alpha);
        addVertex(consumer, pose, matrix4f, -size, size, 0.0F, 0.0F, 0.0F, alpha);
        addVertex(consumer, pose, matrix4f, size, size, 0.0F, 1.0F, 0.0F, alpha);
        addVertex(consumer, pose, matrix4f, size, -size, 0.0F, 1.0F, 1.0F, alpha);
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose pose, Matrix4f matrix4f,
                          float x, float y, float z, float u, float v, float alpha) {
        consumer.addVertex(matrix4f, x, y, z)
                .setColor(1.0F, 1.0F, 1.0F, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(240)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull IgnivorusFlameEntity entity) {
        return TEXTURES[0]; // Default texture (not actually used in render method)
    }
}
