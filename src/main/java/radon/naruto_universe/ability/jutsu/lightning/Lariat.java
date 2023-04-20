package radon.naruto_universe.ability.jutsu.lightning;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.NinjaRank;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.client.CameraShakeHandler;
import radon.naruto_universe.client.ability.ClientAbilityHandler;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SetMovementSpeedC2SPacket;
import radon.naruto_universe.network.packet.TriggerAbilityC2SPacket;
import radon.naruto_universe.sound.NarutoSounds;

import java.util.Random;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Lariat extends Ability {
    private static final float LAUNCH_POWER = 20.0F;
    private static final float REQUIRED_SPEED = 1.5F;
    private static final float SPEED_DAMAGE_MULTIPLIER = 50.0F;

    @Override
    public NinjaRank getRank() {
        return NinjaRank.CHUNIN;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return new AbilityDisplayInfo(this.getId().getPath(), 2.0F, 3.0F);
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.POWER_CHARGE.get();
    }

    @Override
    public NinjaTrait getRelease() {
        return NinjaTrait.LIGHTNING_RELEASE;
    }

    @Override
    public boolean shouldLog(LivingEntity owner) {
        return true;
    }

    @Override
    public SoundEvent getActivationSound() {
        return null;
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!event.getSource().msgId.equals("mob") && !event.getSource().msgId.equals("player")) {
            return;
        }

        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (attacker.level.isClientSide) {
                attacker.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                    if (cap.hasUnlockedAbility(NarutoAbilities.LARIAT.get())) {
                        Vec3 movement = attacker.getDeltaMovement();
                        double speed = Math.sqrt(movement.x() * movement.x() + movement.z() * movement.z());

                        if (speed >= REQUIRED_SPEED) {
                            Ability ability = NarutoAbilities.LARIAT.get();
                            PacketHandler.sendToServer(new SetMovementSpeedC2SPacket(speed));
                            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(ability.getId()));

                            attacker.setLastHurtMob(event.getEntity());
                            ClientAbilityHandler.triggerAbility(ability);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void runClient(LivingEntity owner) {
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            LivingEntity target = owner.getLastHurtMob();

            if (target != null) {
                Vec3 look = owner.getLookAngle();
                target.setDeltaMovement(look.x() * LAUNCH_POWER, look.y() * LAUNCH_POWER, look.z() * LAUNCH_POWER);

                owner.moveTo(target.position());
                owner.setDeltaMovement(target.getDeltaMovement());
            }
        });

        if (owner instanceof LocalPlayer) {
            CameraShakeHandler.shakeCamera(2.5F, 5.0F, 5);
        }
    }

    @Override
    public void runServer(LivingEntity owner) {
        // The Lariat has to be delayed by 1 tick to allow the LivingEntity class to set the last hurt mob.

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            owner.level.playSound(null, owner.blockPosition(), NarutoSounds.LARIAT.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            cap.delayTickEvent((ownerClone) -> {
                LivingEntity target = ownerClone.getLastHurtMob();

                if (target != null) {
                    Vec3 look = ownerClone.getLookAngle();
                    target.setDeltaMovement(look.x() * LAUNCH_POWER, look.y() * LAUNCH_POWER, look.z() * LAUNCH_POWER);

                    ownerClone.moveTo(target.position());
                    ownerClone.setDeltaMovement(target.getDeltaMovement());

                    DamageSource source = owner instanceof Player ? DamageSource.playerAttack((Player) owner) : DamageSource.mobAttack(owner);
                    target.hurt(source, (float) (cap.getMovementSpeed() * SPEED_DAMAGE_MULTIPLIER));

                    Random rand = new Random();

                    for (int i = 0; i < 5; i++) {
                        ServerLevel level = (ServerLevel) ownerClone.level;

                        float f = (rand.nextFloat() - 0.5F) * 4.0F;
                        float f1 = (rand.nextFloat() - 0.5F) * 2.0F;
                        float f2 = (rand.nextFloat() - 0.5F) * 4.0F;
                        level.sendParticles(ParticleTypes.EXPLOSION,
                                target.getX() + f,
                                target.getY() + 2.0D + f1,
                                target.getZ() + f2,
                                0, 1.0D, 1.0D, 1.0D, 0.1D);
                    }
                }
            }, 1, LogicalSide.SERVER);
        });
    }
}