package radon.naruto_universe.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
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
import radon.naruto_universe.ModDamageSource;
import radon.naruto_universe.item.NarutoItems;
import radon.naruto_universe.sound.NarutoSounds;

import javax.annotation.Nullable;

public class ThrownKunaiEntity extends AbstractArrow {
    private ItemStack kunaiItem = new ItemStack(NarutoItems.KUNAI.get());
    private boolean dealtDamage;

    public ThrownKunaiEntity(EntityType<? extends ThrownKunaiEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ThrownKunaiEntity(Level pLevel, LivingEntity pShooter, ItemStack pStack) {
        super(NarutoEntities.THROWN_KUNAI.get(), pShooter, pLevel);
        this.kunaiItem = pStack.copy();
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

    private boolean isAcceptibleReturnOwner() {
        Entity entity = this.getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayer) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return this.kunaiItem.copy();
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

        if (target instanceof LivingEntity livingTarget) {
            damage += EnchantmentHelper.getDamageBonus(this.kunaiItem, livingTarget.getMobType());
        }

        Entity owner = this.getOwner();
        DamageSource source = ModDamageSource.kunai(this, owner == null ? this : owner);
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

        this.playSound(NarutoSounds.KUNAI_HIT.get(), 1.0F, 1.0F);
        this.discard();
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
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.contains("kunai", 10)) {
            this.kunaiItem = ItemStack.of(pCompound.getCompound("kunai"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.put("kunai", this.kunaiItem.save(new CompoundTag()));
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
}