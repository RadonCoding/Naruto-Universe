package radon.naruto_universe.network.packet;

import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.sound.NarutoSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HandleHandSignC2SPacket {
    private final int handSign;

    public HandleHandSignC2SPacket(int handSign) {
        this.handSign = handSign;
    }

    public HandleHandSignC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.handSign);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            assert player != null;

            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.setPowerResetTimer(0));

            SoundEvent sound = switch (this.handSign) {
                case 1 -> NarutoSounds.HAND_SIGN_ONE.get();
                case 2 -> NarutoSounds.HAND_SIGN_TWO.get();
                case 3 -> NarutoSounds.HAND_SIGN_THREE.get();
                default -> null;
            };

            assert sound != null;

            player.level.playSound(null, player.blockPosition(),
                    sound, SoundSource.PLAYERS, 1.0F, 1.0F);
        });
        ctx.setPacketHandled(true);
    }
}
