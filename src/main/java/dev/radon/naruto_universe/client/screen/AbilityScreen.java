package dev.radon.naruto_universe.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.ability.Ability;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.registries.RegistryObject;

public class AbilityScreen extends Screen {
    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
    private static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");

    public static final int WINDOW_WIDTH = 252;
    public static final int WINDOW_HEIGHT = 140;
    private static final int WINDOW_INSIDE_X = 9;
    private static final int WINDOW_INSIDE_Y = 18;
    public static final int WINDOW_INSIDE_WIDTH = 234;
    public static final int WINDOW_INSIDE_HEIGHT = 113;
    private static final int WINDOW_TITLE_X = 8;
    private static final int WINDOW_TITLE_Y = 6;
    public static final int BACKGROUND_TILE_WIDTH = 16;
    public static final int BACKGROUND_TILE_HEIGHT = 16;
    public static final int BACKGROUND_TILE_COUNT_X = 14;
    public static final int BACKGROUND_TILE_COUNT_Y = 7;

    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;

    private double scrollX;
    private double scrollY;
    private boolean centered;

    private boolean isScrolling;

    private static final Component TITLE = Component.literal("Shinobi");

    public AbilityScreen() {
        super(TITLE);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        int i = (this.width - 252) / 2;
        int j = (this.height - 140) / 2;
        this.renderBackground(pPoseStack);
        this.renderInside(pPoseStack, pMouseX, pMouseY, i, j);
        this.renderWindow(pPoseStack, i, j);
    }

    private void renderInside(PoseStack pPoseStack, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY) {
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(pOffsetX + 9, pOffsetY + 18, 0.0D);
        RenderSystem.applyModelViewMatrix();

        if (!this.centered) {
            this.scrollX = 117 - (Integer.MAX_VALUE + Integer.MAX_VALUE) / 2;
            this.scrollY = 56 - (Integer.MAX_VALUE + Integer.MAX_VALUE) / 2;
            this.centered = true;
        }

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

        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);
        int k = i % 16;
        int l = j % 16;

        for (int i1 = -1; i1 <= 15; ++i1) {
            for (int j1 = -1; j1 <= 8; ++j1) {
                blit(pPoseStack, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }

        int x = 0;
        int y = 0;

        for (RegistryObject<Ability> obj : AbilityRegistry.ABILITIES.getEntries()) {
            Ability ability = obj.get();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            this.blit(pPoseStack, x + 4, y + 4, 0, 128, 26, 26);

            x += 26 + 4;

            if (x > this.width - 4) {
                y += 26 + 4;
            }
        }

        RenderSystem.depthFunc(518);
        pPoseStack.translate(0.0D, 0.0D, -950.0D);
        RenderSystem.colorMask(false, false, false, false);
        fill(pPoseStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthFunc(515);
        pPoseStack.popPose();

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
    }

    public void renderWindow(PoseStack pPoseStack, int pOffsetX, int pOffsetY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
        this.blit(pPoseStack, pOffsetX, pOffsetY, 0, 0, 252, 140);
        this.font.draw(pPoseStack, TITLE, (float)(pOffsetX + 8), (float)(pOffsetY + 6), 4210752);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else {
                this.scroll(pDragX, pDragY);
            }
            return true;
        }
    }

    public void scroll(double pDragX, double pDragY) {
        if (this.maxX - this.minX > 234) {
            this.scrollX = Mth.clamp(this.scrollX + pDragX, (double)(-(this.maxX - 234)), 0.0D);
        }

        if (this.maxY - this.minY > 113) {
            this.scrollY = Mth.clamp(this.scrollY + pDragY, (double)(-(this.maxY - 113)), 0.0D);
        }
    }
}
