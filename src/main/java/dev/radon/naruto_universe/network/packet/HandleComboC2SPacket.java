package dev.radon.naruto_universe.network.packet;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.shinobi.ShinobiPlayerProvider;
import dev.radon.naruto_universe.sound.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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

                if (!ability.checkChakra(player)) {
                    canceled.set(true);
                }
            });
        }
        else {
            canceled.set(true);
        }

        if (!canceled.get()) {
            if (ability.activationType() == Ability.ActivationType.INSTANT) {
                ctx.enqueueWork(() -> {
                    ServerPlayer player = ctx.getSender();
                    ability.runServer(player);

                    player.level.playSound(null, player.blockPosition(),
                            SoundRegistry.ABILITY_ACTIVATE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.sendSystemMessage(ability.getMessage());
                });

                LocalPlayer player = Minecraft.getInstance().player;
                ability.runClient(player);
            } else if (ability.activationType() == Ability.ActivationType.CHANNELED) {
                ctx.enqueueWork(() -> {
                    ServerPlayer player = ctx.getSender();

                    player.getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).ifPresent(cap -> {
                        ResourceLocation key = AbilityRegistry.ABILITY_REGISTRY.get().getKey(ability);

                        if (ability instanceof Ability.Channeled channeled) {
                            if (cap.getChanneledAbility() != key) {
                                cap.startChanneledAbility(ability);

                                player.level.playSound(null, player.blockPosition(),
                                        SoundRegistry.ABILITY_ACTIVATE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                                player.sendSystemMessage(channeled.getStartMessage());
                            } else {
                                cap.stopChanneledAbility();
                                player.sendSystemMessage(channeled.getStopMessage());
                            }
                        }
                        PacketHandler.sendToClient(new SyncShinobiPlayerS2CPacket(cap.serialize()), player);
                    });
                });
            } else if (ability.activationType() == Ability.ActivationType.TOGGLED) {
                ctx.enqueueWork(() -> {
                    ServerPlayer player = ctx.getSender();

                    player.getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).ifPresent(cap -> {
                        ResourceLocation key = AbilityRegistry.ABILITY_REGISTRY.get().getKey(ability);

                        if (ability instanceof Ability.Toggled toggled) {
                            if (!cap.getToggledAbilities().contains(key)) {
                                cap.enableToggledAbility(ability);

                                player.level.playSound(null, player.blockPosition(),
                                        SoundRegistry.ABILITY_ACTIVATE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                                player.sendSystemMessage(toggled.getEnableMessage());
                            } else {
                                cap.disableToggledAbility(ability);
                                player.sendSystemMessage(toggled.getDisableMessage());
                            }
                        }
                        PacketHandler.sendToClient(new SyncShinobiPlayerS2CPacket(cap.serialize()), player);
                    });
                });
            }
        }

        ctx.setPacketHandled(true);

        return true;
    }
}
