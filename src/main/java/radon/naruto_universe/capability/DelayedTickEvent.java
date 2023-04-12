package radon.naruto_universe.capability;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
import org.jline.utils.Log;

import java.util.function.Consumer;

public class DelayedTickEvent {
    private final Consumer<LivingEntity> task;
    private int delay;
    private final LogicalSide side;

    public DelayedTickEvent(Consumer<LivingEntity> task, int delay, LogicalSide side) {
        this.task = task;
        this.delay = delay;
        this.side = side;
    }

    public LogicalSide getSide() {
        return this.side;
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
