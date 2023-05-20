package radon.naruto_universe.ability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.NinjaRank;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.sound.NarutoSounds;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Ability {
    public enum ActivationType {
        INSTANT,
        TOGGLED,
        CHANNELED
    }

    public enum Status {
        SUCCESS,
        FAILURE,
        NO_POWER,
        NO_CHAKRA,
        COOLDOWN
    }

    // Used for storing the power that was used to activate the jutsu
    private float power = 0.0F;
    private float experience = 0.0F;

    // A way to avoid hardcoding the dojutsu in AbilityRegistry::getDojutsuAbilities
    public boolean isDojutsu() {
        return false;
    }

    public boolean shouldLog(LivingEntity owner) {
        return owner.level.isClientSide;
    }

    public SoundEvent getActivationSound() {
        return NarutoSounds.ABILITY_ACTIVATE.get();
    }

    public List<NinjaTrait> getRequirements() {
        return Collections.emptyList();
    }

    public abstract NinjaRank getRank();

    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    public NinjaTrait getRelease() {
        return NinjaTrait.NONE;
    }

    // Number of ticks you have to wait before being able to use the ability again
    public int getCooldown() { return 0; }

    public Component getChatMessage() {
        if (this.getRelease() != NinjaTrait.NONE) {
            return Component.literal(String.format("%s - %s", this.getRelease().getIdentifier().getString(), this.getName().getString()));
        }
        return this.getName();
    }

    public boolean checkRequirements(LivingEntity owner) {
        if ((owner instanceof Player player && player.getAbilities().instabuild) || NarutoAbilities.checkRequirements(owner, this)) {
            return true;
        }
        owner.sendSystemMessage(Component.translatable("ability.fail.not_skilled_enough", this.getRank().getIdentifier()));
        return false;
    }

    public boolean isUnlockable(LivingEntity owner) {
        if (owner instanceof Player player && player.getAbilities().instabuild) {
            return true;
        }
        return NarutoAbilities.checkRequirements(owner, this);
    }

    public boolean isUnlocked(LivingEntity owner) {
        return NarutoAbilities.isUnlocked(owner, this);
    }

    public abstract AbilityDisplayInfo getDisplay(LivingEntity owner);
    public abstract Ability getParent();

    public float getMinPower() {
        return 0.0F;
    }
    public float getPower() { return this.power; }
    public float getExperience() { return this.experience; }

    // Used for checking if a toggled ability can still be used
    public Status checkStatus(LivingEntity owner) {
        AtomicReference<Status> result = new AtomicReference<>(Status.SUCCESS);

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            float power = cap.getPower();

            this.power = power;

            float minPower = this.getMinPower();

            if (minPower > 0.0F && this.power < this.getMinPower()) {
                result.set(Status.NO_POWER);
            }
            else {
                if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
                    float cost = this.getCost(owner);

                    if (minPower > 0.0F) {
                        cost *= power;
                        cost -= cap.getAbilityExperience(this) * 0.25F;
                    }

                    if (cap.getChakra() < cost) {
                        result.set(Status.NO_CHAKRA);
                    } else {
                        cap.useChakra(cost);
                    }
                }
            }

            if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
                if (this.getCooldown() != 0) {
                    if (!cap.isCooldownDone(this) && !cap.hasToggledAbility(this)) {
                        result.set(Status.COOLDOWN);
                    } else {
                        cap.addCooldown(this);
                    }
                }
            }

            if (result.get() == Status.SUCCESS) {
                if (minPower > 0.0F) {
                    cap.addAbilityExperience(this, 0.01F * (power / cap.getMaxPower()));

                    cap.resetPower();
                } else {
                    cap.addAbilityExperience(this, 0.001F);
                }
                this.experience = cap.getAbilityExperience(this);
            }
        });
        return result.get();
    }

    public float getDamage() {
        return 0.0F;
    }

    public float getCost(LivingEntity owner) {
        return 0.0F;
    }

    public int getPrice() {
        return 0;
    }

    public void runClient(LivingEntity owner) {}
    public void runServer(LivingEntity owner) {}

    public boolean hasCombo() {
        return false;
    }

    public ResourceLocation getId() {
        return NarutoAbilities.getKey(this);
    }

    public Component getName() {
        ResourceLocation key = this.getId();
        return Component.translatable(String.format("%s.name", key));
    }

    public Component getDescription() {
        ResourceLocation key = this.getId();
        return Component.translatable(String.format("%s.desc", key));
    }

    public ChatFormatting getChatColor() { return ChatFormatting.WHITE; }

    public boolean canTrigger(LivingEntity owner) { return true; };

    public interface IChanneled {
        default void onStart(LivingEntity owner, boolean isClientSide) {}
        default void onStop(LivingEntity owner, boolean isClientSide) {}

        default Component getStartMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = ability.getId();
            return Component.translatable(String.format("%s.%s", key, "start")).withStyle(ability.getChatColor());
        }

        default Component getStopMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = ability.getId();
            return Component.translatable(String.format("%s.%s", key, "stop")).withStyle(ability.getChatColor());
        }
    }

    public interface IToggled {
        default void onToggled(LivingEntity owner, boolean isClientSide) {}
        default void onDisabled(LivingEntity owner, boolean isClientSide) {}

        default Component getEnableMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = ability.getId();
            return Component.translatable(String.format("%s.%s", key, "enable")).withStyle(ability.getChatColor());
        }

        default Component getDisableMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = ability.getId();
            return Component.translatable(String.format("%s.%s", key, "disable")).withStyle(ability.getChatColor());
        }

        default SoundEvent getActivationSound() {
            return NarutoSounds.ABILITY_ACTIVATE.get();
        }

        default SoundEvent getDectivationSound() {
            return null;
        }
    }

    public interface ISpecial {
        List<Ability> getSpecialAbilities(LivingEntity owner);
    }
}
