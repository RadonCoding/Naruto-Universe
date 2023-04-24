package radon.naruto_universe.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityHandler;
import radon.naruto_universe.ability.NarutoAbilities;

import java.util.function.Supplier;

public class TriggerLariatC2SPacket {
    private final Vec3 movement;
    private final int targetId;

    public TriggerLariatC2SPacket(Vec3 movement, int targetId) {
        this.movement = movement;
        this.targetId = targetId;
    }

    public TriggerLariatC2SPacket(FriendlyByteBuf buf) {
        this(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.movement.x());
        buf.writeDouble(this.movement.y());
        buf.writeDouble(this.movement.z());
        buf.writeInt(this.targetId);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();

            assert player != null;

            Entity target = player.level.getEntity(this.targetId);

            if (target != null) {
                player.setDeltaMovement(this.movement);
                player.setLastHurtMob(target);
                AbilityHandler.triggerAbility(player, NarutoAbilities.LARIAT.get());
            }
        });
        ctx.setPacketHandled(true);
    }
}
