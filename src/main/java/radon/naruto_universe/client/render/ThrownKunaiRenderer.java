package radon.naruto_universe.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.client.model.ThrownKunaiModel;
import radon.naruto_universe.entity.ThrownKunaiEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ThrownKunaiRenderer extends EntityRenderer<ThrownKunaiEntity> {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/models/entity/thrown_kunai.png");

    private final ThrownKunaiModel model;

    public ThrownKunaiRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new ThrownKunaiModel(pContext.bakeLayer(ThrownKunaiModel.LAYER_LOCATION));
    }

    @Override
    public void render(ThrownKunaiEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot()) + 90.0F));
        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(pBuffer, this.model.renderType(this.getTextureLocation(pEntity)), false, false);
        this.model.renderToBuffer(pPoseStack, consumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownKunaiEntity animatable) {
        return TEXTURE_LOCATION;
    }
}
