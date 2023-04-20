package radon.naruto_universe.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.capability.ninja.MangekyoType;
import radon.naruto_universe.capability.ninja.ToggledEyes;
import radon.naruto_universe.client.genjutsu.ClientGenjutsuHandler;

import java.util.function.Supplier;

public class GenjutsuS2CPacket {
    private final ToggledEyes eyes;
    private final int duration;

    public GenjutsuS2CPacket(ToggledEyes eyes, int duration) {
        this.eyes = eyes;
        this.duration = duration;
    }

    public GenjutsuS2CPacket(FriendlyByteBuf buf) {
        this(new ToggledEyes(buf), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        this.eyes.serialize(buf);
        buf.writeInt(this.duration);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            assert player != null;

            ClientGenjutsuHandler.trigger(this.eyes, this.duration);
        }));
        ctx.setPacketHandled(true);
    }
}
