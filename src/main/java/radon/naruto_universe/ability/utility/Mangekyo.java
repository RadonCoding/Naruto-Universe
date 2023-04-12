package radon.naruto_universe.ability.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.sound.NarutoSounds;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Mangekyo extends Ability implements Ability.IToggled, Ability.ISpecial {
    @Override
    public NinjaRank getRank() {
        return NinjaRank.ACADEMY_STUDENT;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        AtomicReference<AbilityDisplayInfo> display = new AtomicReference<>(null);

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            String iconName = cap.getMangekyoType().getIconName();
            display.set(new AbilityDisplayInfo(iconName, 7.0F, 0.0F));
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
        return List.of(NinjaTrait.UNLOCKED_ITACHI_MANGEKYO);
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
    public List<Ability> getSpecialAbilities() {
        return List.of(NarutoAbilities.SUSANOO.get());
    }
}
