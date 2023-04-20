package radon.naruto_universe.client.genjutsu;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.capability.ninja.ToggledEyes;
import radon.naruto_universe.client.NarutoRenderTypes;
import radon.naruto_universe.client.layer.NarutoEyesLayer;

import java.util.Random;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientGenjutsuHandler {
    private static GenjutsuInfo current;

    public static IGuiOverlay GENJUTSU_OVERLAY = (gui, poseStack, partialTicks, width, height) -> {
        if (current == null) return;

        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        if (mc.options.getCameraType().isFirstPerson()) {
            poseStack.pushPose();

            float scale = Math.min(1.0F, ((float) current.age / (float) current.lifetime) * 10.0F);

            float texWidth = 160.0F * scale;
            float texHeight = 36.0F * scale;
            float x1 = -texWidth;
            float y1 = -texHeight;
            float x2 = texWidth;
            float y2 = texHeight;

            float u1 = 232.0F / NarutoEyesLayer.TEXTURE_SIZE;
            float v1 = 306.0F / NarutoEyesLayer.TEXTURE_SIZE;
            float u2 = 392.0F / NarutoEyesLayer.TEXTURE_SIZE;
            float v2 = 342.0F / NarutoEyesLayer.TEXTURE_SIZE;

            Random rand = new Random();
            float x = rand.nextFloat() * 2.5F;
            float y = rand.nextFloat() * 2.5F;
            float z = rand.nextFloat() * 2.5F;
            poseStack.translate((width / 2.0F) + x, (height / 2.0F) + y, z);

            RenderType eyes = NarutoRenderTypes.eyes(NarutoEyesLayer.getTexture(current.eyes));

            if (NarutoEyesLayer.hasBackground(current.eyes, true)) {
                VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(NarutoEyesLayer.BACKGROUND);
                Matrix4f pose = poseStack.last().pose();
                consumer.vertex(pose, x1, y1, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(LightTexture.FULL_BLOCK).normal(0.0F, 0.0F, 1.0F).endVertex();
                consumer.vertex(pose, x1, y2, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(LightTexture.FULL_BLOCK).normal(0.0F, 0.0F, 1.0F).endVertex();
                consumer.vertex(pose, x2, y2, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(LightTexture.FULL_BLOCK).normal(0.0F, 0.0F, 1.0F).endVertex();
                consumer.vertex(pose, x2, y1, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(LightTexture.FULL_BLOCK).normal(0.0F, 0.0F, 1.0F).endVertex();
                mc.renderBuffers().bufferSource().endBatch(NarutoEyesLayer.BACKGROUND);
            }

            VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(eyes);
            Matrix4f pose = poseStack.last().pose();
            consumer.vertex(pose, x1, y1, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY).normal(0.0F, 0.0F, 1.0F).endVertex();
            consumer.vertex(pose, x1, y2, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u1, v2).overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY).normal(0.0F, 0.0F, 1.0F).endVertex();
            consumer.vertex(pose, x2, y2, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u2, v2).overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY).normal(0.0F, 0.0F, 1.0F).endVertex();
            consumer.vertex(pose, x2, y1, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u2, v1).overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY).normal(0.0F, 0.0F, 1.0F).endVertex();
            mc.renderBuffers().bufferSource().endBatch(eyes);

            poseStack.popPose();
        }

        current.age++;

        if (current.age >= current.lifetime) {
            current = null;
        }
    };

    public static void trigger(ToggledEyes eyes, int duration) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.level != null;

        if (current == null) {
            current = new GenjutsuInfo(eyes, duration);
        }
    }

    private static class GenjutsuInfo {
        private final ToggledEyes eyes;
        private final int lifetime;
        private int age;

        public GenjutsuInfo(ToggledEyes eyes, int lifetime) {
            this.eyes = eyes;
            this.lifetime = lifetime * 10;
        }
    }
}
