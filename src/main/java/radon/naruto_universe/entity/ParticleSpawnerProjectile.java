package radon.naruto_universe.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.NarutoDamageSource;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.client.particle.NarutoParticles;
import radon.naruto_universe.util.HelperMethods;

public class ParticleSpawnerProjectile extends JutsuProjectile {
    private boolean fire;

    private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.PARTICLE);
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TICKS = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_RANGE = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_THICKNESS = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.FLOAT);

    public ParticleSpawnerProjectile(EntityType<? extends ParticleSpawnerProjectile> pEntityType, Level level) {
        super(pEntityType, level);

        this.setInvisible(true);
    }

    public ParticleSpawnerProjectile(LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, float power, float damage, NinjaTrait release, ParticleOptions particle, int lifetime, float range, float radius, float thickness, boolean fire) {
        super(NarutoEntities.PARTICLE_SPAWNER.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, power, damage, release);

        this.entityData.set(DATA_PARTICLE, particle);
        this.entityData.set(DATA_PARTICLE, particle);
        this.entityData.set(DATA_LIFETIME, lifetime);
        this.entityData.set(DATA_RANGE, range);
        this.entityData.set(DATA_RADIUS, radius);
        this.entityData.set(DATA_THICKNESS, thickness);

        this.fire = fire;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_PARTICLE, NarutoParticles.EMPTY.get());
        this.entityData.define(DATA_LIFETIME, 0);
        this.entityData.define(DATA_TICKS, 0);
        this.entityData.define(DATA_RANGE, 0.0F);
        this.entityData.define(DATA_RADIUS, 0.0F);
        this.entityData.define(DATA_THICKNESS, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putBoolean("fire", this.fire);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.fire = pCompound.getBoolean("fire");
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    private int getTicks() {
        return this.entityData.get(DATA_TICKS);
    }
    private void setTicks(int ticks) {
        this.entityData.set(DATA_TICKS, ticks);
    }

    private int getLifetime() {
        return this.entityData.get(DATA_LIFETIME);
    }

    private float getRange() {
        return this.entityData.get(DATA_RANGE);
    }
    private float getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }
    private float getThickness() {
        return this.entityData.get(DATA_THICKNESS);
    }

    @Override
    public void tick() {
        super.tick();
        
        Entity owner = this.getOwner();

        int ticks = this.getTicks();
        int lifetime = this.getLifetime();

        if (ticks >= lifetime) {
            this.discard();
        }
        else if (!this.level.isClientSide && (owner == null || !owner.isAlive())) {
            this.discard();
        }
        else if (owner != null) {
            float f0 = 1.0F - (float) ticks / lifetime;

            float power = Math.max(10.0F, this.getPower());
            float range = this.getRange() * (power * 0.1F) * f0;
            float radius = this.getRadius() * (power * 0.1F) * f0;

            Vec3 look = owner.getLookAngle();
            double angle = Math.atan(radius / range) * 180.0D / Math.PI;

            if (!this.level.isClientSide) {
                HitResult result = HelperMethods.getHitResult(owner, range, radius);

                if (result != null) {
                    if (result.getType() == HitResult.Type.ENTITY) {
                        EntityHitResult hit = (EntityHitResult) result;

                        Entity entity = hit.getEntity();

                        if (entity != owner) {
                            if (entity.hurt(NarutoDamageSource.jutsu(this, owner), this.getDamage())) {
                                if (this.getRelease() == NinjaTrait.FIRE_RELEASE) {
                                    entity.setSecondsOnFire(Math.round(this.getPower()));
                                }
                            }
                        }
                        this.onHitEntity(hit);
                    } else if (result.getType() == HitResult.Type.BLOCK) {
                        BlockHitResult hit = (BlockHitResult) result;

                        if (this.fire) {
                            BlockPos pos = hit.getBlockPos();
                            BlockState state = this.level.getBlockState(pos);
                            Block block = state.getBlock();

                            if (block != Blocks.AIR) {
                                if (block.isFlammable(state, this.level, pos, null)) {
                                    this.level.setBlockAndUpdate(pos, BaseFireBlock.getState(this.level, pos));
                                }
                            }
                        }
                        this.onHitBlock(hit);
                    }
                }
            }

            ParticleOptions particle = this.entityData.get(DATA_PARTICLE);

            for (int i = 0; i < (range * radius) * this.getThickness(); i++) {
                Vec3 direction = Vec3.directionFromRotation(owner.getXRot() + (float) ((this.random.nextDouble() - 0.5D) * angle),
                        owner.getYRot() + (float) ((this.random.nextDouble() - 0.5D) * angle)).scale(range * 0.05D);
                Vec3 pos = new Vec3(owner.getX() + look.x(), owner.getEyeY() - 0.2D + look.y(), owner.getZ() + look.z())
                        .add(this.random.nextDouble() * 0.2D - 0.1D, this.random.nextDouble() * 0.2D - 0.1D, this.random.nextDouble() * 0.2D - 0.1D);
                this.level.addParticle(particle, pos.x(), pos.y(), pos.z(), direction.x(), direction.y(), direction.z());
            }

            this.setTicks(++ticks);
            this.setDeltaMovement(Vec3.ZERO);
        }
    }
}
