package radon.naruto_universe.client;

import net.minecraft.client.KeyMapping;

public class NarutoKeyMapping extends KeyMapping {
    private int ticksHeld;
    private int clickCount;
    private boolean hasConsumedClickState;
    private Runnable onClick;

    public NarutoKeyMapping(String pName, int pKeyCode, String pCategory) {
        super(pName, pKeyCode, pCategory);
    }

    public enum KeyState {
        CLICK,
        HELD,
        NOT_PRESSED
    }

    public void registerClickConsumer(Runnable runnable) {
        this.onClick = runnable;
    }

    public void update() {
        if (this.isDown()) {
            ticksHeld++;
        }

        if (this.onClick != null) {
            KeyState clickState = this.consumeClickState();

            if (clickState == KeyState.CLICK) {
                this.onClick.run();
            }
        }
    }

    public KeyState consumeClickState() {
        if (this.isDown()) {
            if (this.hasConsumedClickState) {
                return KeyState.HELD;
            }
            this.hasConsumedClickState = true;
            return KeyState.CLICK;
        } else {
            if (this.clickCount == 0) {
                return KeyState.NOT_PRESSED;
            } else {
                this.clickCount--;
                return KeyState.CLICK;
            }
        }
    }

    public void consumeReleaseDuration() {
        if (this.isDown()) {
            return;
        }

        this.ticksHeld = 0;
    }

    public int currentTickCount() {
        return ticksHeld;
    }

    @Override
    public void setDown(boolean down) {
        if (this.isDown() && !down) {
            if (this.hasConsumedClickState && this.clickCount > 0) {
                this.clickCount = 0;
            }
            this.hasConsumedClickState = false;
        }
        else if (!this.isDown() && down) {
            this.ticksHeld = 0;
        }
        super.setDown(down);
    }
}
