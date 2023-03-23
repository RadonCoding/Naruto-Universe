package radon.naruto_universe.client.gui.tab;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.client.gui.NinjaScreen;
import radon.naruto_universe.client.gui.widget.AbilityWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

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

    private final Map<Ability, AbilityWidget> abilities = Maps.newLinkedHashMap();

    public AbilityTab(NinjaScreen screen, Minecraft mc, int index) {
        super(screen, mc, TITLE, ICON, NinjaTabType.ABOVE, index);

        for (RegistryObject<Ability> obj : AbilityRegistry.ABILITIES.getEntries()) {
            this.addAbility(obj.get());
        }
    }

    public void updateWidgets() {
        for (AbilityWidget widget : this.abilities.values()) {
            widget.update();
        }
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
    public void drawContents(PoseStack pPoseStack) {
        super.drawContents(pPoseStack);

        if (!this.centered) {
            this.scrollX = 117 - (this.maxX + this.minX) / 2.0F;
            this.scrollY = 56 - (this.maxY + this.minY) / 2.0F;
            this.centered = true;
        }

        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);

        this.root.drawConnectivity(pPoseStack, i, j, true);
        this.root.drawConnectivity(pPoseStack, i, j, false);
        this.root.draw(pPoseStack, i, j);
    }

    @Override
    public void drawTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int p, int pHeight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, 0.0D, -200.0D);
        fill(pPoseStack, 0, 0, 234, HEIGHT, Mth.floor(this.fade * 255.0F) << 24);
        boolean hovered = false;
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);

        if (pMouseX > 0 && pMouseX < 234 && pMouseY > 0 && pMouseY < HEIGHT) {
            for (AbilityWidget widget : this.abilities.values()) {
                if (widget.isMouseOver(i, j, pMouseX, pMouseY)) {
                    hovered = true;
                    widget.drawHover(pPoseStack, i, j, this.fade, p, pHeight);
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
        if (this.maxX - this.minX > 234) {
            this.scrollX = Mth.clamp(this.scrollX + pDragX, -(this.maxX - 234), 0.0D);
        }

        if (this.maxY - this.minY > HEIGHT) {
            this.scrollY = Mth.clamp(this.scrollY + pDragY, -(this.maxY - HEIGHT), 0.0D);
        }
    }

    public AbilityWidget getAbility(Ability ability) {
        return this.abilities.get(ability);
    }

    public void addAbility(Ability ability) {
        if (ability.getDisplay() != null) {
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
