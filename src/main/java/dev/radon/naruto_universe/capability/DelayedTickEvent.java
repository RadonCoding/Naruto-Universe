package dev.radon.naruto_universe.capability;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class DelayedTickEvent {
    private final Consumer<ServerPlayer> task;
    private int delay;

    public DelayedTickEvent(Consumer<ServerPlayer> task, int delay) {
        this.task = task;
        this.delay = delay;
    }

    public void tick() {
        delay--;
    }

    public boolean run(ServerPlayer player) {
        if (delay <= 0) {
            task.accept(player);
            return true;
        }
        return false;
    }
}
