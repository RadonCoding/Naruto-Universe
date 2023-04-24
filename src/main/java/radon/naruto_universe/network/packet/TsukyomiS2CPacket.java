package radon.naruto_universe.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.client.genjutsu.TsukuyomiHandler;

import java.util.function.Supplier;

public class TsukyomiS2CPacket {
    private final int duration;

    public TsukyomiS2CPacket(int duration) {
        this.duration = duration;
    }

    public TsukyomiS2CPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.duration);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            TsukuyomiHandler.trigger(this.duration);
        }));

        ctx.setPacketHandled(true);
    }
}
