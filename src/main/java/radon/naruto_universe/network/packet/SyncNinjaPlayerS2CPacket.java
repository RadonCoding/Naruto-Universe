package radon.naruto_universe.network.packet;

import radon.naruto_universe.client.gui.NinjaScreen;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncNinjaPlayerS2CPacket {
    private final CompoundTag nbt;

    public SyncNinjaPlayerS2CPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public SyncNinjaPlayerS2CPacket(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            assert player != null;

            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.deserializeNBT(this.nbt));

            if (mc.screen instanceof NinjaScreen screen) {
                screen.updateAbilities();
            }
        }));
        ctx.setPacketHandled(true);

    }
}
