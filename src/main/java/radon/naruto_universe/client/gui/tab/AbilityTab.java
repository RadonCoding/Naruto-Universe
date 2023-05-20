package radon.naruto_universe.client.gui.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.registries.RegistryObject;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.client.gui.NinjaScreen;
import radon.naruto_universe.client.gui.widget.AbilityWidget;

import java.util.HashMap;

public class AbilityTab extends NinjaTab implements NinjaTab.Tooltip, NinjaTab.Scrollable, NinjaTab.Clickable {
    private static final Component TITLE = Component.translatable("gui.tab.ability");
    private static final ResourceLocation ICON = new ResourceLocation(NarutoUniverse.MOD_ID, "textures/gui/tab/ability.png");

    protected int minX = Integer.MAX_VALUE;
    protected int minY = Integer.MAX_VALUE;
    protected int maxX = Integer.MIN_VALUE;
    protected int maxY = Integer.MIN_VALUE;

    protected double scrollX;
    protected double scrollY;
    protected boolean centered;

    protected float fade;

    private AbilityWidget root;

    private final HashMap<Ability, AbilityWidget> abilities = new HashMap<>();

    public AbilityTab(NinjaScreen screen, Minecraft mc, int index) {
        super(screen, mc, TITLE, ICON, NinjaTabType.ABOVE, index);

        for (RegistryObject<Ability> obj : NarutoAbilities.ABILITIES.getEntries()) {
            this.addAbility(obj.get());
        }
    }

    public void updateWidgets() {
        for (AbilityWidget widget : this.abilities.values()) {
            widget.update();
        }
    }

    @Override
    public void drawContents(PoseStack pPoseStack) {
        if (!this.centered) {
            this.scrollX = 117 - (this.maxX + this.minX) / 2.0F;
            this.scrollY = 56 - (this.maxY + this.minY) / 2.0F;
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
        fill(pPoseStack, WIDTH, HEIGHT, 0, 0, -16777216);
        RenderSystem.depthFunc(515);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);

        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);
        int k = i % 16;
        int l = j % 16;

        for(int i1 = -1; i1 <= 15; ++i1) {
            for(int j1 = -1; j1 <= 8; ++j1) {
                blit(pPoseStack, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }

        this.root.drawConnectivity(pPoseStack, i, j, true);
        this.root.drawConnectivity(pPoseStack, i, j, false);
        this.root.draw(pPoseStack, i, j);

        RenderSystem.depthFunc(518);
        pPoseStack.translate(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(pPoseStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthFunc(515);
        pPoseStack.popPose();
    }

    @Override
    public void mouseClicked(int pMouseX, int pMouseY) {
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);

        if (pMouseX > 0 && pMouseX < 234 && pMouseY > 0 && pMouseY < HEIGHT) {
            for (AbilityWidget widget : this.abilities.values()) {
                if (widget.isMouseOver(i, j, pMouseX, pMouseY)) {
                    widget.unlock();
                    break;
                }
            }
        }
    }

    @Override
    public void drawTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, 0.0D, -200.0D);
        fill(pPoseStack, 0, 0, WIDTH, HEIGHT, Mth.floor(this.fade * 255.0F) << 24);
        boolean hovered = false;
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);

        if (pMouseX > 0 && pMouseX < WIDTH && pMouseY > 0 && pMouseY < HEIGHT) {
            for (AbilityWidget widget : this.abilities.values()) {
                if (widget.isMouseOver(i, j, pMouseX, pMouseY)) {
                    hovered = true;
                    widget.drawHover(pPoseStack, i, j, this.fade, pOffsetX, pOffsetY);
                    break;
                }
            }
        }

        pPoseStack.popPose();

        if (hovered) {
            this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }

    @Override
    public void scroll(double pDragX, double pDragY) {
        if (this.maxX - this.minX > WIDTH) {
            this.scrollX = Mth.clamp(this.scrollX + pDragX, -(this.maxX - WIDTH), 0.0D);
        }

        if (this.maxY - this.minY > HEIGHT) {
            this.scrollY = Mth.clamp(this.scrollY + pDragY, -(this.maxY - HEIGHT), 0.0D);
        }
    }

    public AbilityWidget getAbility(Ability ability) {
        return this.abilities.get(ability);
    }

    public void addAbility(Ability ability) {
        if (ability.getDisplay(this.mc.player) != null) {
            AbilityWidget widget = new AbilityWidget(this, this.mc, ability);

            if (this.abilities.size() == 0) {
                this.root = widget;
            }

            this.abilities.put(ability, widget);

            int i = widget.getX();
            int j = i + 28;
            int k = widget.getY();
            int l = k + 27;

            this.minX = Math.min(this.minX, i);
            this.maxX = Math.max(this.maxX, j);
            this.minY = Math.min(this.minY, k);
            this.maxY = Math.max(this.maxY, l);

            for (AbilityWidget child : this.abilities.values()) {
                child.attachToParent();
            }
        }
    }
}
