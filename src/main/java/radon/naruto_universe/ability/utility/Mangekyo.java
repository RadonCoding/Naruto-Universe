package radon.naruto_universe.ability.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.NinjaRank;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.sound.NarutoSounds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Mangekyo extends Ability implements Ability.IToggled, Ability.ISpecial {
    @Override
    public NinjaRank getRank() {
        return NinjaRank.UNRANKED;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        AtomicReference<AbilityDisplayInfo> display = new AtomicReference<>(null);

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            String iconName = cap.getMangekyoType().getIconName();
            display.set(new AbilityDisplayInfo(iconName, 8.0F, 1.0F));
        });
        return display.get();
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.SHARINGAN.get();
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.05F;
    }

    @Override
    public ChatFormatting getChatColor() {
        return ChatFormatting.DARK_RED;
    }

    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.UNLOCKED_MANGEKYO);
    }

    @Override
    public boolean isDojutsu() {
        return true;
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
    public List<Ability> getSpecialAbilities(LivingEntity owner) {
        List<Ability> abilities = new ArrayList<>();
        abilities.add(NarutoAbilities.GENJUTSU.get());

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            switch (cap.getMangekyoType()) {
                case ITACHI -> abilities.addAll(List.of(NarutoAbilities.AMATERASU.get(), NarutoAbilities.TSUKUYOMI.get()));
                case SASUKE -> abilities.add(NarutoAbilities.AMATERASU.get());
            }
        });
        return abilities;
    }
}
