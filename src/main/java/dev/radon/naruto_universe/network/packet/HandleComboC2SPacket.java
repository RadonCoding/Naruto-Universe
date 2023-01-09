package dev.radon.naruto_universe.network.packet;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.capability.NinjaPlayerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class HandleComboC2SPacket {
    private final long combo;

    public HandleComboC2SPacket(long combo) {
        this.combo = combo;
    }

    public HandleComboC2SPacket(FriendlyByteBuf buf) {
        this(buf.readLong());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(this.combo);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        Ability ability = AbilityRegistry.getAbility(this.combo);

        AtomicBoolean canceled = new AtomicBoolean(false);

        if (ability != null) {
            ctx.enqueueWork(() -> {
                ServerPlayer player = ctx.getSender();

                if (!ability.isUnlocked(player) || !ability.checkRequirements(player) || !ability.checkChakra(player)) {
                    canceled.set(true);
                }
            });
        }
        else {
            canceled.set(true);
        }

        if (!canceled.get()) {
            if (ability.getActivationType() == Ability.ActivationType.INSTANT) {
                LocalPlayer localPlayer = Minecraft.getInstance().player;
                ability.runClient(localPlayer);

                ctx.enqueueWork(() -> {
                    ServerPlayer serverPlayer = ctx.getSender();
                    ability.runServer(serverPlayer);

                    SoundEvent sound = ability.getActivationSound();

                    if (sound != null) {
                        serverPlayer.level.playSound(null, serverPlayer.blockPosition(),
                                sound, SoundSource.PLAYERS, 10.0F, 1.0F);
                    }

                    if (ability.shouldLog()) {
                        serverPlayer.sendSystemMessage(ability.getName());
                    }
                });
            } else if (ability.getActivationType() == Ability.ActivationType.CHANNELED) {
                ctx.enqueueWork(() -> {
                    ServerPlayer player = ctx.getSender();

                    player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                        ResourceLocation key = AbilityRegistry.getKey(ability);

                        if (ability instanceof Ability.Channeled) {
                            if (cap.getChanneledAbility() != key) {
                                cap.setChanneledAbility(player, ability);
                            } else {
                                cap.stopChanneledAbility(player);
                            }
                        }
                        PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
                    });
                });
            } else if (ability.getActivationType() == Ability.ActivationType.TOGGLED) {
                ctx.enqueueWork(() -> {
                    ServerPlayer player = ctx.getSender();

                    player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                        if (ability instanceof Ability.Toggled) {
                            if (!cap.hasToggledAbility(ability)) {
                                cap.enableToggledAbility(player, ability);
                            } else {
                                cap.disableToggledAbility(player, ability);
                            }
                        }
                        PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
                    });
                });
            }
        }

        ctx.setPacketHandled(true);

        return true;
    }
}
