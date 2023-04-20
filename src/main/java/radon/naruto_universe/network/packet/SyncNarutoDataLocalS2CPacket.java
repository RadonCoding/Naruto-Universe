package radon.naruto_universe.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.capability.data.NarutoDataHandler;

import java.util.function.Supplier;

public class SyncNarutoDataLocalS2CPacket {
    private final CompoundTag nbt;

    public SyncNarutoDataLocalS2CPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SyncNarutoDataLocalS2CPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            assert player != null;

            player.getCapability(NarutoDataHandler.INSTANCE).ifPresent(cap -> cap.deserializeLocalNBT(this.nbt));
        }));

        ctx.setPacketHandled(true);
    }
}