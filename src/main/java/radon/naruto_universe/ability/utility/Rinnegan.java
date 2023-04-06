package radon.naruto_universe.ability.utility;

import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.sound.NarutoSounds;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.capability.NinjaTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;

import java.util.List;

public class Rinnegan extends Ability implements Ability.IToggled {
    @Override
    public boolean isDojutsu() {
        return true;
    }
    
    @Override
    public NinjaRank getRank() {
        return NinjaRank.ACADEMY_STUDENT;
    }

    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.UNLOCKED_RINNEGAN);
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        return new AbilityDisplayInfo(this.getId().getPath(), 7.0F, 0.0F);
    }

    @Override
    public ChatFormatting getChatColor() {
        return ChatFormatting.DARK_PURPLE;
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.SHARINGAN.get();
    }

    @Override
    public float getCost() {
        return 0.05F;
    }

    @Override
    public void runClient(LivingEntity owner) {

    }

    @Override
    public void runServer(LivingEntity owner) {

    }

    @Override
    public SoundEvent getActivationSound() {
        return NarutoSounds.RINNEGAN_ACTIVATE.get();
    }
}

