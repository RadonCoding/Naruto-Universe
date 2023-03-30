package radon.naruto_universe.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.particle.ParticleRegistry;

public class JutsuProjectile extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Float> DATA_POWER = SynchedEntityData.defineId(JutsuProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_RELEASE = SynchedEntityData.defineId(JutsuProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(JutsuProjectile.class, EntityDataSerializers.FLOAT);

    public JutsuProjectile(EntityType<? extends JutsuProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public JutsuProjectile(EntityType<? extends JutsuProjectile> pEntityType, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, float power, float damage, NinjaTrait release) {
        super(pEntityType, pShooter, pOffsetX, pOffsetY, pOffsetZ, pShooter.level);

        this.moveTo(pShooter.getX(), pShooter.getEyeY() - 0.2D, pShooter.getZ(), this.getYRot(), this.getXRot());
        this.reapplyPosition();

        this.entityData.set(DATA_POWER, power);
        this.entityData.set(DATA_RELEASE, release.ordinal());
        this.entityData.set(DATA_DAMAGE, damage * power);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_POWER, 0.0F);
        this.entityData.define(DATA_RELEASE, NinjaTrait.NONE.ordinal());
        this.entityData.define(DATA_DAMAGE, 0.0F);
    }

    public float getDamage() {
        return this.entityData.get(DATA_DAMAGE);
    }

    public float getPower() {
        return this.entityData.get(DATA_POWER);
    }
    public void setPower(float power) {
        this.entityData.set(DATA_POWER, power);
    }

    public NinjaTrait getRelease() {
        return NinjaTrait.values()[this.entityData.get(DATA_RELEASE)];
    }

    private void extinguish() {
        this.level.addAlwaysVisibleParticle(ParticleTypes.CLOUD, this.getX(), this.getY() + this.getBbHeight(), this.getZ(),
                0.0D, 0.0D, 0.0D);
        this.level.playSound(null, this.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.MASTER, 1.0F, 1.0F);
        this.discard();
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleRegistry.EMPTY.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (pResult.getEntity() instanceof JutsuProjectile projectile) {
            if (this.getPower() < projectile.getPower()) {
                if (this.getRelease() == NinjaTrait.FIRE_RELEASE && projectile.getRelease() == NinjaTrait.WATER_RELEASE) {
                    this.setPower(this.getPower() - projectile.getPower());
                } else if (this.getRelease() == NinjaTrait.WATER_RELEASE && projectile.getRelease() == NinjaTrait.FIRE_RELEASE) {
                    this.setPower(this.getPower() - projectile.getPower());
                }

                if (this.getPower() <= 0.0F) {
                    this.extinguish();
                }
            }
        }
    }
}
