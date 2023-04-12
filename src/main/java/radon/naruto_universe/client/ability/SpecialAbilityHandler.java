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

import java.util.ArrayList;
import java.util.List;

public class SpecialAbilityHandler {
    private static int selected;
    private static final List<Ability> _abilities = new ArrayList<>();

    public static boolean scroll(int direction) {
        int i = -(int) Math.signum(direction);
        int count = _abilities.size();

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
        if (_abilities.size() > selected) {
            Ability ability = _abilities.get(selected);
            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(ability.getId()));
            ClientAbilityHandler.triggerAbility(ability);
        }
    }

    private static Ability getAbility(int idx) {
        int count = _abilities.size();

        while (idx < 0) {
            idx += count;
        }

        while (idx >= count) {
            idx -= count;
        }
        return _abilities.get(idx);
    }

    private static void renderAbility(PoseStack poseStack, int yOffset, Font font, int idx, int color) {
        Minecraft mc = Minecraft.getInstance();

        int screenWidth = mc.getWindow().getGuiScaledWidth();

        Ability ability = getAbility(idx);

        if (ability != null) {
            Component name = ability.getName();
            int x = screenWidth - font.width(name) - 20;
            int y = 20 + yOffset;
            font.drawShadow(poseStack, name, x, y, color);
        }
    }

    public static IGuiOverlay SPECIAL_ABILITY = (gui, poseStack, partialTicks, width, height) -> {
        Minecraft mc = gui.getMinecraft();
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
            Font font = gui.getFont();

            int y = 0;

            poseStack.pushPose();

            if (_abilities.size() > 2) {
                renderAbility(poseStack, y, font, selected - 1, 11513775);
                y += font.lineHeight;
            }
            if (_abilities.size() > 0) {
                renderAbility(poseStack, y, font, selected, 16777215);
                y += font.lineHeight;
            }
            if (_abilities.size() > 1) {
                renderAbility(poseStack, y, font, selected + 1, 11513775);
                y += font.lineHeight;
            }
            poseStack.popPose();
        }
    };
}
