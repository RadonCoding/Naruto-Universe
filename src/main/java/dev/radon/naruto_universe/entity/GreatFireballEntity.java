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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class GreatFireballEntity extends AbstractHurtingProjectile implements IAnimatable {
    private static final EntityDataAccessor<Float> ID_EXPLOSION_POWER = SynchedEntityData.defineId(GreatFireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ID_LIFE = SynchedEntityData.defineId(GreatFireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> ID_TIME = SynchedEntityData.defineId(GreatFireballEntity.class, EntityDataSerializers.INT);

    public static final float INITIAL_SCALE = 0.05F;
    public static final float SCALAR = 1.0F - INITIAL_SCALE;
    public static final float ENTITY_SIZE = 5.0F;
    public static final float SCALE_TIME = 2 * 20;

    private final AnimationFactory manager = GeckoLibUtil.createFactory(this);

    public GreatFireballEntity(EntityType<? extends GreatFireballEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GreatFireballEntity(Player pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, float explosionPower, float life) {
        super(EntityRegistry.GREAT_FIREBALL.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pShooter.level);

        this.entityData.set(ID_EXPLOSION_POWER, explosionPower);
        this.entityData.set(ID_LIFE, life);

        this.moveTo(pShooter.getX(), pShooter.getEyeY() - 0.2D, pShooter.getZ(), this.getYRot(), this.getXRot());
        this.reapplyPosition();
        double d0 = Math.sqrt(pOffsetX * pOffsetX + pOffsetY * pOffsetY + pOffsetZ * pOffsetZ);

        if (d0 != 0.0D) {
            this.xPower = pOffsetX / d0 * 0.2D;
            this.yPower = pOffsetY / d0 * 0.2D;
            this.zPower = pOffsetZ / d0 * 0.2D;
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ID_EXPLOSION_POWER, 0.0F);
        this.entityData.define(ID_LIFE, 0.0F);
        this.entityData.define(ID_TIME, 0);
    }

    public float getScale(float time, float scaleTime) {
        return Math.min(GreatFireballEntity.INITIAL_SCALE + (GreatFireballEntity.SCALAR
                - (GreatFireballEntity.SCALAR * ((scaleTime - time) / scaleTime))), 1.0F);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        EntityDimensions dimensions = EntityDimensions.scalable(ENTITY_SIZE, ENTITY_SIZE);
        return dimensions.scale(this.getScale(this.getTime(), SCALE_TIME));
    }

    public float getExplosionPower() {
        return this.getScale(this.getTime(), SCALE_TIME) * this.entityData.get(ID_EXPLOSION_POWER);
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
                .addAnimation("animation.great_fireball.rotate", ILoopType.EDefaultLoopTypes.LOOP));
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
