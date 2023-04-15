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
import radon.naruto_universe.ModDamageSource;
import radon.naruto_universe.capability.NinjaTrait;
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
    private float getThickness() {
        return this.entityData.get(DATA_THICKNESS);
    }

    @Override
    public void tick() {
        super.tick();

        int ticks = this.getTicks();

        Entity owner = this.getOwner();

        if (ticks >= this.getLifetime()) {
            this.discard();
        }
        else if (!this.level.isClientSide && (owner == null || !owner.isAlive())) {
            this.discard();
        }
        else if (owner != null) {
            float power = Math.max(10.0F, this.getPower());
            float range = this.getRange() * (power * 0.1F);

            Vec3 look = owner.getLookAngle();
            double angle = Math.atan(1.0D) * 180.0D / Math.PI;

            Vec3 center = new Vec3(owner.getX() + look.x(), owner.getEyeY() - 0.2D + look.y(), owner.getZ() + look.z());
            AABB box = new AABB(center.x() - range / 2.0D, center.y() - range / 2.0D, center.z() - range / 2.0D,
                    center.x() + range / 2.0D, center.y() + range / 2.0D, center.z() + range / 2.0D).deflate(this.getThickness());

            for (Entity entity : this.level.getEntities(null, box)) {
                Vec3 direction = entity.position().subtract(owner.position()).normalize();
                double angleBetween = Math.toDegrees(Math.acos(look.dot(direction) / (look.length() * direction.length())));

                if (angleBetween <= angle) {
                    if (entity.hurt(ModDamageSource.jutsu(this, owner), this.getDamage())) {
                        if (this.getRelease() == NinjaTrait.FIRE_RELEASE) {
                            entity.setSecondsOnFire(Math.round(this.getPower()));
                        }
                    }
                }
                this.onHitEntity(new EntityHitResult(entity));
            }


            for (int i = 0; i < range * this.getThickness(); i++) {
                this.level.addParticle(this.getParticle(), owner.getX(), owner.getEyeY() - 0.2D, owner.getZ(), range * (this.random.nextDouble() - 0.5D) * 0.1D,
                        range * (this.random.nextDouble() - 0.5D) * 0.1D, range * (this.random.nextDouble() - 0.5D) * 0.1D);
            }

            this.setTicks(++ticks);
            this.setDeltaMovement(Vec3.ZERO);
        }
    }
}
