package radon.naruto_universe.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.ModDamageSource;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.particle.NarutoParticles;

public class ParticleSpawnerProjectile extends JutsuProjectile {
    private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.PARTICLE);
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TICKS = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_RANGE = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.FLOAT);

    public ParticleSpawnerProjectile(EntityType<? extends ParticleSpawnerProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public ParticleSpawnerProjectile(LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, float power, float damage, NinjaTrait release, ParticleOptions particle, int lifetime, float range, float radius) {
        super(NarutoEntities.PARTICLE_SPAWNER.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, power, damage, release);

        this.entityData.set(DATA_PARTICLE, particle);
        this.entityData.set(DATA_LIFETIME, lifetime);
        this.entityData.set(DATA_RANGE, range);
        this.entityData.set(DATA_RADIUS, radius);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_PARTICLE, NarutoParticles.EMPTY.get());
        this.entityData.define(DATA_LIFETIME, 0);
        this.entityData.define(DATA_TICKS, 0);
        this.entityData.define(DATA_RANGE, 0.0F);
        this.entityData.define(DATA_RADIUS, 0.0F);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    private ParticleOptions getParticle() {
        return this.entityData.get(DATA_PARTICLE);
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

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return EntityDimensions.fixed(0.0F, 0.0F);
    }

    @Override
    public void tick() {
        super.tick();

        int ticks = this.getTicks();

        if (ticks >= this.getLifetime()) {
            this.discard();
        }

        float f0 = 1.0F - (float) ticks / this.getLifetime();
        float range = Math.max(2.5F ,this.getRange() * (this.getPower() * 0.1F) * f0);
        float radius = Math.max(1.25F, this.getRadius() * (this.getPower() * 0.1F) * f0);

        final Entity owner = this.getOwner();

        assert owner != null;

        Vec3 look = owner.getLookAngle();
        final double angle = Math.atan(radius / range) * 180.0D / Math.PI;

        AABB box = new AABB(owner.getX() - range, owner.getY() - radius, owner.getZ() - range, owner.getX() + range,
                owner.getY() + radius, owner.getZ() + range);

        for (Entity entity : this.level.getEntities(null, box)) {
            if (entity instanceof LivingEntity) {
                Vec3 direction = entity.position().subtract(owner.position()).normalize();
                double angleBetween = Math.toDegrees(Math.acos(look.dot(direction) / (look.length() * direction.length())));

                if (angleBetween <= angle) {
                    entity.hurt(ModDamageSource.jutsu(this, owner), this.getDamage());

                    if (this.getRelease() == NinjaTrait.FIRE_RELEASE) {
                        entity.setSecondsOnFire(Math.round(this.getPower()));
                    }
                }
            }
            this.onHitEntity(new EntityHitResult(entity));
        }

        for (int i = 0; i < (range * radius) * 0.5F; i++) {
            Vec3 direction = Vec3.directionFromRotation(owner.getXRot() + (float)((this.random.nextDouble() - 0.5D) * angle * 1.5D),
                    owner.getYRot() + (float)((this.random.nextDouble() - 0.5D) * angle * 2.0D)).scale(range * 0.1D);
            Vec3 pos = new Vec3(owner.getX() + look.x(), owner.getEyeY() - 0.2D + look.y(), owner.getZ() + look.z());
            this.level.addParticle(this.getParticle(), pos.x(), pos.y(), pos.z(), direction.x(), direction.y(), direction.z());
        }

        this.setTicks(++ticks);
        this.setDeltaMovement(Vec3.ZERO);
    }
}
