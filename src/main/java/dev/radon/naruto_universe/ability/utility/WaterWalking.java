package dev.radon.naruto_universe.ability.utility;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import dev.radon.naruto_universe.client.gui.widget.AbilityFrameType;
import dev.radon.naruto_universe.client.particle.ParticleRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class WaterWalking extends Ability implements Ability.Toggled {

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public long getCombo() {
        return 2;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        AbilityDisplayInfo info = new AbilityDisplayInfo(iconPath, 0.0F, 0.0F);
        return info;
    }

    @Override
    public Ability getParent() {
        return null;
    }

    @Override
    public ChatFormatting getChatColor() {
        return ChatFormatting.AQUA;
    }

    @Override
    public float getCost() {
        return 0.001F;
    }

    private void checkWaterWalking(Player player) {
        if (player.isShiftKeyDown()) {
            return;
        }
    }

    @Override
    public void runClient(LocalPlayer player) {
        checkWaterWalking(player);

        player.level.addParticle(ParticleRegistry.CHAKRA.get(),
                player.level.random.nextGaussian() * 0.1D + player.getX(),
                player.getY() + 0.23D, player.level.random.nextGaussian() * 0.1D + player.getZ(), 0.0D, -0.1D, 0.0D);
    }

    @Override
    public void runServer(ServerPlayer player) {
        checkWaterWalking(player);
    }
}
