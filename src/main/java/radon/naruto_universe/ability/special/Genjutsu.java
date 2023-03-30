package radon.naruto_universe.ability.special;

import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;

public class Genjutsu extends Ability {
    @Override
    public NinjaRank getRank() {
        return null;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        return null;
    }

    @Override
    public Ability getParent() {
        return null;
    }

    @Override
    public float getCost() {
        return 100.0F;
    }

    @Override
    public void runClient(LivingEntity owner) {

    }

    @Override
    public void runServer(LivingEntity owner) {

    }
}
