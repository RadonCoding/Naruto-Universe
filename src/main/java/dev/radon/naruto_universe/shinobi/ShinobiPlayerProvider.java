package dev.radon.naruto_universe.shinobi;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShinobiPlayerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<ShinobiPlayer> SHINOBI_PLAYER = CapabilityManager.get(new CapabilityToken<>() {});

    private ShinobiPlayer shinobi = null;
    private final LazyOptional<ShinobiPlayer> optional = LazyOptional.of(this::createShinobiPlayer);

    private ShinobiPlayer createShinobiPlayer() {
        if (this.shinobi == null) {
            this.shinobi = new ShinobiPlayer();
        }

        return this.shinobi;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == SHINOBI_PLAYER) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return createShinobiPlayer().serialize();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createShinobiPlayer().deserialize(nbt);
    }
}
