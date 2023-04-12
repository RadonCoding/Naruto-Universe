package radon.naruto_universe.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.ToggledEyes;
import radon.naruto_universe.network.PacketHandler;

import java.util.UUID;
import java.util.function.Supplier;

public class EyeStatusRequestC2SPacket {
    private final UUID srcUUID;

    public EyeStatusRequestC2SPacket(UUID srcUUID) {
        this.srcUUID = srcUUID;
    }

    public EyeStatusRequestC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.srcUUID);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            Entity src = sender.getLevel().getEntity(this.srcUUID);

            if (src != null) {
                src.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                    Ability current = cap.getCurrentEyes();

                    if (current != null) {
                        ToggledEyes eyes = new ToggledEyes(current.getId(), cap.getSharinganLevel(), cap.getMangekyoType());
                        PacketHandler.sendToClient(new EyeStatusResponseS2CPacket(src.getUUID(), eyes), sender);
                    }
                });
            }
        });
    }
}