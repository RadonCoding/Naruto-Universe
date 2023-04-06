package radon.naruto_universe.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityHandler;
import radon.naruto_universe.ability.NarutoAbilities;

import java.util.function.Supplier;

public class TriggerAbilityC2SPacket {
    private final ResourceLocation key;

    public TriggerAbilityC2SPacket(ResourceLocation key) {
        this.key = key;
    }

    public TriggerAbilityC2SPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.key);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        Ability ability = NarutoAbilities.getValue(this.key);

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            AbilityHandler.triggerAbility(player, ability);
        });
        ctx.setPacketHandled(true);
    }
}
