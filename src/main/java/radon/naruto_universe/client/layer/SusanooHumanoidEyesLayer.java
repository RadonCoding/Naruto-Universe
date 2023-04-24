package radon.naruto_universe.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.entity.SusanooEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SusanooHumanoidEyesLayer extends GeoRenderLayer<SusanooEntity> {
    private static final RenderType EYES = RenderType.eyes(new ResourceLocation(NarutoUniverse.MOD_ID,
            "textures/entity/susanoo_humanoid_eyes.png"));

    public SusanooHumanoidEyesLayer(GeoRenderer<SusanooEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, SusanooEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, EYES, bufferSource.getBuffer(EYES), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
