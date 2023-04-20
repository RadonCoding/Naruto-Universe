package radon.naruto_universe.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.capability.data.NarutoDataHandler;
import radon.naruto_universe.network.PacketHandler;

import java.util.UUID;
import java.util.function.Supplier;

public class RequestNarutoDataC2SPacket {
    private final UUID src;

    public RequestNarutoDataC2SPacket(UUID uuid) {
        this.src = uuid;
    }

    public RequestNarutoDataC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.src);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            LivingEntity target = (LivingEntity) sender.getLevel().getEntity(this.src);

            if (target != null) {
                target.getCapability(NarutoDataHandler.INSTANCE).ifPresent(srcCap -> {
                    sender.getCapability(NarutoDataHandler.INSTANCE).ifPresent(dstCap -> dstCap.deserializeRemoteNBT(target.getUUID(), srcCap.serializeNBT()));
                    PacketHandler.sendToClient(new SyncNarutoDataRemoteS2CPacket(this.src, srcCap.serializeNBT()), sender);
                });
            }
        });
        ctx.setPacketHandled(true);
    }
}
