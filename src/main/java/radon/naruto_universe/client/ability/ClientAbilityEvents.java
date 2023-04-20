package radon.naruto_universe.client.ability;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.effect.NarutoEffects;
import radon.naruto_universe.entity.SusanooEntity;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.ChangeSusanooStageC2SPacket;
import radon.naruto_universe.network.packet.SusanooControlC2SPacket;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientAbilityEvents {
    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        if (mc.player.hasEffect(NarutoEffects.STUN.get())) {
            mc.player.input.forwardImpulse = 0.0F;
            mc.player.input.leftImpulse = 0.0F;
            mc.player.input.jumping = false;
            mc.player.input.shiftKeyDown = false;
        }
    }

    @SubscribeEvent
    public static void onPlayerMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null) {
            if (mc.player.hasEffect(NarutoEffects.STUN.get())) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
            else if (mc.player.getVehicle() instanceof SusanooEntity susanoo) {
                if (event.isAttack()) {
                    PacketHandler.sendToServer(new SusanooControlC2SPacket(SusanooControlC2SPacket.LEFT));
                    susanoo.onLeftClick();

                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
                else if (event.isUseItem() && mc.player.getMainHandItem().isEmpty() && mc.player.getOffhandItem().isEmpty()) {
                    PacketHandler.sendToServer(new SusanooControlC2SPacket(SusanooControlC2SPacket.RIGHT));
                    susanoo.onRightClick();

                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        if (!mc.options.keyShift.isDown()) {
            if (mc.player.getVehicle() instanceof SusanooEntity susanoo) {
                if (event.getKey() == InputConstants.KEY_UP) {
                    PacketHandler.sendToServer(new ChangeSusanooStageC2SPacket(ChangeSusanooStageC2SPacket.UP));
                    susanoo.incrementStage();
                } else if (event.getKey() == InputConstants.KEY_DOWN) {
                    PacketHandler.sendToServer(new ChangeSusanooStageC2SPacket(ChangeSusanooStageC2SPacket.DOWN));
                    susanoo.decrementStage();
                }
            }
        }
    }

    private static void renderBowTrajectory(PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) {
            return;
        }

        Vec3 aimVector = mc.player.getLookAngle().normalize().scale(50.0);

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        double px = mc.player.getX();
        double py = mc.player.getY() + mc.player.getEyeHeight();
        double pz = mc.player.getZ();

        Vec3 result = new Vec3(px, py, pz).add(aimVector);

        VertexConsumer consumer = buffer.getBuffer(RenderType.lines());
        Matrix4f pose = poseStack.last().pose();
        consumer.vertex(pose, (float) px, (float) py, (float) pz).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, 0.0F, -1.0F).endVertex();
        consumer.vertex(pose, (float) result.x(), (float) result.y(), (float) result.z()).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, 0.0F, -1.0F).endVertex();
        buffer.endBatch(RenderType.lines());
    }


    @SubscribeEvent
    public static void onRenderGuiOverlayPost(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()) {
            Minecraft mc = Minecraft.getInstance();

            assert mc.player != null;

            ItemStack heldItem = mc.player.getMainHandItem();

            if (heldItem.getItem() instanceof BowItem || heldItem.getItem() instanceof TridentItem) {
                renderBowTrajectory(event.getPoseStack());
            }
        }
    }
}
