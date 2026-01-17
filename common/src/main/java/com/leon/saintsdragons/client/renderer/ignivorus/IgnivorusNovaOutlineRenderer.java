package com.leon.saintsdragons.client.renderer.ignivorus;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.server.entity.effect.ignivorus.IgnivorusNovaOutlineEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class IgnivorusNovaOutlineRenderer extends EntityRenderer<IgnivorusNovaOutlineEntity> {

    private static final ResourceLocation TEXTURE =
            SaintsDragonsCommon.rl("textures/entity/ignivorus/nova0.png");

    public IgnivorusNovaOutlineRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull IgnivorusNovaOutlineEntity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {

        float scale = entity.getScale(partialTicks);
        float opacity = entity.getOpacity(partialTicks);

        if (opacity <= 0.001F) {
            return;
        }

        poseStack.pushPose();

        float age = entity.getAge() + partialTicks;
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(age * 5.0F));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(age * 3.5F));

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        float s = scale * 16.0F;

        renderCubeOutline(consumer, pose, matrix, s, opacity);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void renderCubeOutline(VertexConsumer consumer, PoseStack.Pose pose, Matrix4f matrix, float s, float opacity) {
        addLine(consumer, pose, matrix, -s, -s, -s, s, -s, -s, opacity);
        addLine(consumer, pose, matrix, s, -s, -s, s, s, -s, opacity);
        addLine(consumer, pose, matrix, s, s, -s, -s, s, -s, opacity);
        addLine(consumer, pose, matrix, -s, s, -s, -s, -s, -s, opacity);

        addLine(consumer, pose, matrix, -s, -s, s, s, -s, s, opacity);
        addLine(consumer, pose, matrix, s, -s, s, s, s, s, opacity);
        addLine(consumer, pose, matrix, s, s, s, -s, s, s, opacity);
        addLine(consumer, pose, matrix, -s, s, s, -s, -s, s, opacity);

        addLine(consumer, pose, matrix, -s, -s, -s, -s, -s, s, opacity);
        addLine(consumer, pose, matrix, s, -s, -s, s, -s, s, opacity);
        addLine(consumer, pose, matrix, s, s, -s, s, s, s, opacity);
        addLine(consumer, pose, matrix, -s, s, -s, -s, s, s, opacity);
    }

    private void addLine(VertexConsumer consumer, PoseStack.Pose pose, Matrix4f matrix,
                        float x1, float y1, float z1,
                        float x2, float y2, float z2, float opacity) {
        consumer.addVertex(matrix, x1, y1, z1)
                .setColor(1.0F, 1.0F, 0.8F, opacity)
                .setNormal(pose, 0, 1, 0);
        consumer.addVertex(matrix, x2, y2, z2)
                .setColor(1.0F, 1.0F, 0.8F, opacity)
                .setNormal(pose, 0, 1, 0);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull IgnivorusNovaOutlineEntity entity) {
        return TEXTURE;
    }
}
