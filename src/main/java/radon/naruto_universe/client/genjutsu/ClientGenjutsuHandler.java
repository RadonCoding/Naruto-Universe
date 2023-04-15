package radon.naruto_universe.client.genjutsu;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;

import java.util.concurrent.LinkedBlockingQueue;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientGenjutsuHandler {
    private static final LinkedBlockingQueue<GenjutsuInfo> genjutsus = new LinkedBlockingQueue<>();

    public static IGuiOverlay GENJUTSU_OVERLAY = (gui, poseStack, partialTicks, width, height) -> {
        if (genjutsus.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        /*if (mc.options.getCameraType() != CameraType.FIRST_PERSON) {
            return;
        }*/

        GenjutsuInfo info = genjutsus.peek();

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        LivingEntityRenderer<? super LivingEntity, ?> renderer = (LivingEntityRenderer<? super LivingEntity, ?>) dispatcher.getRenderer(info.entity);

        ResourceLocation texture = renderer.getTextureLocation(info.entity);
        EntityModel<?> model = renderer.getModel();

        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(0.0F, -2.0F, 0.5F);

        VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(model.renderType(texture));
        model.renderToBuffer(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();

        info.age++;

        if (info.age >= info.lifetime) {
            genjutsus.remove();
        }
    };


    public static void trigger(int entityId, int duration) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.level != null;

        LivingEntity entity = (LivingEntity) mc.level.getEntity(entityId);
        genjutsus.add(new GenjutsuInfo(entity, duration));
    }

    private static class GenjutsuInfo {
        private final LivingEntity entity;
        private final int lifetime;
        private int age;

        public GenjutsuInfo(LivingEntity entity, int lifetime) {
            this.entity = entity;
            this.lifetime = lifetime;
        }
    }
}
