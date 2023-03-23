package radon.naruto_universe.ability.jutsu.fire_release;

import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.AABB;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.client.particle.ParticleRegistry;
import radon.naruto_universe.sound.SoundRegistry;

import java.util.List;
import java.util.Random;

public class GreatFlame extends Ability {
    public static final float BASE_DAMAGE = 2.5F;

    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.FIRE_RELEASE);
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.CHUNIN;
    }

    @Override
    public long getCombo() {
        return 213;
    }

    @Override
    public NinjaTrait getRelease() {
        return NinjaTrait.FIRE_RELEASE;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        return new AbilityDisplayInfo(iconPath, 5.0F, 2.0F);
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.HIDING_IN_ASH.get();
    }

    @Override
    public float getMinPower() {
        return 0.1F;
    }

    public ChatFormatting getChatColor() {
        return ChatFormatting.YELLOW;
    }

    @Override
    public float getCost() {
        return 30.0F;
    }

    @Override
    public void runClient(LocalPlayer player) {

    }

    @Override
    public void runServer(ServerPlayer player) {
        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            player.level.playSound(null, player.blockPosition(), SoundRegistry.GREAT_FLAME.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            cap.delayTickEvent((playerClone1) -> {
                final float power = this.getPower();

                final int duration = Math.max(1, Math.round(power));
                final int cloudSize = Math.round(10.0F + power);

                for (int i = 0; i < duration; i++) {
                    cap.delayTickEvent((playerClone2) -> {
                        double x = playerClone2.getX();
                        double y = playerClone2.getEyeY() - 0.2D;
                        double z = playerClone2.getZ();

                        x += playerClone2.getLookAngle().x();
                        y += playerClone2.getLookAngle().y();
                        z += playerClone2.getLookAngle().z();

                        final ServerLevel serverLevel = playerClone2.getLevel();
                        final Random rand = new Random();

                        for (int j = 0; j < cloudSize; j++) {
                            final AABB bounds = new AABB(x - 3.0D, y - 3.0D, z - 3.0D, x + 3.0D, y + 3.0D, z + 3.0D);

                            for (final Entity entity : serverLevel.getEntities(playerClone2, bounds)) {
                                entity.hurt(DamageSource.playerAttack(playerClone2), BASE_DAMAGE * power);
                                entity.setSecondsOnFire(Math.round(power));
                            }

                            BlockPos.betweenClosedStream(bounds).forEach(pos -> {
                                if (rand.nextInt(5) == 0 && serverLevel.getBlockState(pos).isAir() &&
                                        serverLevel.getBlockState(pos.below()).isSolidRender(serverLevel, pos.below())) {
                                    serverLevel.setBlockAndUpdate(pos, BaseFireBlock.getState(serverLevel, pos));
                                }
                            });

                            for (int k = 0; k < j; k++) {
                                final double offsetX = j * (rand.nextDouble() * (rand.nextBoolean() ? -1 : 1)) * 0.3D;
                                final double offsetY = j * (rand.nextDouble() * (rand.nextBoolean() ? -1 : 1)) * 0.3D;
                                final double offsetZ = j * (rand.nextDouble() * (rand.nextBoolean() ? -1 : 1)) * 0.3D;

                                serverLevel.sendParticles(playerClone2, ParticleRegistry.FLAME.get(), true, x + offsetX, y + offsetY, z + offsetZ,
                                        0, 1.0D, 1.0D, 1.0D, 0.01D);
                            }

                            x += playerClone2.getLookAngle().x();
                            y += playerClone2.getLookAngle().y();
                            z += playerClone2.getLookAngle().z();
                        }
                    }, i * 5);
                }
            }, 20);
        });
    }
}
