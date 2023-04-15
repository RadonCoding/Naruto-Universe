package radon.naruto_universe.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.client.model.ThrownKunaiModel;
import radon.naruto_universe.entity.ThrownKunaiEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ThrownKunaiRenderer extends GeoEntityRenderer<ThrownKunaiEntity> {
    public ThrownKunaiRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ThrownKunaiModel());
    }

    @Override
    public void render(ThrownKunaiEntity entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) - 90.0F));
        poseStack.translate(0.0D, -0.5D, 0.0D);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
