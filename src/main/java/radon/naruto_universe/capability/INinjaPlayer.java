package radon.naruto_universe.capability;

import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.ability.Ability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.function.Consumer;

public interface INinjaPlayer extends INBTSerializable<CompoundTag> {
    void tick(LivingEntity entity, boolean isClientSide);

    void generateShinobi(Player player);
    void setPowerResetTimer(int value);

    void delayTickEvent(Consumer<LivingEntity> task, int delay);

    int getSharinganLevel();
    void levelUpSharingan();

    float getPower();
    void resetPower();
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

    void unlockAbility(Ability key);
    boolean hasUnlockedAbility(Ability ability);

    void enableToggledAbility(LivingEntity entity, Ability ability);
    void disableToggledAbility(LivingEntity entity, Ability ability);
    boolean hasToggledAbility(Ability ability);
    void clearToggledAbilities();
    void clearToggledDojutsus(LivingEntity entity, Ability exclude);
    List<Ability> getSpecialAbilities();
    
    Ability getChanneledAbility();
    void setChanneledAbility(LivingEntity entity, Ability ability);
    void stopChanneledAbility(LivingEntity entity);
    boolean isChannelingAbility(Ability ability);

    void setMovementSpeed(double movementSpeed);
    double getMovementSpeed();
}
