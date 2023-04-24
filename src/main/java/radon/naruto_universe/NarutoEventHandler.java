package radon.naruto_universe;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.capability.data.INarutoData;
import radon.naruto_universe.capability.data.NarutoDataHandler;
import radon.naruto_universe.capability.ninja.INinjaPlayer;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.entity.NarutoEntities;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SyncNarutoDataLocalS2CPacket;
import radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;

public class NarutoEventHandler {
    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player));
                player.getCapability(NarutoDataHandler.INSTANCE).ifPresent(cap -> PacketHandler.sendToClient(new SyncNarutoDataLocalS2CPacket(cap.serializeNBT()), player));
            }
        }

        @SubscribeEvent
        public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                serverPlayer.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap ->
                        PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), serverPlayer));
            }
        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                serverPlayer.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap ->
                        PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), serverPlayer));
            }
        }

        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            Player original = event.getOriginal();
            Player player = event.getEntity();

            original.reviveCaps();

            original.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(oldCap -> {
                player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(newCap -> {
                    newCap.deserializeNBT(oldCap.serializeNBT());

                    if (event.isWasDeath()) {
                        newCap.setChakra(newCap.getMaxChakra());
                        newCap.clearToggledAbilities();
                    }
                });
            });
            original.invalidateCaps();
        }

        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity living) {
                if (living instanceof Player player) {
                    if (!player.getCapability(NinjaPlayerHandler.INSTANCE).isPresent()) {
                        NinjaPlayerHandler.attach(event);
                    }
                }

                if (!living.getCapability(NarutoDataHandler.INSTANCE).isPresent()) {
                    NarutoDataHandler.attach(event);
                }
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
        public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
            Player player = event.getEntity();

            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                cap.setChakra(cap.getMaxChakra());
            });
        }

        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity entity = event.getEntity();
            entity.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.tick(entity, entity.level.isClientSide));
            entity.getCapability(NarutoDataHandler.INSTANCE).ifPresent(cap -> cap.tick(entity, entity.level.isClientSide));
        }

        @SubscribeEvent
        public static void onLevelTick(TickEvent.LevelTickEvent event) {
            if (!event.level.isClientSide) {
                ExplosionHandler.tick((ServerLevel) event.level);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onCreateAttributes(EntityAttributeCreationEvent event) {
            NarutoEntities.createAttributes(event);
        }

        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(INinjaPlayer.class);
            event.register(INarutoData.class);
        }
    }
}
