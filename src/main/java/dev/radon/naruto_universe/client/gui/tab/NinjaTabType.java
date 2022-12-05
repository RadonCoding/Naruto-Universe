package dev.radon.naruto_universe.client.gui.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

public enum NinjaTabType {
    ABOVE(0, 0, 28, 32, 8),
    BELOW(84, 0, 28, 32, 8),
    LEFT(0, 64, 32, 28, 5),
    RIGHT(96, 64, 32, 28, 5);

    private final int textureX;
    private final int textureY;
    private final int width;
    private final int height;
    private final int max;

    NinjaTabType(int pTextureX, int pTextureY, int pWidth, int pHeight, int pMax) {
        this.textureX = pTextureX;
        this.textureY = pTextureY;
        this.width = pWidth;
        this.height = pHeight;
        this.max = pMax;
    }

    public void draw(PoseStack pPoseStack, GuiComponent pAbstractGui, int pOffsetX, int pOffsetY, boolean pIsSelected, int pIndex) {
        int i = this.textureX;

        if (pIndex > 0) {
            i += this.width;
        }

        if (pIndex == this.max - 1) {
            i += this.width;
        }

        int j = pIsSelected ? this.textureY + this.height : this.textureY;
        pAbstractGui.blit(pPoseStack, pOffsetX + this.getX(pIndex), pOffsetY + this.getY(pIndex), i, j, this.width, this.height);
    }

    public void drawIcon(PoseStack pPoseStack, GuiComponent pAbstractGui, ResourceLocation icon, int pOffsetX, int pOffsetY, int pIndex) {
        int i = pOffsetX + this.getX(pIndex);
        int j = pOffsetY + this.getY(pIndex);

        switch (this) {
            case ABOVE:
                i += 6;
                j += 9;
                break;
            case BELOW:
                i += 6;
                j += 6;
                break;
            case LEFT:
                i += 10;
                j += 5;
                break;
            case RIGHT:
                i += 6;
                j += 5;
        }
        RenderSystem.setShaderTexture(0, icon);
        pAbstractGui.blit(pPoseStack, i, j, 0, 0, 16, 16, 16, 16);
    }

    public int getX(int pIndex) {
        switch (this) {
            case ABOVE:
            case BELOW:
                return (this.width + 4) * pIndex;
            case LEFT:
                return -this.width + 4;
            case RIGHT:
                return 248;
            default:
                return 0;
        }
    }

    public int getY(int pIndex) {
        switch (this) {
            case ABOVE:
                return -this.height + 4;
            case BELOW:
                return 136;
            case LEFT:
            case RIGHT:
                return this.height * pIndex;
            default:
                return 0;
        }
    }

    public boolean isMouseOver(int pOffsetX, int pOffsetY, double pMouseX, double pMouseY, int pIndex) {
        int i = pOffsetX + this.getX(pIndex);
        int j = pOffsetY + this.getY(pIndex);
        return pMouseX > (double)i && pMouseX < (double)(i + this.width) && pMouseY > (double)j && pMouseY < (double)(j + this.height);
    }
}
