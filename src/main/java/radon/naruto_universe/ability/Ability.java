package radon.naruto_universe.ability;

import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;
import radon.naruto_universe.sound.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Ability {
    public enum ActivationType {
        INSTANT,
        TOGGLED,
        CHANNELED
    }

    // Used for storing the power that was used to activate the jutsu
    private float power = 0.0F;

    // A way to avoid hardcoding the dojutsu in AbilityRegistry::getDojutsuAbilities
    public boolean isDojutsu() {
        return false;
    }

    public boolean shouldLog() {
        return true;
    }

    public SoundEvent getActivationSound() {
        return SoundRegistry.ABILITY_ACTIVATE.get();
    }

    public List<NinjaTrait> getRequirements() {
        return Collections.emptyList();
    }

    public abstract NinjaRank getRank();

    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    public long getCombo() {
        return 0;
    }

    public NinjaTrait getRelease() {
        return NinjaTrait.NONE;
    }

    public Component getChatMessage() {
        if (this.getRelease() != NinjaTrait.NONE) {
            return Component.literal(String.format("%s - %s", this.getRelease().getIdentifier().getString(), this.getName().getString()));
        }
        return this.getName();
    }

    public boolean checkRequirements(Player player) {
        if (player.getAbilities().instabuild || AbilityRegistry.checkRequirements(player, this)) {
            return true;
        }
        player.sendSystemMessage(Component.translatable("ability.fail.not_skilled_enough", this.getRank().getIdentifier()));
        return false;
    }

    public boolean isUnlockable(Player player) {
        if (player.getAbilities().instabuild) {
            return true;
        }
        return AbilityRegistry.checkRequirements(player, this);
    }

    public boolean isUnlocked(Player player) {
        return AbilityRegistry.isUnlocked(player, this);
    }

    public abstract AbilityDisplayInfo getDisplay();
    public abstract Ability getParent();

    public float getMinPower() {
        return 0.0F;
    }
    public float getPower() { return this.power; }

    public boolean checkChakra(ServerPlayer player) {
        AtomicBoolean result = new AtomicBoolean(true);

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            float power = cap.getPower();

            this.power = power;

            if (this.getMinPower() > 0.0F && power < this.getMinPower()) {
                player.sendSystemMessage(Component.translatable("ability.fail.not_enough_power"));
                result.set(false);
                return;
            }

            if (player.getAbilities().instabuild) {
                return;
            }

            float cost = this.getMinPower() > 0.0F ? this.getCost() * power : this.getCost();

            if (cap.getChakra() < cost) {
                player.sendSystemMessage(Component.translatable("ability.fail.not_enough_chakra"));
                result.set(false);
            } else {
                cap.useChakra(cost);
                PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
            }
        });
        return !result.get();
    }

    public abstract float getCost();

    public abstract void runClient(LivingEntity owner);
    public abstract void runServer(LivingEntity owner);

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

        default SoundEvent getActivationSound() {
            return SoundRegistry.ABILITY_ACTIVATE.get();
        }

        default SoundEvent getDectivationSound() {
            return null;
        }
    }
}
