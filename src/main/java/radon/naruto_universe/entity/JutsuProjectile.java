package radon.naruto_universe.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.client.particle.NarutoParticles;

public class JutsuProjectile extends AbstractHurtingProjectile {
    private float power;
    private float damage;
    private NinjaTrait release;

    public JutsuProjectile(EntityType<? extends JutsuProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public JutsuProjectile(EntityType<? extends JutsuProjectile> pEntityType, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, float power, float damage, NinjaTrait release) {
        super(pEntityType, pShooter, pOffsetX, pOffsetY, pOffsetZ, pShooter.level);

        this.power = power;
        this.damage = damage;
        this.release = release;

        this.moveTo(pShooter.getX(), pShooter.getEyeY() - 0.2D, pShooter.getZ(), this.getYRot(), this.getXRot());
        this.reapplyPosition();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putFloat("power", this.power);
        pCompound.putFloat("damage", this.damage);
        pCompound.putInt("release", this.release.ordinal());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.power = pCompound.getFloat("power");
        this.damage = pCompound.getFloat("damage");
        this.release = NinjaTrait.values()[pCompound.getInt("release")];
    }

    public float getPower() {
        return this.power;
    }

    public float getDamage() {
        return this.damage;
    }

    public NinjaTrait getRelease() {
        return this.release;
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
    protected @NotNull ParticleOptions getTrailParticle() {
        return NarutoParticles.EMPTY.get();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (pResult.getEntity() instanceof JutsuProjectile projectile) {
            if (this.power < projectile.getPower()) {
                if (this.release == NinjaTrait.FIRE_RELEASE && projectile.getRelease() == NinjaTrait.WATER_RELEASE) {
                    this.power = this.power - projectile.getPower();
                } else if (this.release == NinjaTrait.WATER_RELEASE && projectile.getRelease() == NinjaTrait.FIRE_RELEASE) {
                    this.power = this.power - projectile.getPower();
                }

                if (this.getPower() <= 0.0F) {
                    this.extinguish();
                }
            }
        }
    }
}
