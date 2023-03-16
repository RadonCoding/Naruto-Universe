package radon.naruto_universe.client.gui.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import radon.naruto_universe.client.gui.NinjaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public abstract class NinjaTab extends GuiComponent {
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");

    public static final int WIDTH = 234;
    public static final int HEIGHT = 113;

    protected final NinjaScreen screen;
    protected final Minecraft mc;
    private final Component title;
    private final NinjaTabType type;
    private final ResourceLocation icon;
    private final int index;

    public final List<AbstractWidget> widgets = Lists.newArrayList();

    public NinjaTab(NinjaScreen screen, Minecraft mc, Component title, ResourceLocation icon, NinjaTabType type, int index) {
        this.screen = screen;
        this.mc = mc;
        this.title = title;
        this.icon = icon;
        this.type = type;
        this.index = index;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public Component getTitle() {
        return this.title;
    }

    public void drawTab(PoseStack pPoseStack, int pOffsetX, int pOffsetY, boolean pIsSelected) {
        this.type.draw(pPoseStack, this, pOffsetX, pOffsetY, pIsSelected, this.index);
    }

    public void drawIcon(PoseStack pPoseStack, int pOffsetX, int pOffsetY) {
        this.type.drawIcon(pPoseStack, this, this.icon, pOffsetX, pOffsetY, this.index);
    }

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

        RenderSystem.depthFunc(518);
        pPoseStack.translate(0.0D, 0.0D, -950.0D);
        RenderSystem.colorMask(false, false, false, false);
        fill(pPoseStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthFunc(515);
        pPoseStack.popPose();
    }

    public boolean isMouseOver(int pOffsetX, int pOffsetY, double pMouseX, double pMouseY) {
        return this.type.isMouseOver(pOffsetX, pOffsetY, pMouseX, pMouseY, this.index);
    }

    public interface Tooltip {
        void drawTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY);
    }

    public interface Scrollable {
        void scroll(double pDragX, double pDragY);
    }

    public interface Clickable {
        void mouseClicked(int pMouseX, int pMouseY);
    }
}
