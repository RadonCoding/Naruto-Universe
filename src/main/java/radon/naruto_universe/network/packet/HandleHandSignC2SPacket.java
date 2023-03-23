package radon.naruto_universe.network.packet;

import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.sound.SoundRegistry;
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

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                cap.setPowerResetTimer(0);
            });

            SoundEvent sound = null;

            switch (this.handSign) {
                case 1:
                    sound = SoundRegistry.HAND_SIGN_ONE.get();
                    break;
                case 2:
                    sound = SoundRegistry.HAND_SIGN_TWO.get();
                    break;
                case 3:
                    sound = SoundRegistry.HAND_SIGN_THREE.get();
                    break;
            }

            player.level.playSound(null, player.blockPosition(),
                    sound, SoundSource.PLAYERS, 10.0F, 1.0F);
        });

        ctx.setPacketHandled(true);

        return true;
    }
}
