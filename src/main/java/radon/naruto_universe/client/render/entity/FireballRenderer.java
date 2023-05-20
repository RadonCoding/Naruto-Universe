package radon.naruto_universe.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.naruto_universe.client.NarutoRenderTypes;
import radon.naruto_universe.client.model.FireballModel;
import radon.naruto_universe.entity.FireballProjectile;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireballRenderer extends GeoEntityRenderer<FireballProjectile> {
    public FireballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireballModel());
    }

    @Override
    public void render(@NotNull FireballProjectile entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(entity.getSize(), entity.getSize(), entity.getSize());
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public RenderType getRenderType(FireballProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return NarutoRenderTypes.glow(texture);
    }
}
