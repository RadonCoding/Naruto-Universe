package dev.radon.naruto_universe.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class FireballEntity extends AbstractHurtingProjectile implements IAnimatable {
    private static final EntityDataAccessor<Float> ID_EXPLOSION_POWER = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ID_LIFE = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> ID_TIME = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.INT);

    private static int scaleTime;
    private static float entitySize;
    private static float initialScale;
    private static float scalar;

    private final AnimationFactory manager = GeckoLibUtil.createFactory(this);

    public FireballEntity(EntityType<? extends FireballEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FireballEntity(Player pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, float pInaccuracy, float explosionPower, float life,
                          float entitySize, int scaleTime, float initialScale, float scalar) {
        super(EntityRegistry.GREAT_FIREBALL.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pShooter.level);

        this.entitySize = entitySize;
        this.scaleTime = scaleTime;
        this.initialScale = initialScale;
        this.scalar = scalar;

        this.entityData.set(ID_EXPLOSION_POWER, explosionPower);
        this.entityData.set(ID_LIFE, life);

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

    public int getScaleTime() {
        return this.scaleTime;
    }

    public float getEntitySize() {
        return this.entitySize;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ID_EXPLOSION_POWER, 0.0F);
        this.entityData.define(ID_LIFE, 0.0F);
        this.entityData.define(ID_TIME, 0);
    }

    public float getScale(float time, float scaleTime) {
        return Math.min(FireballEntity.initialScale + (FireballEntity.scalar
                - (FireballEntity.scalar * ((scaleTime - time) / scaleTime))), 1.0F);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        EntityDimensions dimensions = EntityDimensions.scalable(entitySize, entitySize);
        return dimensions.scale(this.getScale(this.getTime(), scaleTime));
    }

    public float getExplosionPower() {
        return this.getScale(this.getTime(), scaleTime) * this.entityData.get(ID_EXPLOSION_POWER);
    }

    public int getTime() {
        return this.entityData.get(ID_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(ID_TIME, time);
    }

    public float getLife() {
        return this.entityData.get(ID_LIFE);
    }

    public void setLife(float life) {
        this.entityData.set(ID_LIFE, life);
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        int time = this.getTime();
        this.setTime(++time);

        if (this.isInWaterOrRain()) {
            float life = this.getLife();
            this.setLife(--life);

            if (life == 0) {
                this.level.addParticle(ParticleTypes.CLOUD, this.getX(), this.getY() + this.getBbHeight(), this.getZ(),
                        0.0D, 0.0D, 0.0D);
                this.level.playSound(null, this.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.MASTER, 1.0F, 1.0F);
                this.discard();
            }
        }

        this.level.addParticle(ParticleTypes.FLAME, this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ(),
                0.0D, 0.0D, 0.0D);

        if (time % 10 == 0) {
            this.level.playSound(null, this.blockPosition(), SoundEvents.FIRE_AMBIENT, SoundSource.AMBIENT, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);

        if (!this.level.isClientSide) {
            this.level.explode(null, this.getX(), this.getY(), this.getZ(),
                    this.getExplosionPower(), true, Explosion.BlockInteraction.DESTROY);
            this.discard();
        }
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (!this.level.isClientSide) {
            Entity target = pResult.getEntity();
            Entity owner = this.getOwner();
            target.hurt(DamageSource.playerAttack((Player) owner), 6.0F);

            if (owner instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity)owner, target);
            }
        }
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder()
                .addAnimation("animation.fireball.rotate", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.manager;
    }
}
