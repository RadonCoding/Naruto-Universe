package radon.naruto_universe.capability;

import net.minecraftforge.event.TickEvent;
import radon.naruto_universe.ability.Ability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.Consumer;

public interface INinjaPlayer extends INBTSerializable<CompoundTag> {
    void tick(Player player, LogicalSide side, TickEvent.Phase phase);

    void generateShinobi(Player player);
    void setPowerResetTimer(int value);

    void delayTickEvent(Consumer<ServerPlayer> task, int delay);

    int getSharinganLevel();
    void levelUpSharingan();

    float getPower();
    void addPower(float amount);
    float getMaxPower();

    void addChakra(float amount);
    void useChakra(float amount);
    void setChakra(float chakra);

    float getChakra();
    float getMaxChakra();

    void addExperience(float amount);
    float getExperience();
    void setExperience(float experience);

    NinjaRank getRank();
    void setRank(NinjaRank rank);

    void addTrait(NinjaTrait trait);
    boolean hasTrait(NinjaTrait trait);

    void unlockAbility(ResourceLocation key);
    boolean hasUnlockedAbility(Ability ability);

    void enableToggledAbility(Player player, Ability ability);
    void disableToggledAbility(Player player, Ability ability);
    boolean hasToggledAbility(Ability ability);
    void clearToggledAbilities();
    void clearToggledDojutsus(Player player, Ability exclude);
    
    ResourceLocation getChanneledAbility();
    void setChanneledAbility(Player player, Ability ability);
    void stopChanneledAbility(Player player);
    boolean isChannelingAbility(Ability ability);
}
