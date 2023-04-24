package radon.naruto_universe.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.jutsu.lightning.Lariat;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.client.ability.ClientAbilityHandler;
import radon.naruto_universe.entity.SusanooEntity;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;
import radon.naruto_universe.network.packet.TriggerAbilityC2SPacket;
import radon.naruto_universe.network.packet.TriggerLariatC2SPacket;

import java.util.Random;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AbilityEvents {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
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
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof EntityHitResult result) {
            Entity entity = result.getEntity();

            entity.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                if (cap.hasToggledAbility(NarutoAbilities.SHARINGAN.get()) || cap.hasToggledAbility(NarutoAbilities.MANGEKYO.get())) {
                    Random rand = new Random();
                    double speed = Math.sqrt(entity.getDeltaMovement().x() * entity.getDeltaMovement().x() + entity.getDeltaMovement().y() * entity.getDeltaMovement().y() + entity.getDeltaMovement().z() * entity.getDeltaMovement().z());
                    double chance = 1.0D / (1.0D + Math.abs(speed));

                    if (rand.nextDouble() < chance) {
                        if (entity.level instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.CLOUD, entity.getX(), entity.getY() + (entity.getBbHeight() / 2.0F), entity.getZ(), 0,
                                    0.0D, 0.0D, 0.0D, 0.0D);
                        }

                        BlockPos pos = entity.blockPosition();
                        double diffX = entity.getX() - pos.getX();
                        double diffZ = entity.getZ() - pos.getZ();

                        Vec3 movement = entity.getDeltaMovement();

                        if (diffX <= 0.1D) {
                            movement = new Vec3(rand.nextDouble(), 0.0D, movement.z());
                        } else if (diffX >= 0.9D) {
                            movement = new Vec3(-rand.nextDouble(), 0.0D, movement.z());
                        } else if (diffZ <= 0.1D) {
                            movement = new Vec3(movement.x(), 0.0D, rand.nextDouble());
                        } else if (diffZ >= 0.9D) {
                            movement = new Vec3(movement.x(), 0.0D, -rand.nextDouble());
                        } else {
                            movement = new Vec3(rand.nextDouble() * 2.0D - 1.0D, 0.0D, rand.nextDouble() * 2.0D - 1.0D);
                        }

                        entity.setDeltaMovement(movement);
                        event.setCanceled(true);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        DamageSource source = event.getSource();

        if (!source.isBypassArmor()) {
            LivingEntity owner = event.getEntity();

            owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                if (cap.hasToggledAbility(NarutoAbilities.SUSANOO.get())) {
                    if (owner.getVehicle() instanceof SusanooEntity susanoo) {
                        susanoo.hurt(source, event.getAmount());
                        event.setCanceled(true);
                    }
                }
            });
        }

        if (event.getSource().msgId.equals("mob") || event.getSource().msgId.equals("player")) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                attacker.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                    if (cap.hasUnlockedAbility(NarutoAbilities.LARIAT.get())) {
                        Vec3 movement = attacker.getDeltaMovement();
                        double speed = Math.sqrt(movement.x() * movement.x() + movement.z() * movement.z());

                        Entity target = event.getEntity();

                        if (attacker instanceof Player) {
                            if (attacker.level.isClientSide) {
                                if (speed >= Lariat.REQUIRED_SPEED) {
                                    attacker.setLastHurtMob(target);
                                    ClientAbilityHandler.triggerAbility(NarutoAbilities.LARIAT.get());

                                    PacketHandler.sendToServer(new TriggerLariatC2SPacket(movement, target.getId()));
                                }
                            }
                        } else {
                            if (speed >= Lariat.REQUIRED_SPEED) {
                                attacker.setLastHurtMob(target);

                                if (attacker.level.isClientSide) {
                                    ClientAbilityHandler.triggerAbility(NarutoAbilities.LARIAT.get());
                                } else {
                                    AbilityHandler.triggerAbility(attacker, NarutoAbilities.LARIAT.get());
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}
