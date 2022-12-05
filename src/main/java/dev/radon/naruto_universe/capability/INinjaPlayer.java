package dev.radon.naruto_universe.capability;

import dev.radon.naruto_universe.ability.Ability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.LogicalSide;

import java.util.List;
import java.util.function.Consumer;

@AutoRegisterCapability
public interface INinjaPlayer extends INBTSerializable<CompoundTag> {
    void tick(Player player, LogicalSide side);

    void generateShinobi(Player player);

    void delayTickEvent(Consumer<ServerPlayer> task, int delay);
    void updateTickEvents(ServerPlayer player);

    NinjaClan getClan();
    void setClan(NinjaClan clan);

    int getSharinganLevel();
    boolean levelUpSharingan();

    void addChakra(float amount);
    void useChakra(float amount);
    void setChakra(float chakra);

    float getChakra();
    float getMaxChakra();

    float getVoicePitch();
    void setVoicePitch(float voicePitch);

    void addTrait(NinjaTrait trait);
    boolean hasTrait(NinjaTrait trait);

    void unlockAbility(ResourceLocation key);
    boolean hasUnlockedAbility(Ability ability);

    void enableToggledAbility(Ability ability);
    void disableToggledAbility(Ability ability);
    boolean hasToggledAbility(Ability ability);
    void updateToggledAbilities(Player player, LogicalSide side);
    
    ResourceLocation getChanneledAbility();
    void setChanneledAbility(Ability ability);
    void stopChanneledAbility();
    void updateChanneledAbilities(Player player, LogicalSide side);
}
