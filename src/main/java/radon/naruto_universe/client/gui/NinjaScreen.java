package radon.naruto_universe.client.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.client.gui.tab.AbilityTab;
import radon.naruto_universe.client.gui.tab.InfoTab;
import radon.naruto_universe.client.gui.tab.NinjaTab;

import java.util.ArrayList;
import java.util.List;

public class NinjaScreen extends Screen {
    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
    private static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");

    public static int WINDOW_WIDTH = 252;
    public static int WINDOW_HEIGHT = 140;
    
    private static final Component TITLE = Component.translatable("gui.ninja");

    private boolean isScrolling;

    private final List<NinjaTab> tabs = new ArrayList<>();
    private NinjaTab selectedTab;

    public NinjaScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        this.tabs.clear();

        int index = 0;
        this.tabs.add(new InfoTab(this, this.minecraft, index++));
        this.tabs.add(new AbilityTab(this, this.minecraft, index++));
        this.setTab(this.tabs.get(0));
    }

    private void setTab(NinjaTab newTab) {
        this.selectedTab = newTab;

        for (NinjaTab tab : this.tabs) {
            for (AbstractWidget widget : tab.widgets) {
                widget.active = this.selectedTab == tab;
                widget.visible = this.selectedTab == tab;
            }
        }
    }

    public void updateAbilities() {
        for (NinjaTab tab : this.tabs) {
            if (tab instanceof AbilityTab abilityTab) {
                abilityTab.updateWidgets();
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            int i = (this.width - WINDOW_WIDTH) / 2;
            int j = (this.height - WINDOW_HEIGHT) / 2;

            for (NinjaTab tab : this.tabs) {
                if (tab.isMouseOver(i, j, pMouseX, pMouseY)) {
                    this.setTab(tab);
                    break;
                }
            }

            if (this.selectedTab instanceof NinjaTab.Clickable clickableTab) {
                clickableTab.mouseClicked((int) pMouseX - i - 9, (int) pMouseY - j - 18);
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        int i = (this.width - WINDOW_WIDTH) / 2;
        int j = (this.height - WINDOW_HEIGHT) / 2;
        this.renderBackground(pPoseStack);
        this.renderInside(pPoseStack, i, j);
        this.renderWindow(pPoseStack, i, j);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltips(pPoseStack, pMouseX, pMouseY, i, j);
    }

    private void renderInside(PoseStack pPoseStack, int pOffsetX, int pOffsetY) {
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate(pOffsetX + 9, pOffsetY + 18, 0.0D);
        RenderSystem.applyModelViewMatrix();

        this.selectedTab.drawContents(pPoseStack);

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
    }

    public void renderWindow(PoseStack pPoseStack, int pOffsetX, int pOffsetY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
        this.blit(pPoseStack, pOffsetX, pOffsetY, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        if (this.tabs.size() > 0) {
            RenderSystem.setShaderTexture(0, TABS_LOCATION);

            for (NinjaTab tab : this.tabs) {
                tab.drawTab(pPoseStack, pOffsetX, pOffsetY, tab == this.selectedTab);
            }

            RenderSystem.defaultBlendFunc();

            for (NinjaTab tab : this.tabs) {
                tab.drawIcon(pPoseStack, pOffsetX, pOffsetY);
            }

            RenderSystem.disableBlend();
        }
        this.font.draw(pPoseStack, TITLE, (float)(pOffsetX + 8), (float)(pOffsetY + 6), 4210752);
    }

    private void renderTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.selectedTab != null && this.selectedTab instanceof NinjaTab.Tooltip tooltipTab) {
            PoseStack posestack = RenderSystem.getModelViewStack();
            posestack.pushPose();
            posestack.translate(pOffsetX + 9, pOffsetY + 18, 400.0D);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.enableDepthTest();

            tooltipTab.drawTooltips(pPoseStack, pMouseX - pOffsetX - 9, pMouseY - pOffsetY - 18, pOffsetX, pOffsetY);

            RenderSystem.disableDepthTest();
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
        }

        if (this.tabs.size() > 1) {
            for (NinjaTab tab : this.tabs) {
                if (tab.isMouseOver(pOffsetX, pOffsetY, pMouseX, pMouseY)) {
                    this.renderTooltip(pPoseStack, tab.getTitle(), pMouseX, pMouseY);
                }
            }
        }
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else if (this.selectedTab != null && this.selectedTab instanceof NinjaTab.Scrollable scrollableTab) {
                scrollableTab.scroll(pDragX, pDragY);
            }
            return true;
        }
    }
}
