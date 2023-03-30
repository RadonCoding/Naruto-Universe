package radon.naruto_universe.network.packet;

import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.NinjaPlayerHandler;
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

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            assert player != null;

            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                Ability ability = AbilityRegistry.getValue(this.key);

                if (ability.checkRequirements(player)) {
                    cap.unlockAbility(ability);
                    PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
                }
            });
        });
        ctx.setPacketHandled(true);

    }
}
