package dev.radon.naruto_universe.ability;

import dev.radon.naruto_universe.capability.NinjaPlayerHandler;
import dev.radon.naruto_universe.capability.NinjaTrait;
import dev.radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Ability {
    public enum ActivationType {
        INSTANT,
        TOGGLED,
        CHANNELED
    }

    protected final List<NinjaTrait> requirements = Lists.newArrayList();

    public List<NinjaTrait> getRequirements() {
        return this.requirements;
    }

    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    public abstract long getCombo();

    public boolean checkRequirements(Player player) {
        return AbilityRegistry.getProgress(player, this) >= 1.0F;
    }

    public float getProgress(Player player) {
        return AbilityRegistry.getProgress(player, this);
    }

    public boolean isUnlocked(Player player) {
        return AbilityRegistry.isUnlocked(player, this);
    }

    public abstract AbilityDisplayInfo getDisplay();
    public abstract Ability getParent();

    public boolean checkChakra(ServerPlayer player) {
        AtomicBoolean result = new AtomicBoolean(false);

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (cap.getChakra() >= this.getCost()) {
                cap.useChakra(this.getCost());
                PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
                result.set(true);
            }
            else {
                player.sendSystemMessage(Component.translatable("ability.fail.not_enough_chakra"));
            }
        });

        return result.get();
    }

    public abstract float getCost();

    public abstract void runClient(LocalPlayer player);
    public abstract void runServer(ServerPlayer player);

    public ResourceLocation getId() {
        return AbilityRegistry.getKey(this);
    }

    public Component getName() {
        ResourceLocation key = this.getId();
        return Component.translatable(String.format("%s.name", key));
    }

    public Component getDescription() {
        ResourceLocation key = this.getId();
        return Component.translatable(String.format("%s.desc", key));
    }

    public ChatFormatting getChatColor() { return ChatFormatting.WHITE; }

    public interface Channeled {
        default Component getStartMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = ability.getId();
            return Component.translatable(String.format("%s.%s", key, "start")).withStyle(ability.getChatColor());
        }

        default Component getStopMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = ability.getId();
            return Component.translatable(String.format("%s.%s", key, "stop")).withStyle(ability.getChatColor());
        }
    }

    public interface Toggled {
        default Component getEnableMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = ability.getId();
            return Component.translatable(String.format("%s.%s", key, "enable")).withStyle(ability.getChatColor());
        }

        default Component getDisableMessage() {
            Ability ability = (Ability) this;
            ResourceLocation key = ability.getId();
            return Component.translatable(String.format("%s.%s", key, "disable")).withStyle(ability.getChatColor());
        }
    }
}
