package radon.naruto_universe.capability.ninja;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public enum MangekyoType {
    ITACHI(11878454, "itachi_mangekyo"),
    SASUKE(11231743, "sasuke_mangekyo"),
    OBITO(3396607, "obito_mangekyo"),
    MADARA(225276, "madara_mangekyo");

    private final int susanooColor;
    private final String iconName;

    MangekyoType(int susanooColor, String iconName) {
        this.susanooColor = susanooColor;
        this.iconName = iconName;
    }

    public Vector3f getSusanooColor() {
        return Vec3.fromRGB24(this.susanooColor).toVector3f();
    }

    public String getIconName() { return this.iconName; }
}
