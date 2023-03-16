package radon.naruto_universe.client.gui.tab;

import radon.naruto_universe.client.gui.NinjaScreen;
import radon.naruto_universe.NarutoUniverse;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SettingsTab extends NinjaTab {
    private static final Component TITLE = Component.translatable("gui.tab.settings");
    private static final ResourceLocation ICON = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/gui/tab/settings.png");

    public SettingsTab(NinjaScreen screen, Minecraft mc, int index) {
        super(screen, mc, TITLE, ICON, NinjaTabType.ABOVE, index);
    }
}
