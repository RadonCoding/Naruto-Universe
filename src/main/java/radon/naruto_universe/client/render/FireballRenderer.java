package radon.naruto_universe.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.system.MathUtil;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.entity.EntityRegistry;
import radon.naruto_universe.entity.FireballEntity;
import radon.naruto_universe.util.HelperMethods;

public class FireballRenderer extends EntityRenderer<FireballEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/entity/fireball_2.png");

    public FireballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(FireballEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, pEntity.getBbHeight() / 2.0F, 0.0D);

        /*double d0 = pEntity.xOld + (pEntity.getX() - pEntity.xOld) * (double)pPartialTick;
        double d1 = pEntity.yOld + (pEntity.getY() - pEntity.yOld) * (double)pPartialTick;
        double d2 = pEntity.zOld + (pEntity.getZ() - pEntity.zOld) * (double)pPartialTick;

        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        pPoseStack.translate(d0 - cam.x, d1 - cam.y, d2 - cam.z);
        pPoseStack.translate(0.0F, 0.5F, 0.0F);*/

        Entity entity = Minecraft.getInstance().getCameraEntity();
        RenderType type = EntityRegistry.ModRenderType.fireball(TEXTURE);
        RenderSystem.disableCull();

        if (entity != null) {
            float yaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * pPartialTick;
            float pitch = entity.xRotO + (entity.getXRot() - entity.xRotO) * pPartialTick;
            HelperMethods.rotateQ(360.0F - yaw, 0.0F, 1.0F, 0.0F, pPoseStack);
            HelperMethods.rotateQ(pitch + 90.0F, 1.0F, 0.0F, 0.0F, pPoseStack);

            float growth = Math.min(pEntity.getTime() / FireballEntity.GROW_TIME, 1.0F);
            float scale = FireballEntity.INITIAL_SCALE + (growth * FireballEntity.GROW_SCALE);
            float size = pEntity.getSize() * scale;

            int j = 240;
            int k = 240;
            float bxx = -size;
            float yy = 0.0F;

            VertexConsumer bb = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(type);
            Matrix4f pose = pPoseStack.last().pose();
            bb.vertex(pose, bxx, yy, bxx).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F).uv2(j, k).endVertex();
            bb.vertex(pose, bxx, yy, size).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 1.0F).uv2(j, k).endVertex();
            bb.vertex(pose, size, yy, size).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 1.0F).uv2(j, k).endVertex();
            bb.vertex(pose, size, yy, bxx).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 0.0F).uv2(j, k).endVertex();

            Minecraft.getInstance().renderBuffers().bufferSource().endBatch(type);
        }
        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    protected int getBlockLightLevel(@NotNull FireballEntity pEntity, @NotNull BlockPos pPos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FireballEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
