package radon.naruto_universe.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.capability.data.NarutoDataHandler;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncNarutoDataRemoteS2CPacket {
    private final UUID src;
    private final CompoundTag nbt;

    public SyncNarutoDataRemoteS2CPacket(UUID src, CompoundTag nbt) {
        this.src = src;
        this.nbt = nbt;
    }

    public SyncNarutoDataRemoteS2CPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.src);
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            assert player != null;

            player.getCapability(NarutoDataHandler.INSTANCE).ifPresent(cap -> {
                if (player.getUUID().equals(this.src)) {
                    cap.deserializeLocalNBT(this.nbt);
                } else {
                    cap.deserializeRemoteNBT(this.src, this.nbt);
                }
            });
        }));

        ctx.setPacketHandled(true);
    }
}