package dev.radon.naruto_universe.shinobi;

public class DelayedTickEvent {
    private int delay;
    private final Runnable task;

    public DelayedTickEvent(int delay, Runnable task) {
        this.delay = delay;
        this.task = task;
    }

    public void tick() {
        delay--;
    }

    public boolean run() {
        if (delay <= 0) {
            task.run();
            return true;
        }
        return false;
    }
}
