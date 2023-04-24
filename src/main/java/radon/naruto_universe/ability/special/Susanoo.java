package radon.naruto_universe.ability.special;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.NinjaRank;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.entity.SusanooEntity;
import radon.naruto_universe.sound.NarutoSounds;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Susanoo extends Ability implements Ability.IToggled {
    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.UNLOCKED_MANGEKYO);
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.UNRANKED;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return new AbilityDisplayInfo(this.getId().getPath(), 10.0F, 1.0F);
    }

    @Override
    public boolean shouldLog(LivingEntity owner) {
        return false;
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.MANGEKYO.get();
    }

    @Override
    public SoundEvent getActivationSound() {
        return NarutoSounds.SUSANOO.get();
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean(false);

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            result.set(cap.hasUnlockedAbility(NarutoAbilities.MANGEKYO.get()));
        });
        return result.get();
    }

    @Override
    public float getCost(LivingEntity owner) {
        SusanooEntity susanoo = (SusanooEntity) owner.getVehicle();

        if (susanoo != null) {
            return switch (susanoo.getStage()) {
                case RIBCAGE -> 1.0F;
                case SKELETAL -> 2.5F;
                case HUMANOID -> 5.0F;
                case ARMORED -> 7.5F;
                case PERFECT -> 10.0F;
            };
        }
        return 0.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 60 * 20; // 5 minute cooldown
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        if (!(owner.getVehicle() instanceof SusanooEntity)) {
            return Status.FAILURE;
        }
        return super.checkStatus(owner);
    }

    @Override
    public void runServer(LivingEntity owner) {
        if (owner.getVehicle() instanceof SusanooEntity susanoo) {
            int multiplier = susanoo.getStage().ordinal();
            owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.increaseMangekyoBlindess(multiplier * 0.001F));
        }
    }

    @Override
    public void runClient(LivingEntity owner) {
        if (owner.getVehicle() instanceof SusanooEntity susanoo) {
            int multiplier = susanoo.getStage().ordinal();
            owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.increaseMangekyoBlindess(multiplier * 0.001F));
        }
    }

    @Override
    public void onToggled(LivingEntity owner, boolean isClientSide) {
        if (!isClientSide) {
            SusanooEntity susanoo = new SusanooEntity(owner);
            owner.level.addFreshEntity(susanoo);
        }
    }

    @Override
    public void onDisabled(LivingEntity owner, boolean isClientSide) {
        if (owner.getVehicle() instanceof SusanooEntity susanoo) {
            susanoo.discard();
        }
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }
}
