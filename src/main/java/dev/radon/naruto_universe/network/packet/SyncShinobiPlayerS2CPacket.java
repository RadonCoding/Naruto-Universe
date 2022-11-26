package dev.radon.naruto_universe.network.packet;

import dev.radon.naruto_universe.shinobi.ShinobiPlayerProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncShinobiPlayerS2CPacket {
    private final CompoundTag nbt;

    public SyncShinobiPlayerS2CPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SyncShinobiPlayerS2CPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            player.getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).ifPresent(cap -> cap.deserialize(this.nbt));
        });
        ctx.setPacketHandled(true);

        return true;
    }
}
