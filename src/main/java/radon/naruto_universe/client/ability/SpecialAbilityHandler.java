package radon.naruto_universe.client.ability;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.apache.commons.compress.utils.Lists;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.TriggerAbilityC2SPacket;

import java.util.List;

public class SpecialAbilityHandler {
    private static int selected;
    private static final List<Ability> _abilities = Lists.newArrayList();

    public static boolean scroll(int direction) {
        final int i = -(int) Math.signum(direction);
        final int count = _abilities.size();

        if (count == 0) {
            return false;
        }

        selected += i;

        while (selected < 0) {
            selected += count;
        }

        while (selected >= count) {
            selected -= count;
        }
        return true;
    }

    public static void triggerSelectedAbility() {
        Ability ability = _abilities.get(selected);

        if (ability != null) {
            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(ability.getId()));
            ClientAbilityHandler.triggerAbility(ability);
        }
    }

    private static Ability getAbility(int idx) {
        final int count = _abilities.size();

        while (idx < 0) {
            idx += count;
        }

        while (idx >= count) {
            idx -= count;
        }
        return _abilities.get(idx);
    }

    private static void renderAbility(PoseStack poseStack, int yOffset, Font font, int idx, int color) {
        final Minecraft mc = Minecraft.getInstance();

        int screenWidth = mc.getWindow().getGuiScaledWidth();

        Ability ability = getAbility(idx);

        if (ability != null) {
            Component name = ability.getName();
            final int x = screenWidth - font.width(name) - 20;
            final int y = 20 + yOffset;
            font.drawShadow(poseStack, name, x, y, color);
        }
    }

    public static final IGuiOverlay SPECIAL_ABILITY = (gui, poseStack, partialTicks, width, height) -> {
        final Minecraft mc = gui.getMinecraft();
        LocalPlayer player = mc.player;

        assert player != null;

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            List<Ability> abilities = cap.getSpecialAbilities();

            if (_abilities.isEmpty() || !_abilities.equals(abilities)) {
                _abilities.clear();
                _abilities.addAll(cap.getSpecialAbilities());
            }
        });

        if (_abilities.size() > 0) {
            final Font font = gui.getFont();

            int y = 0;

            poseStack.pushPose();

            renderAbility(poseStack, y, font, selected - 1, 11513775);
            y += font.lineHeight;
            renderAbility(poseStack, y, font, selected, 16777215);
            y += font.lineHeight;
            renderAbility(poseStack, y, font, selected + 1, 11513775);

            poseStack.popPose();
        }
    };
}
