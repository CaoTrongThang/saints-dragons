package com.leon.saintsdragons.client.renderer.nulljaw;

import com.leon.saintsdragons.client.model.nulljaw.NulljawModel;
import com.leon.saintsdragons.server.entity.dragons.nulljaw.Nulljaw;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class NulljawRenderer extends GeoEntityRenderer<Nulljaw> {
    private BakedGeoModel lastBakedModel;
    public NulljawRenderer(EntityRendererProvider.Context context) {
        super(context, new NulljawModel());
    }

    @Override
    public float getMotionAnimThreshold(Nulljaw animatable) {
        return 0.000001f;
    }

    @Override
    protected float getDeathMaxRotation(Nulljaw entity) {
        return 0.0F;
    }

    // preRender signature changed; rely on base renderer and handle tracking in render

    @Override
    public void render(@NotNull Nulljaw entity, float entityYaw, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        this.lastBakedModel = this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity));
        if (this.lastBakedModel != null) {
            enableTrackingForBones(this.lastBakedModel);
        }
        // Call normal rendering first
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        // After bones have been processed, sample accurate world positions for locators
        sampleAndStashLocatorsAccurate(entity);
    }

    private static final float PASSENGER_X = 0.0f, PASSENGER_Y = -3.0f, PASSENGER_Z = 0.0f;

    private void enableTrackingForBones(BakedGeoModel model) {
        if (model == null) return;
        model.getBone("passengerBone").ifPresent(b -> b.setTrackingMatrices(true));
    }

    private void sampleAndStashLocatorsAccurate(Nulljaw entity) {
        if (this.lastBakedModel == null || entity == null) return;

        this.lastBakedModel.getBone("passengerBone").ifPresent(b -> {
            net.minecraft.world.phys.Vec3 world = transformLocator(b, PASSENGER_X, PASSENGER_Y, PASSENGER_Z);
            if (world != null) entity.setClientLocatorPosition("passengerLocator", world);
        });
    }

    private static net.minecraft.world.phys.Vec3 transformLocator(software.bernie.geckolib.cache.object.GeoBone bone,
                                                                  float px, float py, float pz) {
        if (bone == null) return null;
        // Convert pixels to model units (blocks)
        float lx = px / 16f;
        float ly = py / 16f;
        float lz = pz / 16f;
        org.joml.Matrix4f worldMat = new org.joml.Matrix4f(bone.getWorldSpaceMatrix());
        org.joml.Vector4f in = new org.joml.Vector4f(lx, ly, lz, 1f);
        org.joml.Vector4f out = worldMat.transform(in);
        return new net.minecraft.world.phys.Vec3(out.x(), out.y(), out.z());
    }
}
