package radon.naruto_universe.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.capability.MangekyoType;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.ToggledEyes;

import java.util.UUID;
import java.util.function.Supplier;

public class ClearEyeStatusS2CPacket {
    private final UUID srcUUID;

    public ClearEyeStatusS2CPacket(UUID srcUUID) {
        this.srcUUID = srcUUID;
    }

    public ClearEyeStatusS2CPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.srcUUID);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            assert player != null;

            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.removeToggledEyes(this.srcUUID));
        }));
        ctx.setPacketHandled(true);
    }
}
