package radon.naruto_universe.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.entity.EntityRegistry;
import radon.naruto_universe.entity.FireballJutsuEntity;
import radon.naruto_universe.util.HelperMethods;

public class FireballRenderer extends EntityRenderer<FireballJutsuEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/entity/fireball.png");
    private static final RenderType RENDER_TYPE = EntityRegistry.ModRenderType.glow(TEXTURE);

    public FireballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(FireballJutsuEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, pEntity.getBbHeight() / 2.0F, 0.0D);

        Minecraft mc = Minecraft.getInstance();

        Entity entity = mc.getCameraEntity();

        if (entity != null) {
            float yaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * pPartialTick;
            float pitch = entity.xRotO + (entity.getXRot() - entity.xRotO) * pPartialTick;
            HelperMethods.rotateQ(360.0F - yaw, 0.0F, 1.0F, 0.0F, pPoseStack);
            HelperMethods.rotateQ(pitch + 90.0F, 1.0F, 0.0F, 0.0F, pPoseStack);

            float time = (pEntity.getTime() + pPartialTick) * 10.0F;
            float scaleTime = FireballJutsuEntity.SCALE_TIME * 10.0F;
            float scale = pEntity.getSize() * Math.min(FireballJutsuEntity.INITIAL_SCALE + (FireballJutsuEntity.SCALAR - (FireballJutsuEntity.SCALAR * ((scaleTime - time) / scaleTime))), 1.0F);

            int j = 240;
            int k = 240;
            float bxx = -scale;
            float yy = 0.0F;

            VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(RENDER_TYPE);
            Matrix4f pose = pPoseStack.last().pose();
            consumer.vertex(pose, bxx, yy, bxx).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F).uv2(j, k).endVertex();
            consumer.vertex(pose, bxx, yy, scale).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 1.0F).uv2(j, k).endVertex();
            consumer.vertex(pose, scale, yy, scale).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 1.0F).uv2(j, k).endVertex();
            consumer.vertex(pose, scale, yy, bxx).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 0.0F).uv2(j, k).endVertex();
            mc.renderBuffers().bufferSource().endBatch(RENDER_TYPE);
        }
        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    protected int getBlockLightLevel(@NotNull FireballJutsuEntity pEntity, @NotNull BlockPos pPos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FireballJutsuEntity pEntity) {
        return null;
    }
}
