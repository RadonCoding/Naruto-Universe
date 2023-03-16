package radon.naruto_universe.ability.utility;

import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.sound.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

import java.util.Arrays;
import java.util.List;

public class Sharingan extends Ability implements Ability.Toggled {

    @Override
    public List<NinjaTrait> getRequirements() {
        return Arrays.asList(NinjaTrait.UNLOCKED_SHARINGAN);
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
    public long getCombo() {
        return 12;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        AbilityDisplayInfo info = new AbilityDisplayInfo(iconPath, 6.0F, 0.0F);
        return info;
    }

    @Override
    public ChatFormatting getChatColor() {
        return ChatFormatting.DARK_RED;
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.POWER_CHARGE.get();
    }

    @Override
    public float getCost() {
        return 0.025F;
    }

    @Override
    public void runClient(LocalPlayer player) {

    }

    @Override
    public void runServer(ServerPlayer player) {

    }

    @Override
    public SoundEvent getActivationSound() {
        return SoundRegistry.SHARINGAN_ACTIVATE.get();
    }

    @Override
    public SoundEvent getDectivationSound() {
        return SoundRegistry.SHARINGAN_DEACTIVATE.get();
    }
}
