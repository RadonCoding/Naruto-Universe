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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.ModDamageSource;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.particle.VaporParticle;

public class FireballJutsuProjectile extends JutsuProjectile {
    private static final EntityDataAccessor<Float> DATA_SIZE = SynchedEntityData.defineId(FireballJutsuProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(FireballJutsuProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_LIFE = SynchedEntityData.defineId(FireballJutsuProjectile.class, EntityDataSerializers.INT);

    public FireballJutsuProjectile(EntityType<? extends FireballJutsuProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public FireballJutsuProjectile(LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, float power, float damage, float size, float maxSize) {
        super(NarutoEntities.FIREBALL_JUTSU.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, power, damage, NinjaTrait.FIRE_RELEASE);

        this.entityData.set(DATA_SIZE, Math.min(maxSize, power * size));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_SIZE, 0.0F);
        this.entityData.define(DATA_TIME, 0);
        this.entityData.define(DATA_LIFE, 60);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return EntityDimensions.fixed(this.getSize(), this.getSize());
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
        this.setTime(++time);

        int life = this.getLife();

        if (this.isInWaterOrRain()) {
            this.setLife(--life);

            if (life % 5 == 0) {
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
            double offsetX = (this.random.nextDouble() * 0.5F) * (random.nextBoolean() ? -1 : 1);
            double offsetY = (this.getBbHeight() / 2.0) + ((this.random.nextDouble() * 0.5F) * (random.nextBoolean() ? -1 : 1));
            double offsetZ = (this.random.nextDouble() * 0.5F) * (random.nextBoolean() ? -1 : 1);

            this.level.addAlwaysVisibleParticle(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.FLAME_COLOR, this.getSize() * 2.0F, 0.25F,
                            true, this.random.nextInt(1, 20)), true, this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ,
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
            float power = this.getPower() * 0.25F;
            float size = this.getSize() * 0.75F;
            float explosion = size * power;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), explosion,
                    true, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }

    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (!this.level.isClientSide) {
            Entity target = pResult.getEntity();
            LivingEntity owner = (LivingEntity) this.getOwner();

            if (owner != null) {
                float power = this.getPower() * 0.75F;
                float size = this.getSize() * 0.5F;
                float damage = this.getDamage() * power * size;
                target.hurt(ModDamageSource.jutsu(owner, this), damage);
                this.doEnchantDamageEffects(owner, target);
            }
        }
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }
}
