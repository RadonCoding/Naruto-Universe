package radon.naruto_universe.capability.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.capability.ninja.ToggledEyes;

import java.util.UUID;

public interface INarutoData {
    void tick(LivingEntity entity, boolean isClientSide);

    boolean isRemoteBurning(UUID uuid);
    boolean isLocalBurning();

    void setLocalBurning(LivingEntity owner, boolean burning);

    ToggledEyes getRemoteEyes(UUID uuid);
    ToggledEyes getLocalEyes();

    void setLocalEyes(LivingEntity owner, ToggledEyes eyes);

    boolean isSynced(UUID uuid);
    void sync(LivingEntity owner);

    CompoundTag serializeNBT();
    void deserializeLocalNBT(CompoundTag nbt);
    void deserializeRemoteNBT(UUID uuid, CompoundTag nbt);
}
