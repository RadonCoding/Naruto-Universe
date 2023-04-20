package radon.naruto_universe.client.gui.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
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
        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, 0.0D, 950.0D);
        RenderSystem.enableDepthTest();
        RenderSystem.colorMask(false, false, false, false);
        fill(pPoseStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        pPoseStack.translate(0.0D, 0.0D, -950.0D);
        RenderSystem.depthFunc(518);
        fill(pPoseStack, 234, 113, 0, 0, -16777216);
        RenderSystem.depthFunc(515);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);

        int k = WIDTH % 16;
        int l = HEIGHT % 16;

        for(int i1 = -1; i1 <= 15; ++i1) {
            for(int j1 = -1; j1 <= 8; ++j1) {
                blit(pPoseStack, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }

        LocalPlayer player = this.mc.player;

        assert player != null;

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            int i = 7;
            int j = 7;

            this.mc.font.drawShadow(pPoseStack, Component.literal("Name: ").append(player.getName()), i, j, 16777215);
            j += this.mc.font.lineHeight + 2;
            Component rank = Component.literal("Rank: ").append(cap.getRank().getIdentifier());
            this.mc.font.drawShadow(pPoseStack, rank, i, j, 16777215);
            j += this.mc.font.lineHeight + 2;
        });

        RenderSystem.depthFunc(518);
        pPoseStack.translate(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(pPoseStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthFunc(515);
        pPoseStack.popPose();
    }
}
