package radon.naruto_universe.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.network.PacketHandler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class TriggerAbilityPacket {
    private final ResourceLocation key;

    public TriggerAbilityPacket(ResourceLocation key) {
        this.key = key;
    }

    public TriggerAbilityPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.key);
    }

    private void setChanneledAbility(Player player, Ability ability) {
        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (ability instanceof Ability.IChanneled) {
                if (cap.getChanneledAbility() != ability) {
                    cap.setChanneledAbility(player, ability);
                } else {
                    cap.stopChanneledAbility(player);
                }
            }
        });
    }

    private void setToggledAbility(Player player, Ability ability) {
        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (ability instanceof Ability.IToggled) {
                if (!cap.hasToggledAbility(ability)) {
                    cap.enableToggledAbility(player, ability);
                } else {
                    cap.disableToggledAbility(player, ability);
                }

                if (ability.isDojutsu()) {
                    cap.clearToggledDojutsus(player, ability);
                }
            }
        });
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        Ability ability = AbilityRegistry.getValue(this.key);

        AtomicBoolean canceled = new AtomicBoolean(false);

        if (ability != null) {
            ctx.enqueueWork(() -> {
                ServerPlayer player = ctx.getSender();

                assert player != null;

                if (!ability.isUnlocked(player) || /*!ability.checkRequirements(player) ||*/ ability.checkChakra(player)) {
                    canceled.set(true);
                }
            });
        }
        else {
            canceled.set(true);
        }

        if (!canceled.get()) {
            ctx.enqueueWork(() -> {
                assert ability != null;

                if (ability.getMinPower() > 0.0F) {
                    ServerPlayer player = ctx.getSender();
                    assert player != null;
                    player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.addPower(0.0F));
                }
            });

            LocalPlayer localPlayer = Minecraft.getInstance().player;

            assert localPlayer != null;
            assert ability != null;

            if (ability.getActivationType() == Ability.ActivationType.INSTANT) {
                ability.runClient(localPlayer);

                ctx.enqueueWork(() -> {
                    ServerPlayer serverPlayer = ctx.getSender();
                    ability.runServer(serverPlayer);

                    SoundEvent sound = ability.getActivationSound();

                    assert serverPlayer != null;

                    if (sound != null) {
                        serverPlayer.level.playSound(null, serverPlayer.blockPosition(), sound, SoundSource.PLAYERS, 10.0F, 1.0F);
                    }
                });

                if (ability.shouldLog(localPlayer)) {
                    localPlayer.sendSystemMessage(ability.getChatMessage());
                }
            } else if (ability.getActivationType() == Ability.ActivationType.CHANNELED) {
                this.setChanneledAbility(localPlayer, ability);

                ctx.enqueueWork(() -> {
                    ServerPlayer serverPlayer = ctx.getSender();

                    assert serverPlayer != null;

                    this.setChanneledAbility(serverPlayer, ability);
                });
            } else if (ability.getActivationType() == Ability.ActivationType.TOGGLED) {
                this.setToggledAbility(localPlayer, ability);

                ctx.enqueueWork(() -> {
                    ServerPlayer serverPlayer = ctx.getSender();

                    assert serverPlayer != null;

                    this.setToggledAbility(serverPlayer, ability);
                });
            }
        }
        ctx.setPacketHandled(true);
    }
}
