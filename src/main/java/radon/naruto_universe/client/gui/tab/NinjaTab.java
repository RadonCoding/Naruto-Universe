package radon.naruto_universe.client.gui.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import radon.naruto_universe.client.gui.NinjaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;

public abstract class NinjaTab extends GuiComponent {
    public static ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");

    public static int WIDTH = 234;
    public static int HEIGHT = 113;

    protected NinjaScreen screen;
    protected Minecraft mc;
    private final Component title;
    private final NinjaTabType type;
    private final ResourceLocation icon;
    private final int index;

    public List<AbstractWidget> widgets = new ArrayList<>();

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

    public abstract void drawContents(PoseStack pPoseStack);

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
