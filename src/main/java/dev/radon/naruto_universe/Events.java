package dev.radon.naruto_universe;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.network.packet.SyncShinobiPlayerS2CPacket;
import dev.radon.naruto_universe.shinobi.ShinobiPlayer;
import dev.radon.naruto_universe.shinobi.ShinobiPlayerProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

public class Events {
    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(ShinobiPlayer.class);
        }
    }

    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player player) {
                if (!player.getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).isPresent()) {
                    event.addCapability(new ResourceLocation(NarutoUniverse.MOD_ID, "shinobi_player"),
                            new ShinobiPlayerProvider());
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).ifPresent(cap ->
                        PacketHandler.sendToClient(new SyncShinobiPlayerS2CPacket(cap.serialize()),
                                player));
            }
        }

        @SubscribeEvent
        public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
            if (event.getEntity() instanceof Player player) {
                player.getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).ifPresent(cap -> {
                    if (cap.hasToggledAbility(AbilityRegistry.CHAKRA_CONTROL.get())) {
                        Vec3 movement = player.getDeltaMovement();
                        player.setDeltaMovement(movement.multiply(3.0D, 3.0D, 3.0D));
                    }
                });
            }
        }

        @SubscribeEvent
        public static void onLivingFall(LivingFallEvent event) {
            if (event.getEntity() instanceof Player) {
                if (event.getDistance() < 20.0F) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            if (event.isWasDeath()) {
                event.getOriginal().getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).ifPresent(oldStore ->
                        event.getEntity().getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).ifPresent(newStore ->
                                newStore.copyFrom(oldStore)));
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            event.player.getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).ifPresent(cap -> {
                cap.updateDelayedTickEvents();

                Ability channeled = AbilityRegistry.ABILITY_REGISTRY.get()
                        .getValue(cap.getChanneledAbility());

                if (channeled != null) {
                    if (event.side == LogicalSide.CLIENT) {
                        channeled.runClient((LocalPlayer) event.player);
                    }
                    else {
                        channeled.runServer((ServerPlayer) event.player);
                    }
                }

                for (var key : cap.getToggledAbilities()) {
                    Ability toggled = AbilityRegistry.ABILITY_REGISTRY.get()
                            .getValue(key);

                    if (event.side == LogicalSide.CLIENT) {
                        toggled.runClient((LocalPlayer) event.player);
                    }
                    else {
                        if (!toggled.checkChakra((ServerPlayer) event.player)) {
                            cap.disableToggledAbility(toggled);
                        }
                        toggled.runServer((ServerPlayer) event.player);
                    }
                }
            });
        }
    }
}
