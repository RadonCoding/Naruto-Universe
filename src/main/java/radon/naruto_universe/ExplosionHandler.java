package radon.naruto_universe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.CameraShakeS2CPacket;
import radon.naruto_universe.sound.NarutoSounds;
import radon.naruto_universe.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;

public class ExplosionHandler {
    private static final List<ExplosionData> explosions = new ArrayList<>();

    public static void tick(ServerLevel level) {
        List<ExplosionData> remove = new ArrayList<>();

        for (ExplosionData explosion : explosions) {
            if (level.dimension() != explosion.dimension) return;

            float radius = Math.min(explosion.radius, explosion.radius * (0.5F + ((float) explosion.age / explosion.duration)));
            BlockPos center = new BlockPos(explosion.position);
            int minX = Mth.floor(center.getX() - radius - 1.0F);
            int maxX = Mth.floor(center.getX() + radius + 1.0F);
            int minY = Mth.floor(center.getY() - radius - 1.0F);
            int maxY = Mth.floor(center.getY() + radius + 1.0F);
            int minZ = Mth.floor(center.getZ() - radius - 1.0F);
            int maxZ = Mth.floor(center.getZ() + radius + 1.0F);

            if (explosion.age == 0) {
                level.playSound(null, explosion.position.x(), explosion.position.y(), explosion.position.z(), NarutoSounds.EXPLOSION.get(), SoundSource.MASTER, 10.0F, 1.0F);
                AABB bounds = new AABB(center.getX() - 100.0D, center.getY() - 100.0D, center.getZ() - 100.0D,
                        center.getX() + 100.0D, center.getY() + 100.0D, center.getZ() + 100.0D);

                for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, bounds)) {
                    PacketHandler.sendToClient(new CameraShakeS2CPacket(1.0F, 5.0F, explosion.duration), player);
                }
            }

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        double distance = (x - center.getX()) * (x - center.getX()) + (y - center.getY()) * (y - center.getY()) + (z - center.getZ()) * (z - center.getZ());

                        if (distance <= radius * radius) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockState state = level.getBlockState(pos);

                            if (state.getBlock().defaultDestroyTime() > -1.0F && !state.isAir()) {
                                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);

                                if (HelperMethods.RANDOM.nextInt(10) == 0) {
                                    level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
                                }

                                for (Entity entity : level.getEntities(null, new AABB(pos).inflate(10.0D))) {
                                    entity.hurt(DamageSource.explosion(null), explosion.radius);
                                }
                            }
                        }
                    }
                }
            }

            explosion.age++;

            if (explosion.age >= explosion.duration) {
                remove.add(explosion);
            }
        }
        explosions.removeAll(remove);
    }

    public static void spawn(ResourceKey<Level> dimension, Vec3 position, float radius, int duration) {
        explosions.add(new ExplosionData(dimension, position, radius, duration));
    }

    private static class ExplosionData {
        private final ResourceKey<Level> dimension;
        private final Vec3 position;
        private final float radius;
        private final int duration;
        private int age;

        public ExplosionData(ResourceKey<Level> dimension, Vec3 position, float radius, int duration) {
            this.dimension = dimension;
            this.position = position;
            this.radius = radius;
            this.duration = duration;
        }
    }
}
