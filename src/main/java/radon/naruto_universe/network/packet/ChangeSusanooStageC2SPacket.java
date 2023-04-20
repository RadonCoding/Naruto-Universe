package radon.naruto_universe.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.entity.SusanooEntity;

import java.util.function.Supplier;

public class ChangeSusanooStageC2SPacket {
    public static final int UP = 1;
    public static final int DOWN = -1;

    private final int direction;

    public ChangeSusanooStageC2SPacket(int direction) {
        this.direction = direction;
    }

    public ChangeSusanooStageC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.direction);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            assert player != null;

            if (player.getVehicle() instanceof SusanooEntity susanoo) {
                if (this.direction == UP) {
                    susanoo.incrementStage();
                }
                else if (this.direction == DOWN) { // Verbosity
                    susanoo.decrementStage();
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
