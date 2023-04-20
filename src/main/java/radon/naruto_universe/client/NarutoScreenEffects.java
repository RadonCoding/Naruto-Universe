package radon.naruto_universe.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import radon.naruto_universe.capability.data.NarutoDataHandler;

public class NarutoScreenEffects {
    public static void render(PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        mc.player.getCapability(NarutoDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.isLocalBurning()) {
                AmaterasuHandler.renderScreenEffect(poseStack);
            }
        });
    }
}
