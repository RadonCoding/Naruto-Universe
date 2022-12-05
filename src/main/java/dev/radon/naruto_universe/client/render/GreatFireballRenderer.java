package dev.radon.naruto_universe.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.radon.naruto_universe.client.model.GreatFireballModel;
import dev.radon.naruto_universe.entity.GreatFireballEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class GreatFireballRenderer extends GeoProjectilesRenderer<GreatFireballEntity> {

    public GreatFireballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GreatFireballModel());
    }

    @Override
    public void render(GeoModel model, GreatFireballEntity animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.translate(0.0D, animatable.getBbHeight() / 2.0F, 0.0D);

        float time = (animatable.getTime() + partialTick) * 10.0F;
        float scaleTime = GreatFireballEntity.SCALE_TIME * 10.0F;
        float scale = GreatFireballEntity.ENTITY_SIZE * animatable.getScale(time, scaleTime);
        poseStack.scale(scale, scale, scale);

        super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected int getBlockLightLevel(GreatFireballEntity pEntity, BlockPos pPos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(GreatFireballEntity animatable) {
        return GreatFireballModel.TEXTURE_LOCATION;
    }
}
