package radon.naruto_universe.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.capability.MangekyoType;
import radon.naruto_universe.capability.ToggledEyes;
import radon.naruto_universe.client.genjutsu.ClientGenjutsuHandler;

import java.util.UUID;
import java.util.function.Supplier;

public class GenjutsuS2CPacket {
    private final int entityId;
    private final int duration;

    public GenjutsuS2CPacket(int entityId, int duration) {
        this.entityId = entityId;
        this.duration = duration;
    }

    public GenjutsuS2CPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.duration);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            assert player != null;

            ClientGenjutsuHandler.trigger(this.entityId, this.duration);
        }));
        ctx.setPacketHandled(true);
    }
}
