package radon.naruto_universe.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.entity.SusanooEntity;

import java.util.function.Supplier;

public class SyncSusanooAnimationS2CPacket {
    private final int id;
    private final SusanooEntity.SusanooAnimationState state;

    public SyncSusanooAnimationS2CPacket(int id, SusanooEntity.SusanooAnimationState state) {
        this.id = id;
        this.state = state;
    }

    public SyncSusanooAnimationS2CPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), new SusanooEntity.SusanooAnimationState(buf));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        this.state.serialize(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();

            assert mc.level != null;

            if (mc.level.getEntity(this.id) instanceof SusanooEntity susanoo) {
                susanoo.updateAnimationState(this.state);
            }
        }));

        ctx.setPacketHandled(true);
    }
}
