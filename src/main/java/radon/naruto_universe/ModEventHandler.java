package radon.naruto_universe;

import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.INinjaPlayer;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaTrait;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventHandler {
    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.register();
        AbilityRegistry.registerCombos();
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
            });
        }
    }

    @SubscribeEvent
    public static void onLivingFall(final LivingFallEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDistance() < 20.0F) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPLayerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap ->
                    PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), serverPlayer));
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap ->
                    PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), serverPlayer));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(final PlayerEvent.Clone event) {
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
    public static void onPlayerWakeUp(final PlayerWakeUpEvent event) {
        final Player player = event.getEntity();

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            cap.setChakra(cap.getMaxChakra());
        });
    }

    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof TamableAnimal tamable) {
            Entity owner = tamable.getOwner();

            if (owner instanceof ServerPlayer player) {
                if (event.getSource().getEntity() != null) {
                    // BAD BOY !!!
                    if (event.getSource().getEntity().is(player)) {
                        return;
                    }
                }

                player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                    if (!cap.hasTrait(NinjaTrait.SHARINGAN) || cap.hasTrait(NinjaTrait.UNLOCKED_SHARINGAN)) {
                        return;
                    }

                    cap.levelUpSharingan();
                    cap.addTrait(NinjaTrait.UNLOCKED_SHARINGAN);

                    player.sendSystemMessage(Component.translatable("sharingan.unlock.one"));
                    PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
        event.player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.tick(event.player, event.side, event.phase));
    }

    @SubscribeEvent
    public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!player.getCapability(NinjaPlayerHandler.INSTANCE).isPresent()) {
                NinjaPlayerHandler.attach(event);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterCapabilities(final RegisterCapabilitiesEvent event) {
            event.register(INinjaPlayer.class);
        }
    }
}
