package dev.radon.naruto_universe.client.gui.tab;

import dev.radon.naruto_universe.client.gui.NinjaScreen;
import dev.radon.naruto_universe.NarutoUniverse;
import dev.radon.naruto_universe.client.gui.widget.PitchSlider;
import dev.radon.naruto_universe.capability.NinjaPlayerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SettingsTab extends NinjaTab {
    private static final Component TITLE = Component.translatable("gui.tab.settings");
    private static final ResourceLocation ICON = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/gui/tab/settings.png");

    public SettingsTab(NinjaScreen screen, Minecraft mc, int index) {
        super(screen, mc, TITLE, ICON, NinjaTabType.ABOVE, index);

        this.mc.player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            int i = (screen.width - NinjaScreen.WINDOW_WIDTH) / 2;
            int j = (screen.height - NinjaScreen.WINDOW_HEIGHT) / 2;
            i += 16;
            j += 26;
            widgets.add(this.screen.addTabWidget(this, new PitchSlider(i, j, 220, 20, cap.getVoicePitch())));
        });
    }
}
