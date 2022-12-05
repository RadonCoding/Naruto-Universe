package dev.radon.naruto_universe.client.gui.widget;

import dev.radon.naruto_universe.NarutoUniverse;
import net.minecraft.resources.ResourceLocation;

public class AbilityDisplayInfo {
    private final ResourceLocation icon;
    private final float x;
    private final float y;

    public AbilityDisplayInfo(String iconPath, float x, float y) {
        this.icon = new ResourceLocation(NarutoUniverse.MOD_ID, String.format("textures/abilities/%s.png", iconPath));
        this.x = x;
        this.y = y;
    }

    public ResourceLocation getIcon() {
        return this.icon;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
}
