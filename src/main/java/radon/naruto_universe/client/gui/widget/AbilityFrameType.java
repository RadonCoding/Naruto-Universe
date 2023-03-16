package radon.naruto_universe.client.gui.widget;

public enum AbilityFrameType {
    NORMAL(0),
    UNLOCKABLE(52),
    UNLOCKED(26);

    private final int texture;

    AbilityFrameType(int pTexture) {
        this.texture = pTexture;
    }

    public int getTexture() {
        return this.texture;
    }
}
