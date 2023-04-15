package radon.naruto_universe.ability.special;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.ability.event.AbilityTriggerEvent;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.util.HelperMethods;

public class Copy extends Ability implements Ability.IChanneled {
    private static final double RANGE = 50.0D;

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.GENIN;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return null;
    }

    @Override
    public Ability getParent() {
        return null;
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.CHANNELED;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    @SubscribeEvent
    public static void onAbilityTrigger(AbilityTriggerEvent event) {
        LivingEntity target = event.getEntity();

        AABB bounds = new AABB(target.getX() - RANGE, target.getY() - RANGE, target.getZ() - RANGE, target.getX() + RANGE, target.getY() + RANGE, target.getZ() + RANGE);

        for (LivingEntity owner : target.level.getEntitiesOfClass(LivingEntity.class, bounds)) {
            owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                if (cap.isChannelingAbility(NarutoAbilities.COPY.get())) {
                    Ability ability = event.getAbility();

                    if (ability.isUnlockable(owner)) {
                        EntityHitResult hit = HelperMethods.getEntityLookAt(owner, RANGE);

                        if (hit != null && hit.getEntity() == target) {
                            NarutoAbilities.unlockAbility(owner, ability);
                        }
                    }
                }
            });
        }
    }
}
