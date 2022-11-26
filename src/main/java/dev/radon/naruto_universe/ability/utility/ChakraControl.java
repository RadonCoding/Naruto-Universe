package dev.radon.naruto_universe.ability.utility;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.network.packet.SyncShinobiPlayerS2CPacket;
import dev.radon.naruto_universe.shinobi.ShinobiPlayerProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class ChakraControl extends Ability implements Ability.Toggled {
    @Override
    public ActivationType activationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public long getCombo() {
        return 2;
    }

    @Override
    public ResourceLocation getIcon() {
        return null;
    }

    @Override
    public float getCost() {
        return 0.001F;
    }

    @Override
    public void runClient(LocalPlayer player) {

    }

    @Override
    public void runServer(ServerPlayer player) {
        player.getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).ifPresent(cap -> {
            PacketHandler.sendToClient(new SyncShinobiPlayerS2CPacket(cap.serialize()), player);
        });
    }

    @Override
    public Component getEnableMessage() {
        String key = this.getTranslationKey();
        return Component.translatable(String.format("%s.%s", key, "enable")).withStyle(ChatFormatting.AQUA);
    }

    @Override
    public Component getDisableMessage() {
        String key = this.getTranslationKey();
        return Component.translatable(String.format("%s.%s", key, "disable")).withStyle(ChatFormatting.AQUA);
    }
}
