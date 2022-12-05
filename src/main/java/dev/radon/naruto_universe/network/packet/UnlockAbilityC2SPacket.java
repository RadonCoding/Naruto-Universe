package dev.radon.naruto_universe.network.packet;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.capability.NinjaPlayerHandler;
import dev.radon.naruto_universe.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UnlockAbilityC2SPacket {
    private final ResourceLocation key;

    public UnlockAbilityC2SPacket(ResourceLocation key) {
        this.key = key;
    }

    public UnlockAbilityC2SPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.key);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                Ability ability = AbilityRegistry.getValue(this.key);

                if (AbilityRegistry.getProgress(player, ability) >= 1.0F) {
                    cap.unlockAbility(this.key);
                    PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
                }
            });
        });
        ctx.setPacketHandled(true);

        return true;
    }
}
