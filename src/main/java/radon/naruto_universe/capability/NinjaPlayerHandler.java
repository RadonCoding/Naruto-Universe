package radon.naruto_universe.capability;

import net.minecraft.world.entity.player.Player;
import radon.naruto_universe.NarutoUniverse;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NinjaPlayerHandler {
    public static final Capability<INinjaPlayer> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void attach(final AttachCapabilitiesEvent<Entity> event) {
        final NinjaPlayerProvider provider = new NinjaPlayerProvider();
        event.addCapability(NinjaPlayerProvider.IDENTIFIER, provider);

        Player player = (Player) event.getObject();
        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.generateShinobi(player));
    }

    private static class NinjaPlayerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static final ResourceLocation IDENTIFIER = new ResourceLocation(NarutoUniverse.MOD_ID, "shinobi_player");

        private final INinjaPlayer cap = new NinjaPlayer();
        private final LazyOptional<INinjaPlayer> optional = LazyOptional.of(() -> this.cap);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == INSTANCE ? this.optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.cap.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.cap.deserializeNBT(nbt);
        }
    }
}
