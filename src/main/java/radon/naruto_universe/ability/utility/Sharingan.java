package radon.naruto_universe.ability.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.sound.NarutoSounds;

import java.util.List;

public class Sharingan extends Ability implements Ability.IToggled, Ability.ISpecial {
    @Override
    public boolean isDojutsu() {
        return true;
    }

    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.UNLOCKED_SHARINGAN);
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.ACADEMY_STUDENT;
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        return new AbilityDisplayInfo(iconPath, 6.0F, 0.0F);
    }

    @Override
    public ChatFormatting getChatColor() {
        return ChatFormatting.DARK_RED;
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.POWER_CHARGE.get();
    }

    @Override
    public float getCost() {
        return 0.025F;
    }

    @Override
    public void runClient(LivingEntity owner) {

    }

    @Override
    public void runServer(LivingEntity owner) {

    }

    @Override
    public SoundEvent getActivationSound() {
        return NarutoSounds.SHARINGAN_ACTIVATE.get();
    }

    @Override
    public SoundEvent getDectivationSound() {
        return NarutoSounds.SHARINGAN_DEACTIVATE.get();
    }

    @Override
    public List<Ability> getSpecialAbilities() {
        return List.of(NarutoAbilities.GENJUTSU.get(), NarutoAbilities.AMATERASU.get(), NarutoAbilities.SUSANOO.get());
    }
}
