package dev.radon.naruto_universe.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.radon.naruto_universe.client.model.FireballModel;
import dev.radon.naruto_universe.entity.FireballEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class FireballRenderer extends GeoProjectilesRenderer<FireballEntity> {

    public FireballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireballModel());
    }

    @Override
    public void render(GeoModel model, FireballEntity animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.translate(0.0D, animatable.getBbHeight() / 2.0F, 0.0D);

        float time = (animatable.getTime() + partialTick) * 10.0F;
        float scaleTime = animatable.getScaleTime() * 10.0F;
        float scale = animatable.getEntitySize() * animatable.getScale(time, scaleTime);
        poseStack.scale(scale, scale, scale);

        super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected int getBlockLightLevel(FireballEntity pEntity, BlockPos pPos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(FireballEntity animatable) {
        return FireballModel.TEXTURE_LOCATION;
    }
}
