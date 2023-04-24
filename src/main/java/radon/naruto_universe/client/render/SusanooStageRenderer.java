package radon.naruto_universe.client.render;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.naruto_universe.client.NarutoRenderTypes;
import radon.naruto_universe.entity.SusanooEntity;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SusanooStageRenderer extends GeoEntityRenderer<SusanooEntity> {
    public SusanooStageRenderer(EntityRendererProvider.Context renderManager, GeoModel<SusanooEntity> model) {
        super(renderManager, model);
    }

    @Override
    public Color getRenderColor(SusanooEntity animatable, float partialTick, int packedLight) {
        Vector3f color = animatable.getVariant().getSusanooColor();
        return Color.ofRGBA(color.x(), color.y(), color.z(), 0.5F);
    }

    @Override
    public RenderType getRenderType(SusanooEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return NarutoRenderTypes.glow(texture);
    }

    @Override
    protected int getBlockLightLevel(@NotNull SusanooEntity pEntity, @NotNull BlockPos pPos) {
        return 15;
    }
}
