package radon.naruto_universe.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.capability.ninja.MangekyoType;
import radon.naruto_universe.client.NarutoRenderTypes;
import radon.naruto_universe.item.SusanooSwordItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SusanooSwordRenderer extends GeoItemRenderer<SusanooSwordItem> {
    public SusanooSwordRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(NarutoUniverse.MOD_ID, "susanoo_sword")));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public RenderType getRenderType(SusanooSwordItem animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return NarutoRenderTypes.glow(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, SusanooSwordItem animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.translate(0.0F, -0.51F, 0.1F);
    }

    @Override
    public Color getRenderColor(SusanooSwordItem animatable, float partialTick, int packedLight) {
        MangekyoType variant = SusanooSwordItem.getVariant(this.currentItemStack);
        if (variant == null) return super.getRenderColor(animatable, partialTick, packedLight);
        Vector3f color = variant.getSusanooColor();
        return Color.ofRGBA(color.x(), color.y(), color.z(), 0.25F);
    }
}
