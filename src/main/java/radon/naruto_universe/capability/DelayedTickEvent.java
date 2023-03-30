package radon.naruto_universe.capability;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class DelayedTickEvent {
    private final Consumer<LivingEntity> task;
    private int delay;

    public DelayedTickEvent(Consumer<LivingEntity> task, int delay) {
        this.task = task;
        this.delay = delay;
    }

    public void tick() {
        delay--;
    }

    public boolean run(LivingEntity entity) {
        if (delay <= 0) {
            task.accept(entity);
            return true;
        }
        return false;
    }
}
