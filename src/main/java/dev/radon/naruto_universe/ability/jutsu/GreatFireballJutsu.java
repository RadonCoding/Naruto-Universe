package dev.radon.naruto_universe.ability.jutsu;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.entity.GreatFireballEntity;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.network.packet.SyncShinobiPlayerS2CPacket;
import dev.radon.naruto_universe.shinobi.ShinobiPlayerProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.atomic.AtomicBoolean;

public class GreatFireballJutsu extends Ability {

    @Override
    public long getCombo() {
        return 123;
    }

    @Override
    public Component getMessage() {
        String key = this.getTranslationKey();
        return Component.translatable(key).withStyle(ChatFormatting.YELLOW);
    }

    @Override
    public ResourceLocation getIcon() {
        return null;
    }

    @Override
    public float getCost() {
        return 25.0F;
    }

    @Override
    public void runClient(LocalPlayer player) {

    }

    @Override
    public void runServer(ServerPlayer player) {
        GreatFireballEntity fireball = new GreatFireballEntity(player.level, player, 1);
        fireball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
        player.level.addFreshEntity(fireball);
    }
}
