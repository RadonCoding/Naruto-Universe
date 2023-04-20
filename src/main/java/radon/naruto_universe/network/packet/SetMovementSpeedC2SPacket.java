package radon.naruto_universe.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;

import java.util.function.Supplier;

public class SetMovementSpeedC2SPacket {
    private final double speed;

    public SetMovementSpeedC2SPacket(double speed) {
        this.speed = speed;
    }

    public SetMovementSpeedC2SPacket(FriendlyByteBuf buf) {
        this(buf.readDouble());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.speed);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            assert player != null;

            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.setMovementSpeed(this.speed));
        });
        ctx.setPacketHandled(true);
    }
}
