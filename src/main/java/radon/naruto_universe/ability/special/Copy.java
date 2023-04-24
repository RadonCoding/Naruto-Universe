package radon.naruto_universe.ability.special;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.ability.event.AbilityTriggerEvent;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.NinjaRank;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.util.HelperMethods;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Copy extends Ability implements Ability.IChanneled {
    private static final double RAYCAST_RANGE = 50.0D;

    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.SHARINGAN);
    }

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
        return new AbilityDisplayInfo(this.getId().getPath(), 10.0F, 0.0F);
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.SHARINGAN.get();
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.CHANNELED;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean(false);

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            result.set(cap.hasUnlockedAbility(NarutoAbilities.SHARINGAN.get()));
        });
        return result.get();
    }


    @SubscribeEvent
    public static void onAbilityTrigger(AbilityTriggerEvent event) {
        LivingEntity target = event.getEntity();

        AABB bounds = new AABB(target.getX() - RAYCAST_RANGE, target.getY() - RAYCAST_RANGE, target.getZ() - RAYCAST_RANGE, target.getX() + RAYCAST_RANGE, target.getY() + RAYCAST_RANGE, target.getZ() + RAYCAST_RANGE);

        for (LivingEntity owner : target.level.getEntitiesOfClass(LivingEntity.class, bounds)) {
            owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                if (cap.isChannelingAbility(NarutoAbilities.COPY.get())) {
                    Ability ability = event.getAbility();

                    if (ability.isUnlockable(owner)) {
                        EntityHitResult hit = HelperMethods.getLivingEntityLookAt(owner, RAYCAST_RANGE, 1.0F);

                        if (hit != null && hit.getEntity() == target) {
                            NarutoAbilities.unlockAbility(owner, ability);
                        }
                    }
                }
            });
        }
    }
}
