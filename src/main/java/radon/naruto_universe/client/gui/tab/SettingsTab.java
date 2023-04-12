package radon.naruto_universe.client.gui.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
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

        super.drawContents(pPoseStack);

        RenderSystem.depthFunc(518);
        pPoseStack.translate(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(pPoseStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthFunc(515);
        pPoseStack.popPose();
    }
}
