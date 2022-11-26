package dev.radon.naruto_universe.ability.utility;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.network.packet.SyncShinobiPlayerS2CPacket;
import dev.radon.naruto_universe.shinobi.ShinobiPlayerProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class ChakraCharge extends Ability implements Ability.Channeled {
    @Override
    public ActivationType activationType() {
        return ActivationType.CHANNELED;
    }

    @Override
    public long getCombo() {
        return 1;
    }

    @Override
    public ResourceLocation getIcon() {
        return null;
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

    }

    @Override
    public void runServer(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1, 10, false, false, false));

        player.getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).ifPresent(cap -> {
            if (cap.getChakra() < cap.getMaxChakra()) {
                cap.addChakra(0.1F);
            }
            PacketHandler.sendToClient(new SyncShinobiPlayerS2CPacket(cap.serialize()), player);
        });
    }

    @Override
    public Component getStartMessage() {
        String key = this.getTranslationKey();
        return Component.translatable(String.format("%s.%s", key, "start")).withStyle(ChatFormatting.AQUA);
    }

    @Override
    public Component getStopMessage() {
        String key = this.getTranslationKey();
        return Component.translatable(String.format("%s.%s", key, "stop")).withStyle(ChatFormatting.AQUA);
    }
}
