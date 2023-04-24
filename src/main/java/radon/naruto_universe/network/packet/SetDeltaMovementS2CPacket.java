package radon.naruto_universe.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.capability.data.NarutoDataHandler;

import java.util.function.Supplier;

public class SetDeltaMovementS2CPacket {
    private final Vec3 movement;

    public SetDeltaMovementS2CPacket(Vec3 movement) {
        this.movement = movement;
    }

    public SetDeltaMovementS2CPacket(FriendlyByteBuf buf) {
        this(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.movement.x());
        buf.writeDouble(this.movement.y());
        buf.writeDouble(this.movement.z());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            assert player != null;

            player.setDeltaMovement(this.movement);
        }));

        ctx.setPacketHandled(true);
    }
}