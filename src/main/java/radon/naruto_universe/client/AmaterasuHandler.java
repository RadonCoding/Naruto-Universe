package radon.naruto_universe.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import radon.naruto_universe.NarutoUniverse;

public class AmaterasuHandler {
    public static final Material AMATERASU_0 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(NarutoUniverse.MOD_ID, "block/amaterasu_0"));
    public static final Material AMATERASU_1 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(NarutoUniverse.MOD_ID, "block/amaterasu_1"));

    private static void fireVertex(PoseStack.Pose pMatrixEntry, VertexConsumer pBuffer, float pX, float pY, float pZ, float pTexU, float pTexV) {
        pBuffer.vertex(pMatrixEntry.pose(), pX, pY, pZ).color(255, 255, 255, 255).uv(pTexU, pTexV).overlayCoords(0, 10).uv2(240).normal(pMatrixEntry.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }

    public static void renderEntityOverlay(PoseStack poseStack, MultiBufferSource buffer, Entity entity) {
        TextureAtlasSprite texture0 = AMATERASU_0.sprite();
        TextureAtlasSprite texture1 = AMATERASU_1.sprite();
        poseStack.pushPose();
        float f = entity.getBbWidth() * 1.4F;
        poseStack.scale(f, f, f);
        float f1 = 0.5F;
        float f2 = entity.getBbHeight() / f;
        float f3 = 0.0F;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.translate(0.0F, 0.0F, -0.3F + (float)((int)f2) * 0.02F);
        float f4 = 0.0F;
        int i = 0;
        VertexConsumer vertexconsumer = buffer.getBuffer(Sheets.cutoutBlockSheet());

        for (PoseStack.Pose pose = poseStack.last(); f2 > 0.0F; ++i) {
            TextureAtlasSprite texture = i % 2 == 0 ? texture0 : texture1;
            float f5 = texture.getU0();
            float f6 = texture.getV0();
            float f7 = texture.getU1();
            float f8 = texture.getV1();

            if (i / 2 % 2 == 0) {
                float f10 = f7;
                f7 = f5;
                f5 = f10;
            }

            fireVertex(pose, vertexconsumer, f1 - 0.0F, 0.0F - f3, f4, f7, f8);
            fireVertex(pose, vertexconsumer, -f1 - 0.0F, 0.0F - f3, f4, f5, f8);
            fireVertex(pose, vertexconsumer, -f1 - 0.0F, 1.4F - f3, f4, f5, f6);
            fireVertex(pose, vertexconsumer, f1 - 0.0F, 1.4F - f3, f4, f7, f6);
            f2 -= 0.45F;
            f3 -= 0.45F;
            f1 *= 0.9F;
            f4 += 0.03F;
        }

        poseStack.popPose();
    }

    public static void renderScreenEffect(PoseStack poseStack) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        TextureAtlasSprite texture = AMATERASU_1.sprite();
        RenderSystem.setShaderTexture(0, texture.atlasLocation());
        float f = texture.getU0();
        float f1 = texture.getU1();
        float f2 = (f + f1) / 2.0F;
        float f3 = texture.getV0();
        float f4 = texture.getV1();
        float f5 = (f3 + f4) / 2.0F;
        float f6 = texture.uvShrinkRatio();
        float f7 = Mth.lerp(f6, f, f2);
        float f8 = Mth.lerp(f6, f1, f2);
        float f9 = Mth.lerp(f6, f3, f5);
        float f10 = Mth.lerp(f6, f4, f5);

        for(int i = 0; i < 2; ++i) {
            poseStack.pushPose();
            poseStack.translate((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees((float)(i * 2 - 1) * 10.0F));
            Matrix4f matrix4f = poseStack.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
            bufferbuilder.vertex(matrix4f, -0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f8, f10).endVertex();
            bufferbuilder.vertex(matrix4f, 0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f7, f10).endVertex();
            bufferbuilder.vertex(matrix4f, 0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f7, f9).endVertex();
            bufferbuilder.vertex(matrix4f, -0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f8, f9).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
            poseStack.popPose();
        }

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }
}
