package radon.naruto_universe.entity;

import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import radon.naruto_universe.ModDamageSource;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.particle.ParticleRegistry;

public class ParticleSpawnerProjectile extends JutsuProjectile {
    private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.PARTICLE);
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TICKS = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(ParticleSpawnerProjectile.class, EntityDataSerializers.FLOAT);

    public ParticleSpawnerProjectile(EntityType<? extends ParticleSpawnerProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public ParticleSpawnerProjectile(LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, float power, float damage, NinjaTrait release, ParticleOptions particle, int lifetime, float radius) {
        super(EntityRegistry.PARTICLE_SPAWNER.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, power, damage, release);

        this.setInvisible(true);

        this.entityData.set(DATA_PARTICLE, particle);
        this.entityData.set(DATA_LIFETIME, lifetime);
        this.entityData.set(DATA_RADIUS, radius);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_PARTICLE, ParticleRegistry.EMPTY.get());
        this.entityData.define(DATA_LIFETIME, 0);
        this.entityData.define(DATA_TICKS, 0);
        this.entityData.define(DATA_RADIUS, 0.0F);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    private ParticleOptions getParticle() {
        return this.entityData.get(DATA_PARTICLE);
    }

    private int getTicks() {
        return this.entityData.get(DATA_TICKS);
    }
    private void setTicks(int ticks) {
        this.entityData.set(DATA_TICKS, ticks);
    }

    private int getLifetime() {
        return this.entityData.get(DATA_LIFETIME);
    }

    private float getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    @Override
    public void tick() {
        super.tick();

        int ticks = this.getTicks();

        if (ticks >= this.getLifetime()) {
            this.discard();
        }

        float f0 = 1.0F - (float) ticks / this.getLifetime();
        float range = this.getPower() * f0;
        float radius = this.getRadius() * (this.getPower() * 0.1F) * f0;

        final Entity owner = this.getOwner();

        assert owner != null;

        Vec3 look = owner.getLookAngle();
        final double angle = Math.atan(radius / range) * 180.0D / Math.PI;

        Vec3 direction = Vec3.directionFromRotation(owner.getXRot() + (float)((this.random.nextDouble() - 0.5D) * angle * 3.0D),
                owner.getYRot() + (float)((this.random.nextDouble() - 0.5D) * angle * 3.0D)).scale(range * 0.1D);

        Vec3 center = owner.position().add(direction.scale(range * 0.5));

        // Calculate the corner points of the AABB
        Vec3 corner1 = center.add(direction.scale(-range)).add(direction.cross(new Vec3(0, 1, 0)).normalize().scale(radius * 5.0F));
        Vec3 corner2 = center.add(direction.scale(-range)).add(direction.cross(new Vec3(0, -1, 0)).normalize().scale(radius * 5.0F));
        Vec3 corner3 = center.add(direction.scale(range)).add(direction.cross(new Vec3(0, -1, 0)).normalize().scale(radius * 5.0F));
        Vec3 corner4 = center.add(direction.scale(range)).add(direction.cross(new Vec3(0, 1, 0)).normalize().scale(radius * 5.0F));

        // Create the AABB using the corner points
        AABB box = new AABB(corner1, corner2).expandTowards(corner3).expandTowards(corner4);

        if (this.level.isClientSide) {
            DebugRenderer.renderFilledBox(box, 1.0F, 1.0F, 1.0F, 0.5F);
        }

        for (Entity entity : this.level.getEntities(null, box)) {
            if (entity instanceof LivingEntity) {
                entity.hurt(ModDamageSource.jutsu(this, owner), this.getDamage());
            }
            this.onHitEntity(new EntityHitResult(entity));
        }

        for (int i = 0; i < range * radius; i++) {
            this.level.addParticle(this.getParticle(), owner.getX() + look.x(), owner.getEyeY() - 0.2D + look.y(), owner.getZ() + look.z(),
                    direction.x(), direction.y(), direction.z());
        }
        this.setTicks(++ticks);
    }
}
