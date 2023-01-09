package dev.radon.naruto_universe.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.capability.NinjaTrait;
import dev.radon.naruto_universe.client.gui.tab.AbilityTab;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.network.packet.UnlockAbilityC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class AbilityWidget extends GuiComponent {
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
    private static final int[] TEST_SPLIT_OFFSETS = new int[] { 0, 10, -10, 25, -25 };

    private final Ability ability;

    private AbilityWidget parent;
    private final List<AbilityWidget> children = Lists.newArrayList();

    private final int x;
    private final int y;

    private final Minecraft mc;

    private final AbilityTab tab;

    private final FormattedCharSequence title;
    private final List<FormattedCharSequence> description;

    private final int width;
    private boolean unlockable;
    private boolean unlocked;
    private AbilityFrameType frame;
    private final AbilityDisplayInfo display;

    public AbilityWidget(AbilityTab tab, Minecraft mc, Ability ability) {
        this.tab = tab;
        this.ability = ability;
        this.display = ability.getDisplay();
        this.x = Mth.floor(this.display.getX() * 28.0F);
        this.y = Mth.floor(this.display.getY() * 27.0F);
        this.mc = mc;

        this.title = Language.getInstance().getVisualOrder(this.mc.font.substrByWidth(ability.getName(), 163));
        int len = 29 + this.mc.font.width(this.title);

        MutableComponent component = this.ability.getDescription().copy();
        component.append("\n");
        component.append("\n");

        component.append(Component.literal("Difficulty: "));
        component.append(this.ability.getRank().getIdentifier());
        component.append("\n");
        component.append("\n");

        List<NinjaTrait> requirements = this.ability.getRequirements();

        component.append(Component.literal("Requirements: "));
        component.append("\n");

        if (!requirements.isEmpty()) {
            for (NinjaTrait requirement : requirements) {
                component.append(requirement.getIdentifier());
            }
        }
        else {
            component.append("None");
        }
        component.append("\n");
        component.append("\n");

        component.append(Component.literal("Combo: "));
        component.append(AbilityRegistry.getStringFromCombo(this.ability.getCombo()));

        this.description = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(component.copy(),
                Style.EMPTY), len));

        for (FormattedCharSequence sequence : this.description) {
            len = Math.max(len, this.mc.font.width(sequence));
        }

        this.width = len + 3 + 5;

        this.update();
    }

    public void update() {
        this.unlockable = this.ability.isUnlockable(this.mc.player);
        this.unlocked = this.ability.isUnlocked(this.mc.player);
        this.frame = this.unlockable ? (this.unlocked ? AbilityFrameType.UNLOCKED : AbilityFrameType.UNLOCKABLE) : AbilityFrameType.NORMAL;
    }

    private List<FormattedText> findOptimalLines(Component pComponent, int pMaxWidth) {
        StringSplitter stringsplitter = this.mc.font.getSplitter();
        List<FormattedText> list = null;
        float f = Float.MAX_VALUE;

        for (int i : TEST_SPLIT_OFFSETS) {
            List<FormattedText> list1 = stringsplitter.splitLines(pComponent, pMaxWidth - i, Style.EMPTY);
            float f1 = Math.abs(this.getMaxWidth(stringsplitter, list1) - (float)pMaxWidth);

            if (f1 <= 10.0F) {
                return list1;
            }

            if (f1 < f) {
                f = f1;
                list = list1;
            }
        }

        return list;
    }

    private static float getMaxWidth(StringSplitter pManager, List<FormattedText> pText) {
        return (float)pText.stream().mapToDouble(pManager::stringWidth).max().orElse(0.0D);
    }

    public void drawConnectivity(PoseStack pPoseStack, int i, int j, boolean pDropShadow) {
        if (this.parent != null) {
            int k = i + this.parent.x + 13;
            int l = i + this.parent.x + 26 + 4;
            int i2 = j + this.parent.y + 13;
            int j2 = i + this.x + 13;
            int k2 = j + this.y + 13;
            int l2 = pDropShadow ? -16777216 : -1;

            if (pDropShadow) {
                this.hLine(pPoseStack, l, k, i2 - 1, l2);
                this.hLine(pPoseStack, l + 1, k, i2, l2);
                this.hLine(pPoseStack, l, k, i2 + 1, l2);
                this.hLine(pPoseStack, j2, l - 1, k2 - 1, l2);
                this.hLine(pPoseStack, j2, l - 1, k2, l2);
                this.hLine(pPoseStack, j2, l - 1, k2 + 1, l2);
                this.vLine(pPoseStack, l - 1, k2, i2, l2);
                this.vLine(pPoseStack, l + 1, k2, i2, l2);
            } else {
                this.hLine(pPoseStack, l, k, i2, l2);
                this.hLine(pPoseStack, j2, l, k2, l2);
                this.vLine(pPoseStack, l, k2, i2, l2);
            }
        }

        for (AbilityWidget child : this.children) {
            child.drawConnectivity(pPoseStack, i, j, pDropShadow);
        }
    }

    public void draw(PoseStack pPoseStack, int i, int j) {
        AbilityWidgetType type;

        if (this.unlocked) {
            type = AbilityWidgetType.OBTAINED;
        } else {
            type = AbilityWidgetType.UNOBTAINED;
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        this.blit(pPoseStack, i + this.x + 3, j + this.y,
                this.frame.getTexture(), 128 + type.getIndex() * 26, 26, 26);
        RenderSystem.setShaderTexture(0, this.display.getIcon());
        this.blit(pPoseStack, i + this.x + 8, j + this.y + 5,
                0, 0, 16, 16, 16, 16);

        for (AbilityWidget child : this.children) {
            child.draw(pPoseStack, i, j);
        }
    }

    public boolean isMouseOver(int pX, int pY, int pMouseX, int pMouseY) {
        int i = pX + this.x;
        int j = i + 26;
        int k = pY + this.y;
        int l = k + 26;
        return pMouseX >= i && pMouseX <= j && pMouseY >= k && pMouseY <= l;
    }

    public void unlock() {
        if (this.unlockable && !this.unlocked) {
            this.mc.player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);
            PacketHandler.sendToServer(new UnlockAbilityC2SPacket(AbilityRegistry.getKey(this.ability)));
            this.update();
        }
    }

    public void drawHover(PoseStack pPoseStack, int pX, int pY, float pFade, int pWidth, int pHeight) {
        boolean drawLeft = pWidth + pX + this.x + this.width + 26 >= this.tab.getScreen().width;
        boolean newLine = 113 - pY - this.y - 26 <= 6 + this.description.size() * 9;

        int j = this.unlockable ? this.width : 0;

        AbilityWidgetType type1;
        AbilityWidgetType type2;
        AbilityWidgetType type3;

        if (this.unlocked) {
            j = this.width / 2;
            type1 = AbilityWidgetType.OBTAINED;
            type2 = AbilityWidgetType.OBTAINED;
            type3 = AbilityWidgetType.OBTAINED;
        } else if (this.unlockable) {
            type1 = AbilityWidgetType.OBTAINED;
            type2 = AbilityWidgetType.UNOBTAINED;
            type3 = AbilityWidgetType.UNOBTAINED;
        } else {
            j = this.width / 2;
            type1 = AbilityWidgetType.UNOBTAINED;
            type2 = AbilityWidgetType.UNOBTAINED;
            type3 = AbilityWidgetType.UNOBTAINED;
        }

        int k = this.width - j;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        int l = pY + this.y;
        int i1;

        if (drawLeft) {
            i1 = pX + this.x - this.width + 26 + 6;
        } else {
            i1 = pX + this.x;
        }

        int j1 = 32 + this.description.size() * 9;

        if (newLine) {
            this.render9Sprite(pPoseStack, i1, l + 26 - j1, this.width, j1, 10, 200, 26, 0, 52);
        } else {
            this.render9Sprite(pPoseStack, i1, l, this.width, j1, 10, 200, 26, 0, 52);
        }

        this.blit(pPoseStack, i1, l, 0, type1.getIndex() * 26, j, 26);
        this.blit(pPoseStack, i1 + j, l, 200 - k, type2.getIndex() * 26, k, 26);
        this.blit(pPoseStack, pX + this.x + 3, pY + this.y, this.frame.getTexture(), 128 + type3.getIndex() * 26, 26, 26);

        if (drawLeft) {
            this.mc.font.drawShadow(pPoseStack, this.title, (float)(i1 + 5), (float)(pY + this.y + 9), -1);
        } else {
            this.mc.font.drawShadow(pPoseStack, this.title, (float)(pX + this.x + 32), (float)(pY + this.y + 9), -1);
        }

        if (newLine) {
            for (int k1 = 0; k1 < this.description.size(); ++k1) {
                this.mc.font.draw(pPoseStack, this.description.get(k1), (float)(i1 + 5), (float)(l + 26 - j1 + 7 + k1 * 9), -5592406);
            }
        } else {
            for (int l1 = 0; l1 < this.description.size(); ++l1) {
                this.mc.font.draw(pPoseStack, this.description.get(l1), (float)(i1 + 5), (float)(pY + this.y + 9 + 17 + l1 * 9), -5592406);
            }
        }

        RenderSystem.setShaderTexture(0, this.display.getIcon());
        this.blit(pPoseStack, pX + this.x + 8, pY + this.y + 5, 0, 0, 16, 16, 16, 16);
    }

    protected void render9Sprite(PoseStack pPoseStack, int pX, int pY, int pWidth, int pHeight, int pPadding, int pUWidth, int pVHeight, int pUOffset, int pVOffset) {
        this.blit(pPoseStack, pX, pY, pUOffset, pVOffset, pPadding, pPadding);
        this.renderRepeating(pPoseStack, pX + pPadding, pY, pWidth - pPadding - pPadding, pPadding, pUOffset + pPadding, pVOffset, pUWidth - pPadding - pPadding, pVHeight);
        this.blit(pPoseStack, pX + pWidth - pPadding, pY, pUOffset + pUWidth - pPadding, pVOffset, pPadding, pPadding);
        this.blit(pPoseStack, pX, pY + pHeight - pPadding, pUOffset, pVOffset + pVHeight - pPadding, pPadding, pPadding);
        this.renderRepeating(pPoseStack, pX + pPadding, pY + pHeight - pPadding, pWidth - pPadding - pPadding, pPadding, pUOffset + pPadding, pVOffset + pVHeight - pPadding, pUWidth - pPadding - pPadding, pVHeight);
        this.blit(pPoseStack, pX + pWidth - pPadding, pY + pHeight - pPadding, pUOffset + pUWidth - pPadding, pVOffset + pVHeight - pPadding, pPadding, pPadding);
        this.renderRepeating(pPoseStack, pX, pY + pPadding, pPadding, pHeight - pPadding - pPadding, pUOffset, pVOffset + pPadding, pUWidth, pVHeight - pPadding - pPadding);
        this.renderRepeating(pPoseStack, pX + pPadding, pY + pPadding, pWidth - pPadding - pPadding, pHeight - pPadding - pPadding, pUOffset + pPadding, pVOffset + pPadding, pUWidth - pPadding - pPadding, pVHeight - pPadding - pPadding);
        this.renderRepeating(pPoseStack, pX + pWidth - pPadding, pY + pPadding, pPadding, pHeight - pPadding - pPadding, pUOffset + pUWidth - pPadding, pVOffset + pPadding, pUWidth, pVHeight - pPadding - pPadding);
    }

    protected void renderRepeating(PoseStack pPoseStack, int pX, int pY, int pBorderToU, int pBorderToV, int pUOffset, int pVOffset, int pUWidth, int pVHeight) {
        for(int i = 0; i < pBorderToU; i += pUWidth) {
            int j = pX + i;
            int k = Math.min(pUWidth, pBorderToU - i);

            for(int l = 0; l < pBorderToV; l += pVHeight) {
                int i1 = pY + l;
                int j1 = Math.min(pVHeight, pBorderToV - l);
                this.blit(pPoseStack, j, i1, pUOffset, pVOffset, k, j1);
            }
        }
    }

    public void addChild(AbilityWidget child) {
        this.children.add(child);
    }

    private AbilityWidget getFirstVisibleParent(Ability ability) {
        do {
            ability = ability.getParent();
        } while (ability != null && ability.getDisplay() == null);

        return ability != null && ability.getDisplay() != null ? this.tab.getAbility(ability) : null;
    }

    public void attachToParent() {
        if (this.parent == null && this.ability.getParent() != null) {
            this.parent = this.getFirstVisibleParent(this.ability);

            if (this.parent != null) {
                this.parent.addChild(this);
            }
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
