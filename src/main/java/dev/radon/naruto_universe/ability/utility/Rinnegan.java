package dev.radon.naruto_universe.ability.utility;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.capability.NinjaTrait;
import dev.radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;

public class Rinnegan extends Ability implements Ability.Toggled {

    public Rinnegan() {
        this.requirements.add(NinjaTrait.UNLOCKED_RINNEGAN);
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public long getCombo() {
        return 23;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        AbilityDisplayInfo info = new AbilityDisplayInfo(iconPath, 4.0F, 0.0F);
        return info;
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
        return 0.01F;
    }

    @Override
    public void runClient(LocalPlayer player) {

    }

    @Override
    public void runServer(ServerPlayer player) {

    }
}

