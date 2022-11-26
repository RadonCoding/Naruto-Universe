package dev.radon.naruto_universe.client.screen;

public enum AbilityWidgetType {
    OBTAINED(0),
    UNOBTAINED(1);

    private final int y;

    private AbilityWidgetType(int pY) {
        this.y = pY;
    }

    public int getIndex() {
        return this.y;
    }
}
