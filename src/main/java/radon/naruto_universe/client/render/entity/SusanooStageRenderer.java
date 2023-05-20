package radon.naruto_universe.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.naruto_universe.client.NarutoRenderTypes;
import radon.naruto_universe.entity.SusanooEntity;
import software.bernie.example.client.renderer.entity.GremlinRenderer;
import software.bernie.example.entity.DynamicExampleEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class SusanooStageRenderer extends GeoEntityRenderer<SusanooEntity> {
    public SusanooStageRenderer(EntityRendererProvider.Context renderManager, GeoModel<SusanooEntity> model) {
        super(renderManager, model);
    }

    @Override
    public Color getRenderColor(SusanooEntity animatable, float partialTick, int packedLight) {
        Vector3f color = animatable.getVariant().getSusanooColor();
        return Color.ofRGBA(color.x(), color.y(), color.z(), 0.75F);
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
