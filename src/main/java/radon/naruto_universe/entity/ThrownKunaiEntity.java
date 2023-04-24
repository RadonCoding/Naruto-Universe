package radon.naruto_universe.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.NarutoDamageSource;
import radon.naruto_universe.item.NarutoItems;
import radon.naruto_universe.sound.NarutoSounds;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class ThrownKunaiEntity extends AbstractArrow implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean dealtDamage;

    public ThrownKunaiEntity(EntityType<? extends ThrownKunaiEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ThrownKunaiEntity(Level pLevel, LivingEntity pShooter) {
        super(NarutoEntities.THROWN_KUNAI.get(), pShooter, pLevel);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity entity = this.getOwner();

        if (this.isNoPhysics() && entity != null) {
            if (!this.level.isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }
            this.discard();
        }
        super.tick();
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return new ItemStack(NarutoItems.KUNAI.get());
    }

    @Nullable
    protected EntityHitResult findHitEntity(@NotNull Vec3 pStartVec, @NotNull Vec3 pEndVec) {
        return this.dealtDamage ? null : super.findHitEntity(pStartVec, pEndVec);
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return NarutoSounds.KUNAI_HIT.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity target = pResult.getEntity();
        float damage = 8.0F;

        Entity owner = this.getOwner();
        DamageSource source = NarutoDamageSource.kunai(this, owner == null ? this : owner);
        this.dealtDamage = true;

        if (target.hurt(source, damage)) {
            if (target.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (target instanceof LivingEntity livingTarget) {
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingTarget, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)owner, livingTarget);
                }
                this.doPostHurtEffects(livingTarget);
            }
        }

        if (target instanceof ThrownKunaiEntity) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
            this.playSound(NarutoSounds.AMATERASU.get(), 10.0F, 1.0F);
        } else {
            this.playSound(NarutoSounds.KUNAI_HIT.get(), 1.0F, 1.0F);
            this.discard();
        }
    }

    @Override
    protected boolean tryPickup(@NotNull Player pPlayer) {
        return super.tryPickup(pPlayer) || this.isNoPhysics() && this.ownedBy(pPlayer) && pPlayer.getInventory().add(this.getPickupItem());
    }

    @Override
    public void playerTouch(@NotNull Player pEntity) {
        if (this.ownedBy(pEntity) || this.getOwner() == null) {
            super.playerTouch(pEntity);
        }
    }

    @Override
    public void tickDespawn() {
        if (this.pickup != AbstractArrow.Pickup.ALLOWED) {
            super.tickDespawn();
        }
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }
}