package dev.radon.naruto_universe.ability.utility;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.client.particle.ParticleRegistry;
import dev.radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;
import dev.radon.naruto_universe.capability.NinjaPlayerHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class ChakraCharge extends Ability implements Ability.Channeled {

    @Override
    public ActivationType getActivationType() {
        return ActivationType.CHANNELED;
    }

    @Override
    public long getCombo() {
        return 1;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        AbilityDisplayInfo info = new AbilityDisplayInfo(iconPath, 2.0F, 0.0F);
        return info;
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.WATER_WALKING.get();
    }

    public ChatFormatting getChatColor() {
        return ChatFormatting.AQUA;
    }

    @Override
    public boolean checkChakra(ServerPlayer player) {
        return true;
    }

    @Override
    public float getCost() {
        return 0.0F;
    }

    @Override
    public void runClient(LocalPlayer player) {
        player.level.addParticle(ParticleRegistry.CHAKRA.get(),
                player.level.random.nextGaussian() * 0.1D + player.getX(),
                player.getY() + 0.56F, player.level.random.nextGaussian() * 0.1D + player.getZ(),
                0.0D, 0.75D, 0.0D);
    }

    @Override
    public void runServer(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1, 10, false, false, false));

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            float amount = 0.1F;
            cap.addChakra(amount);
            PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
        });
    }
}
