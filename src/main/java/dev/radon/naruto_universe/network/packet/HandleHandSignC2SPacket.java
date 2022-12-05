package dev.radon.naruto_universe.network.packet;

import dev.radon.naruto_universe.sound.SoundRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HandleHandSignC2SPacket {
    public HandleHandSignC2SPacket() {

    }

    public HandleHandSignC2SPacket(FriendlyByteBuf buf) {
        this();
    }

    public void encode(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            player.level.playSound(null, player.blockPosition(),
                    SoundRegistry.HAND_SIGN.get(), SoundSource.PLAYERS, 10.0F, 1.0F);

        });

        ctx.setPacketHandled(true);

        return true;
    }
}
