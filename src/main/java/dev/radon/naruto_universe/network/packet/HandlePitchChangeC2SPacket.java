package dev.radon.naruto_universe.network.packet;

import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.capability.NinjaPlayerHandler;
import dev.radon.naruto_universe.client.gui.widget.PitchSlider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HandlePitchChangeC2SPacket {
    private final float pitch;

    public HandlePitchChangeC2SPacket(float pitch) {
        this.pitch = pitch;
    }

    public HandlePitchChangeC2SPacket(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(this.pitch);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                if (this.pitch < PitchSlider.MIN_VALUE || this.pitch > PitchSlider.MAX_VALUE) {
                    return;
                }
                cap.setVoicePitch(this.pitch);
                PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
            });
        });
        ctx.setPacketHandled(true);

        return true;
    }
}
