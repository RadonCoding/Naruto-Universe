package radon.naruto_universe.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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

public class FireballEntity extends AbstractHurtingProjectile {
    public static final float INITIAL_SCALE = 0.1F;
    public static final float GROW_SCALE = 1.0F - INITIAL_SCALE;
    public static final float GROW_TIME = 20.0F;

    private static final float BASE_DAMAGE = 6.0F;

    private static final EntityDataAccessor<Float> DATA_POWER = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_SIZE = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_LIFE = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.INT);

    public FireballEntity(EntityType<? extends FireballEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FireballEntity(Player pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, float pInaccuracy, float power, float size) {
        super(EntityRegistry.GREAT_FIREBALL.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pShooter.level);

        this.entityData.set(DATA_POWER, power);
        this.entityData.set(DATA_SIZE, Math.max(size / 2.0F, Math.min(size * power, size * 2.0F)));

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
        float growth = Math.min(this.getTime() / FireballEntity.GROW_TIME, 1.0F);
        float scale = FireballEntity.INITIAL_SCALE + (growth * FireballEntity.GROW_SCALE);
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

        this.refreshDimensions();

        int time = this.getTime();
        this.setTime(--time);

        int life = this.getLife();

        if (this.isInWaterOrRain()) {
            this.setLife(--life);

            if (life-- % 5 == 0) {
                this.playSound(SoundEvents.FIRE_EXTINGUISH, 1F, 1.0F);
            }
        }

        if (this.isInWater() || life <= 0) {
            this.level.addParticle(ParticleTypes.CLOUD, this.getX(), this.getY() + this.getBbHeight(), this.getZ(),
                    0.0D, 0.0D, 0.0D);
            this.level.playSound(null, this.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.MASTER, 1.0F, 1.0F);
            this.discard();
        }

        if (time % 10 == 0) {
            this.level.playSound(null, this.blockPosition(), SoundEvents.FIRE_AMBIENT, SoundSource.AMBIENT, 1.0F, 1.0F);
        }

        for (int i = 0; i < 6; i++) {
            this.level.addParticle(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.FIRE_COLOR, this.getSize() * 3.0F),
                    this.getX() + this.random.nextDouble() * (random .nextBoolean() ? -1 : 1),
                    this.getY() + this.random.nextDouble() * (random .nextBoolean() ? -1 : 1),
                    this.getZ() + this.random.nextDouble() * (random .nextBoolean() ? -1 : 1),
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected @NotNull ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    protected void onHit(@NotNull HitResult pResult) {
        super.onHit(pResult);

        if (!this.level.isClientSide) {
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), this.getPower(), true, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }

    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (!this.level.isClientSide) {
            Entity target = pResult.getEntity();
            Entity owner = this.getOwner();

            if (owner != null) {
                target.hurt(DamageSource.playerAttack((Player) owner), BASE_DAMAGE * this.getPower());
                this.doEnchantDamageEffects((LivingEntity) owner, target);
            }
        }
    }
}
