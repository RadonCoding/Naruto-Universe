package radon.naruto_universe.client.ability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.client.NarutoKeyMapping;

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

    public static Ability getSelected() {
        if (_abilities.size() > selected) {
            return _abilities.get(selected);
        }
        return null;
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
            int maxNameWidth = 0;
            int yOffset = 0;

            for (int i = selected - 1; i <= selected + 1; i++) {
                Ability ability = getAbility(i);

                if ((i == selected - 1 && _abilities.size() > 2) || (i == selected && _abilities.size() > 0) || (i == selected + 1 && _abilities.size() > 1)) {
                    maxNameWidth = Math.max(maxNameWidth, mc.font.width(ability.getName()));
                }
            }

            int screenWidth = mc.getWindow().getGuiScaledWidth();

            for (int i = selected - 1; i <= selected + 1; i++) {
                Ability ability = getAbility(i);

                if ((i == selected - 1 && _abilities.size() > 2) || (i == selected && _abilities.size() > 0) || (i == selected + 1 && _abilities.size() > 1)) {
                    int color = i == selected ? 16777215 : 2139062143;
                    Component name = ability.getName();
                    int x = screenWidth - maxNameWidth - 20 + (maxNameWidth - mc.font.width(name)) / 2;
                    int y = 20 + yOffset;
                    mc.font.drawShadow(poseStack, name, x, y, color);

                    yOffset += mc.font.lineHeight;
                }
            }
        }
    };
}
