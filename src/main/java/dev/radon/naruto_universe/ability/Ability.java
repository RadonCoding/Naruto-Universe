package dev.radon.naruto_universe.ability;

import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.network.packet.SyncShinobiPlayerS2CPacket;
import dev.radon.naruto_universe.shinobi.ShinobiPlayerProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Ability {
    public enum ActivationType {
        INSTANT,
        TOGGLED,
        CHANNELED
    }

    public ActivationType activationType() {
        return ActivationType.INSTANT;
    }

    public abstract long getCombo();

    public Component getMessage() {
        return Component.empty();
    }

    public boolean checkChakra(ServerPlayer player) {
        AtomicBoolean result = new AtomicBoolean(false);

        player.getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).ifPresent(cap -> {
            if (cap.getChakra() >= this.getCost()) {
                cap.useChakra(this.getCost());
                PacketHandler.sendToClient(new SyncShinobiPlayerS2CPacket(cap.serialize()), player);
                result.set(true);
            }
            else {
                player.sendSystemMessage(Component.translatable("ability.fail.not_enough_chakra"));
            }
        });

        return result.get();
    }

    public abstract float getCost();

    public abstract ResourceLocation getIcon();
    public abstract void runClient(LocalPlayer player);
    public abstract void runServer(ServerPlayer player);

    public String getTranslationKey() {
        String key = AbilityRegistry.ABILITY_REGISTRY.get().getKey(this).toString();
        return key;
    }

    public interface Channeled {
        Component getStartMessage();
        Component getStopMessage();
    }

    public interface Toggled {
        Component getEnableMessage();
        Component getDisableMessage();
    }
}
