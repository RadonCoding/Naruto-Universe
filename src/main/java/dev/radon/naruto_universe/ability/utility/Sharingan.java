package dev.radon.naruto_universe.ability.utility;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.capability.NinjaTrait;
import dev.radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class Sharingan extends Ability implements Ability.Toggled {

    public Sharingan() {
        this.requirements.add(NinjaTrait.UNLOCKED_SHARINGAN);
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
        AbilityDisplayInfo info = new AbilityDisplayInfo(iconPath, 3.0F, 0.0F);
        return info;
    }

    @Override
    public ChatFormatting getChatColor() {
        return ChatFormatting.DARK_RED;
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.CHAKRA_CHARGE.get();
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
