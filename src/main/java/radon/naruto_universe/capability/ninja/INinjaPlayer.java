package radon.naruto_universe.capability.ninja;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
import radon.naruto_universe.ability.Ability;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface INinjaPlayer extends INBTSerializable<CompoundTag> {
    void tick(LivingEntity entity, boolean isClientSide);

    void generateNinja();

    void setPowerResetTimer(int value);

    void delayTickEvent(Consumer<LivingEntity> task, int delay, LogicalSide side);

    float getAbilityExperience(Ability ability);
    void addAbilityExperience(Ability ability, float amount);

    float getMangekyoBlindess();
    void increaseMangekyoBlindess(float amount);

    boolean isInitialized();

    int getSharinganLevel();
    void levelUpSharingan();

    MangekyoType getMangekyoType();
    void setMangekyoType(MangekyoType type);

    void addCooldown(Ability ability);
    int getRemainingCooldown(Ability ability);
    boolean isCooldownDone(Ability ability);

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

    Ability getCurrentEyes();

    void enableToggledAbility(LivingEntity entity, Ability ability);
    void disableToggledAbility(LivingEntity entity, Ability ability);
    boolean hasToggledAbility(Ability ability);
    void clearToggledAbilities();
    void clearToggledDojutsus(LivingEntity entity, Ability exclude);
    List<Ability> getSpecialAbilities(LivingEntity owner);
    
    Ability getChanneledAbility();
    void setChanneledAbility(LivingEntity entity, Ability ability);
    void stopChanneledAbility(LivingEntity entity);
    boolean isChannelingAbility(Ability ability);

    void setMovementSpeed(double movementSpeed);
    double getMovementSpeed();
}
