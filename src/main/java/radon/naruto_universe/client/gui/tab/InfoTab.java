package radon.naruto_universe.client.gui.tab;

import com.mojang.blaze3d.vertex.PoseStack;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.client.gui.NinjaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class InfoTab extends NinjaTab{
    private static final Component TITLE = Component.translatable("gui.tab.info");
    private static final ResourceLocation ICON = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/gui/tab/info.png");

    public InfoTab(NinjaScreen screen, Minecraft mc, int index) {
        super(screen, mc, TITLE, ICON, NinjaTabType.ABOVE, index);
    }

    @Override
    public void drawContents(PoseStack pPoseStack) {
        super.drawContents(pPoseStack);

        LocalPlayer player = this.mc.player;

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            int i = 7;
            int j = 7;

            this.mc.font.drawShadow(pPoseStack, Component.literal("Name: ").append(player.getName()), i, j, 16777215);
            j += this.mc.font.lineHeight + 2;
            Component rank = Component.literal("Rank: ").append(cap.getRank().getIdentifier());
            this.mc.font.drawShadow(pPoseStack, rank, i, j, 16777215);
            j += this.mc.font.lineHeight + 2;
            Component clan = Component.literal("Clan: ").append(cap.getClan().getIdentifier());
            this.mc.font.drawShadow(pPoseStack, clan, i, j, 16777215);
        });
    }
}
