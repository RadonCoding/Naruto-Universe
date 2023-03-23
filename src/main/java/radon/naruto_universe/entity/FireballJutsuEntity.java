package radon.naruto_universe.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.client.particle.VaporParticle;

public class FireballJutsuEntity extends AbstractHurtingProjectile {
    public static final float INITIAL_SCALE = 0.1F;
    public static final float SCALAR = 1.0F - INITIAL_SCALE;
    public static final float SCALE_TIME = 20.0F;

    private static final float BASE_DAMAGE = 10.0F;
    private static final float BASE_EXPLOSION = 5.0F;

    private static final EntityDataAccessor<Float> DATA_POWER = SynchedEntityData.defineId(FireballJutsuEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_SIZE = SynchedEntityData.defineId(FireballJutsuEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(FireballJutsuEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_LIFE = SynchedEntityData.defineId(FireballJutsuEntity.class, EntityDataSerializers.INT);

    public FireballJutsuEntity(EntityType<? extends FireballJutsuEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FireballJutsuEntity(Player pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, float pInaccuracy, float power, float size, float maxSize) {
        super(EntityRegistry.FIREBALL_JUTSU.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pShooter.level);

        this.entityData.set(DATA_POWER, power);
        this.entityData.set(DATA_SIZE, Math.min(maxSize, power * size));

        this.moveTo(pShooter.getX(), pShooter.getEyeY() - 0.2D, pShooter.getZ(), this.getYRot(), this.getXRot());
        this.reapplyPosition();

        Vec3 movement = new Vec3(pOffsetX, pOffsetY, pOffsetZ).normalize().add(this.random.triangle(0.0D, 0.0172275D * 2.0D),
                        this.random.triangle(0.0D, 0.0172275D * (double) pInaccuracy),
                        this.random.triangle(0.0D, 0.0172275D * (double) pInaccuracy));
        this.setDeltaMovement(movement);

        double d0 = Math.sqrt(pOffsetX * pOffsetX + pOffsetY * pOffsetY + pOffsetZ * pOffsetZ);

        if (d0 != 0.0D) {
            this.xPower = pOffsetX / d0 * 0.2D;
            this.yPower = pOffsetY / d0 * 0.2D;
            this.zPower = pOffsetZ / d0 * 0.2D;
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_POWER, 0.0F);
        this.entityData.define(DATA_SIZE, 0.0F);
        this.entityData.define(DATA_TIME, 0);
        this.entityData.define(DATA_LIFE, 60);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        float scale = Math.min(INITIAL_SCALE + (SCALAR - (SCALAR * ((SCALE_TIME - this.getTime()) / SCALE_TIME))), 1.0F);
        return EntityDimensions.scalable(this.getSize(), this.getSize())
                .scale(scale);
    }

    public float getPower() {
        return this.entityData.get(DATA_POWER);
    }

    public float getSize() {
        return this.entityData.get(DATA_SIZE);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    private void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    public int getLife() {
        return this.entityData.get(DATA_LIFE);
    }

    private void setLife(int life) {
        this.entityData.set(DATA_LIFE, life);
    }

    @Override
    public void tick() {
        super.tick();

        int time = this.getTime();
        this.setTime(++time);

        this.refreshDimensions();

        int life = this.getLife();

        if (this.isInWaterOrRain()) {
            this.setLife(--life);

            if (life-- % 5 == 0) {
                this.playSound(SoundEvents.FIRE_EXTINGUISH, 1F, 1.0F);
            }
        }

        if (this.isInWater() || life <= 0) {
            this.level.addAlwaysVisibleParticle(ParticleTypes.CLOUD, this.getX(), this.getY() + this.getBbHeight(), this.getZ(),
                    0.0D, 0.0D, 0.0D);
            this.level.playSound(null, this.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.MASTER, 1.0F, 1.0F);
            this.discard();
        }

        if (time % 10 == 0) {
            this.level.playSound(null, this.blockPosition(), SoundEvents.FIRE_AMBIENT, SoundSource.AMBIENT, 1.0F, 1.0F);
        }

        for (int i = 0; i < 6; i++) {
            double offsetX = this.random.nextDouble() * (random.nextBoolean() ? -1 : 1);
            double offsetY = (this.getBbHeight() / 2.0) + (this.random.nextDouble() * (random.nextBoolean() ? -1 : 1));
            double offsetZ = this.random.nextDouble() * (random.nextBoolean() ? -1 : 1);

            this.level.addAlwaysVisibleParticle(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.FLAME_COLOR, this.getSize(), true, this.random.nextInt(1, 20)),
                    true,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    protected @NotNull ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    protected void onHit(@NotNull HitResult pResult) {
        super.onHit(pResult);

        if (!this.level.isClientSide) {
            float powerFactor = this.getPower() / 15.0F;
            float sizeFactor = this.getSize() / 2.0F;
            float explosion = BASE_EXPLOSION * sizeFactor * powerFactor;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), explosion,
                    true, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }

    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (!this.level.isClientSide) {
            Entity target = pResult.getEntity();
            Entity owner = this.getOwner();

            if (owner != null) {
                float sizeFactor = this.getSize() / 2.0F;
                float damage = BASE_DAMAGE * this.getPower() * sizeFactor;
                target.hurt(DamageSource.playerAttack((Player) owner), damage);
                this.doEnchantDamageEffects((LivingEntity) owner, target);
            }
        }
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }
}
