package radon.naruto_universe.ability.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import radon.naruto_universe.ability.Ability;

public class AbilityTriggerEvent extends LivingEvent {
    private final Ability ability;

    public AbilityTriggerEvent(LivingEntity entity, Ability ability) {
        super(entity);

        this.ability = ability;
    }

    public Ability getAbility() {
        return this.ability;
    }
}
