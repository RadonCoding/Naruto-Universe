package radon.naruto_universe.ability.special;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.capability.ninja.NinjaRank;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.entity.ChibakuTenseiEntity;
import radon.naruto_universe.entity.MeteoriteEntity;
import radon.naruto_universe.sound.NarutoSounds;
import radon.naruto_universe.util.HelperMethods;

import java.util.List;

public class ChibakuTensei extends Ability {
    private static final double RANGE = 50.0D;
    private static final double RADIUS = 5.0D;

    @Override
    public float getCost(LivingEntity owner) {
        return 500.0F;
    }

    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.RINNEGAN);
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.UNRANKED;
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
    public SoundEvent getActivationSound() {
        return null;
    }

    @Override
    public boolean shouldLog(LivingEntity owner) {
        return false;
    }

    @Override
    public void runServer(LivingEntity owner) {
        if (owner.isShiftKeyDown()) {
            for (ChibakuTenseiEntity entity : owner.level.getEntitiesOfClass(ChibakuTenseiEntity.class, owner.getBoundingBox().inflate(100.0D))) {
                MeteoriteEntity meteorite = entity.getMeteorite();

                if (meteorite != null && !meteorite.isFalling()) {
                    entity.drop();
                    return;
                }
            }
        }

        EntityHitResult result = HelperMethods.getLivingEntityLookAt(owner, RANGE, RADIUS);

        ChibakuTenseiEntity entity;

        if (result != null) {
            Entity target = result.getEntity();
            entity = new ChibakuTenseiEntity(owner, target);
        } else  {
            entity = new ChibakuTenseiEntity(owner);
        }

        owner.level.addFreshEntity(entity);

        owner.level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), NarutoSounds.CHIBAKU_TENSEI.get(), SoundSource.MASTER, 3.0F, 1.0F);
    }
}
