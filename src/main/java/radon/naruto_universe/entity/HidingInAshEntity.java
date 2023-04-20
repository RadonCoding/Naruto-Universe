package radon.naruto_universe.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import radon.naruto_universe.NarutoDamageSource;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.client.particle.NarutoParticles;

public class HidingInAshEntity extends JutsuProjectile {
    private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(HidingInAshEntity.class, EntityDataSerializers.PARTICLE);
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(HidingInAshEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TICKS = SynchedEntityData.defineId(HidingInAshEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_RANGE = SynchedEntityData.defineId(HidingInAshEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_THICKNESS = SynchedEntityData.defineId(HidingInAshEntity.class, EntityDataSerializers.FLOAT);

    public HidingInAshEntity(EntityType<? extends HidingInAshEntity> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public HidingInAshEntity(LivingEntity pShooter, float power, float damage, ParticleOptions particle, int lifetime, float range, float thickness) {
        super(NarutoEntities.HIDING_IN_ASH.get(), pShooter, 0.0D, 0.0D, 0.0D, power, damage, NinjaTrait.FIRE_RELEASE);

        this.setInvisible(true);

        pShooter.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, lifetime, 1, false, false, false));

        this.entityData.set(DATA_PARTICLE, particle);
        this.entityData.set(DATA_PARTICLE, particle);
        this.entityData.set(DATA_LIFETIME, lifetime);
        this.entityData.set(DATA_RANGE, range);
        this.entityData.set(DATA_THICKNESS, thickness);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_PARTICLE, NarutoParticles.EMPTY.get());
        this.entityData.define(DATA_LIFETIME, 0);
        this.entityData.define(DATA_TICKS, 0);
        this.entityData.define(DATA_RANGE, 0.0F);
        this.entityData.define(DATA_THICKNESS, 0.0F);
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
            float power = Math.max(10.0F, this.getPower());
            float range = this.getRange() * (power * 0.1F);

            float thickness = this.getThickness();

            Vec3 center = new Vec3(owner.getX(), owner.getEyeY() - 0.2D, owner.getZ());
            AABB box = new AABB(center.x() - range / 2.0D, center.y() - range / 2.0D, center.z() - range / 2.0D,
                    center.x() + range / 2.0D, center.y() + range / 2.0D, center.z() + range / 2.0D)
                    .deflate(thickness);

            for (Entity entity : this.level.getEntities(owner, box)) {
                if (entity.hurt(NarutoDamageSource.jutsu(this, owner), this.getDamage())) {
                    if (this.getRelease() == NinjaTrait.FIRE_RELEASE) {
                        entity.setSecondsOnFire(Math.round(this.getPower()));
                    }
                }
                this.onHitEntity(new EntityHitResult(entity));
            }

            ParticleOptions particle = this.entityData.get(DATA_PARTICLE);

            for (int i = 0; i < range * thickness; i++) {
                this.level.addParticle(particle, owner.getX(), owner.getEyeY() - 0.2D, owner.getZ(), range * (this.random.nextDouble() - 0.5D) * 0.1D,
                        range * (this.random.nextDouble() - 0.5D) * 0.1D, range * (this.random.nextDouble() - 0.5D) * 0.1D);
            }

            this.setTicks(++ticks);
            this.setDeltaMovement(Vec3.ZERO);
        }
    }
}
