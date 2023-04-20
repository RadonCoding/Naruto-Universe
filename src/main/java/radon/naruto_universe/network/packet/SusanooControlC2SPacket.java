package radon.naruto_universe.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.entity.SusanooEntity;

import java.util.function.Supplier;

public class SusanooControlC2SPacket {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    private final int click;

    public SusanooControlC2SPacket(int click) {
        this.click = click;
    }

    public SusanooControlC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.click);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            assert player != null;

            if (player.getVehicle() instanceof SusanooEntity susanoo) {
                if (this.click == LEFT) {
                    susanoo.onLeftClick();
                } else if (this.click == RIGHT) { // Verbosity
                    susanoo.onRightClick();
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
