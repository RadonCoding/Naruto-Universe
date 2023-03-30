package radon.naruto_universe.ability.utility;

import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.sound.SoundRegistry;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
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
        String iconPath = this.getId().getPath();
        return new AbilityDisplayInfo(iconPath, 7.0F, 0.0F);
    }

    @Override
    public ChatFormatting getChatColor() {
        return ChatFormatting.DARK_PURPLE;
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.SHARINGAN.get();
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
        return SoundRegistry.RINNEGAN_ACTIVATE.get();
    }
}

