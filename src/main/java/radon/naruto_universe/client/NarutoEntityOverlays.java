package radon.naruto_universe.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import radon.naruto_universe.capability.data.NarutoDataHandler;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.RequestNarutoDataC2SPacket;

public class NarutoEntityOverlays {
    public static void render(PoseStack poseStack, MultiBufferSource buffer, Entity entity) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        mc.player.getCapability(NarutoDataHandler.INSTANCE).ifPresent(cap -> {
            boolean remote = entity != mc.player;

            if (remote && !cap.isSynced(entity.getUUID())) {
                PacketHandler.sendToServer(new RequestNarutoDataC2SPacket(entity.getUUID()));
                return;
            }

            if (remote ? cap.isRemoteBurning(entity.getUUID()) : cap.isLocalBurning()) {
                AmaterasuHandler.renderEntityOverlay(poseStack, buffer, entity);
            }
        });
    }
}
