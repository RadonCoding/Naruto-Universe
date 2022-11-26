package dev.radon.naruto_universe.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class GreatFireballEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<Integer> ID_EXPLOSION_POWER = SynchedEntityData.defineId(GreatFireballEntity.class, EntityDataSerializers.INT);

    public GreatFireballEntity(EntityType<? extends GreatFireballEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GreatFireballEntity(Level pLevel, LivingEntity pShooter, int pExplosionPower) {
        super(EntityRegistry.GREAT_FIREBALL.get(), pShooter, pLevel);
        this.entityData.set(ID_EXPLOSION_POWER, pExplosionPower);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ID_EXPLOSION_POWER, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isInWater()) {
            this.level.playSound(null, this.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.MASTER, 1.0F, 1.0F);
            this.discard();
        }

        this.level.addParticle(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);

        if (!this.level.isClientSide) {
            boolean causesFire = ForgeEventFactory.getMobGriefingEvent(this.level, this.getOwner());
            this.level.explode(null, this.getX(), this.getY(), this.getZ(), (float) this.entityData.get(ID_EXPLOSION_POWER), causesFire, causesFire ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE);
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

    @Override
    protected float getGravity() {
        return 0.01F;
    }
}
