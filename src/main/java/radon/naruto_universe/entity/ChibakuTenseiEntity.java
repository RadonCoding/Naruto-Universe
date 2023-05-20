package radon.naruto_universe.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SetDeltaMovementS2CPacket;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.*;

public class ChibakuTenseiEntity extends Mob implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> DATA_HAS_TARGET = SynchedEntityData.defineId(ChibakuTenseiEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int MAX_SIZE = 16;
    private static final int HEIGHT = 64;

    private UUID meteoriteUUID;
    private MeteoriteEntity cachedMeteorite;

    private Vec3 targetPos;

    private boolean reached;
    private static final int SUCK_RANGE = 32;

    private Entity target;

    private final List<Entity> entities = new ArrayList<>();
    private final List<BlockAppearanceEntity> blocks = new ArrayList<>();

    protected ChibakuTenseiEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ChibakuTenseiEntity(LivingEntity owner) {
        super(NarutoEntities.CHIBAKU_TENSEI.get(), owner.level);

        MeteoriteEntity meteorite = new MeteoriteEntity(owner, owner.getX(), owner.getY(), owner.getZ());
        owner.level.addFreshEntity(meteorite);
        this.setMeteorite(meteorite);

        this.targetPos = owner.position().add(0.0D, HEIGHT, 0.0D);

        this.moveTo(owner.getX(), owner.getEyeY() - 0.2D, owner.getZ(), this.getYRot(), this.getXRot());
        this.reapplyPosition();
    }

    public ChibakuTenseiEntity(LivingEntity owner, Entity target) {
        super(NarutoEntities.CHIBAKU_TENSEI.get(), owner.level);

        MeteoriteEntity meteorite = new MeteoriteEntity(owner, target.getX(), target.getY(), target.getZ());
        owner.level.addFreshEntity(meteorite);
        this.setMeteorite(meteorite);

        this.targetPos = target.position().add(0.0D, HEIGHT, 0.0D);

        this.moveTo(target.getX(), target.getEyeY() - 0.2D, target.getZ(), this.getYRot(), this.getXRot());
        this.reapplyPosition();

        this.entityData.set(DATA_HAS_TARGET, true);

        this.target = target;
        this.entities.add(target);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 100.0D);
    }

    public void setMeteorite(MeteoriteEntity meteorite) {
        if (meteorite != null) {
            this.meteoriteUUID = meteorite.getUUID();
            this.cachedMeteorite = meteorite;
        }
    }

    public MeteoriteEntity getMeteorite() {
        if (this.cachedMeteorite != null && !this.cachedMeteorite.isRemoved()) {
            return this.cachedMeteorite;
        } else if (this.meteoriteUUID != null && this.level instanceof ServerLevel) {
            this.cachedMeteorite = (MeteoriteEntity) ((ServerLevel) this.level).getEntity(this.meteoriteUUID);
            return this.cachedMeteorite;
        } else {
            return null;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_HAS_TARGET, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.meteoriteUUID != null) {
            pCompound.putUUID("meteorite", this.meteoriteUUID);
        }
        pCompound.putBoolean("reached", this.reached);
        pCompound.putBoolean("has_target", this.entityData.get(DATA_HAS_TARGET));
        pCompound.putDouble("target_x", this.targetPos.x());
        pCompound.putDouble("target_y", this.targetPos.y());
        pCompound.putDouble("target_z", this.targetPos.z());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("meteorite")) {
            this.meteoriteUUID = pCompound.getUUID("meteorite");
        }
        this.reached = pCompound.getBoolean("reached");
        this.entityData.set(DATA_HAS_TARGET, pCompound.getBoolean("has_target"));
        this.targetPos = new Vec3(pCompound.getDouble("target_x"), pCompound.getDouble("target_y"), pCompound.getDouble("target_z"));
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        MeteoriteEntity meteorite = this.getMeteorite();

        int meteoriteId = meteorite == null ? 0 : meteorite.getId();
        return new ClientboundAddEntityPacket(this, meteoriteId);
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        MeteoriteEntity meteorite = (MeteoriteEntity) this.level.getEntity(pPacket.getData());

        if (meteorite != null) {
            this.setMeteorite(meteorite);
        }
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    private void flyTowards(Entity src, Entity dst) {
        double xDiff = dst.getX() - src.getX();
        double yDiff = dst.getY() - src.getY();
        double zDiff = dst.getZ() - src.getZ();
        double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);

        if (distance != 0.0D) {
            double speed = Math.max(0.1D, Math.min(0.5D, Math.sqrt(distance)));
            double xMove = xDiff / distance * speed;
            double yMove = yDiff / distance * speed;
            double zMove = zDiff / distance * speed;

            Vec3 movement = new Vec3(xMove, yMove, zMove).normalize().scale(speed);

            if (src instanceof ServerPlayer player) {
                player.setDeltaMovement(movement);
                PacketHandler.sendToClient(new SetDeltaMovementS2CPacket(movement), player);
            } else {
                src.setDeltaMovement(movement);
            }
        }
    }

    @Override
    public void knockback(double pStrength, double pX, double pZ) {}

    private boolean suckable(Entity entity) {
        MeteoriteEntity meteorite = this.getMeteorite();
        return !this.entities.contains(entity) &&
                entity != meteorite.getOwner() &&
                entity instanceof LivingEntity &&
                !(entity instanceof ChibakuTenseiEntity) &&
                !(entity instanceof Player player && player.getAbilities().instabuild);
    }

    @Override
    public boolean isInvisible() {
        return this.entityData.get(DATA_HAS_TARGET);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    public void drop() {
        MeteoriteEntity meteorite = this.getMeteorite();

        if (meteorite.getSize() > 0) {
            meteorite.drop();
        }
    }

    private void suck() {
        MeteoriteEntity meteorite = this.getMeteorite();

        if (this.blocks.isEmpty() && this.level.getGameTime() % 20 == 0) {
            int size = this.random.nextInt(2, 5) * 2;

            for (int i = 0; i < size; i++) {
                BlockPos closestPos = null;
                BlockState closestState = null;
                double closestDistance = Double.MAX_VALUE;

                for (int x = -SUCK_RANGE; x <= SUCK_RANGE; x++) {
                    for (int y = -(HEIGHT + SUCK_RANGE); y <= SUCK_RANGE; y++) {
                        for (int z = -SUCK_RANGE; z <= SUCK_RANGE; z++) {
                            BlockPos pos = new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
                            BlockState state = this.level.getBlockState(pos);

                            if (state.getBlock().defaultDestroyTime() > -1.0F && state.canOcclude()) {
                                double distance = pos.distSqr(this.blockPosition());

                                if (distance < closestDistance) {
                                    closestDistance = distance;
                                    closestState = state;
                                    closestPos = pos;
                                }
                            }
                        }
                    }
                }

                if (closestPos != null) {
                    this.level.setBlock(closestPos, Blocks.AIR.defaultBlockState(), 2);

                    BlockAppearanceEntity block = new BlockAppearanceEntity(this.level, closestPos, closestState);
                    this.level.addFreshEntity(block);

                    this.blocks.add(block);
                }
            }
        }

        if (meteorite.getSize() > 0) {
            for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(SUCK_RANGE, HEIGHT + SUCK_RANGE, SUCK_RANGE), this::suckable)) {
                this.flyTowards(entity, this);

                if (entity.getBoundingBox().intersects(this.getBoundingBox())) {
                    this.entities.add(entity);
                }
            }
        }
    }

    private void update() {
        MeteoriteEntity meteorite = this.getMeteorite();

        this.moveTo(meteorite.position().add(0.0D, (meteorite.getBbHeight() / 2.0F) - (this.getBbHeight() / 2.0F), 0.0D));

        Iterator<BlockAppearanceEntity> blocksIter = this.blocks.iterator();

        while (blocksIter.hasNext()) {
            BlockAppearanceEntity block = blocksIter.next();

            if (block.getBoundingBox().intersects(this.getBoundingBox())) {
                Vec3 pos = this.position().subtract(0.0D, (meteorite.getBbHeight() / 2.0F) - (this.getBbHeight() / 2.0F), 0.0D);

                meteorite.addBlock(block.getBlockState().getBlock());
                meteorite.setSize(meteorite.getSize() + 1);
                blocksIter.remove();
                block.discard();

                this.moveTo(pos.add(0.0D, (meteorite.getBbHeight() / 2.0F) - (this.getBbHeight() / 2.0F), 0.0D));

                meteorite.move(MoverType.SELF, new Vec3((this.random.nextDouble() - 0.5D) * 0.2D, (this.random.nextDouble() - 0.5D) * 0.2D, (this.random.nextDouble() - 0.5D) * 0.2D));
            } else {
                this.flyTowards(block, this);
            }
        }

        Iterator<Entity> entitiesIter = this.entities.iterator();

        while (entitiesIter.hasNext()) {
            Entity entity = entitiesIter.next();

            AABB entityBB = entity.getBoundingBox();

            if (entityBB.intersects(this.getBoundingBox())) {
                entity.setDeltaMovement(Vec3.ZERO);
            }
            else {
                if (!entity.isRemoved() && entity.isAlive()) {
                    if (meteorite.isFalling()) {
                        entity.moveTo(this.position());
                    } else {
                        this.flyTowards(entity, this);
                    }
                } else {
                    entitiesIter.remove();
                }
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;

        MeteoriteEntity meteorite = this.getMeteorite();

        if (!this.level.isClientSide && meteorite == null) {
            this.discard();
        } else {
            if (this.level.isClientSide) {
                if (meteorite == null) return;
                if (meteorite.getSize() < 4) return;

                List<Block> blocks = meteorite.getBlocks();

                AABB bounds = meteorite.getBoundingBox();

                double bbWidth = bounds.maxX - bounds.minX;
                double bbHeight = bounds.maxY - bounds.minY;
                double bbDepth = bounds.maxZ - bounds.minZ;
                double bbSize = Math.max(Math.max(bbWidth, bbHeight), bbDepth);
                int particleCount = (int) Math.round(bbSize * 5.0D);

                Random rand = new Random();

                for (int i = 0; i < particleCount; i++) {
                    double xPos = bounds.minX + rand.nextDouble() * bbWidth;
                    double yPos = bounds.minY + rand.nextDouble() * bbHeight;
                    double zPos = bounds.minZ + rand.nextDouble() * bbDepth;
                    double xSpeed = rand.nextDouble() * 0.2D - 0.1D;
                    double ySpeed = rand.nextDouble() * -0.1D - 0.2D;
                    double zSpeed = rand.nextDouble() * 0.2D - 0.1D;
                    this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blocks.get(rand.nextInt(blocks.size())).defaultBlockState()),
                            xPos, yPos, zPos, xSpeed, ySpeed, zSpeed);
                }
            } else {
                if (this.target != null && (!this.target.isAlive() || this.target.isRemoved())) {
                    this.target = null;
                }

                if (meteorite.getSize() > 0) {
                    for (Entity entity : this.level.getEntities(this, meteorite.getBoundingBox())) {
                        entity.hurt(DamageSource.IN_WALL, meteorite.getSize());
                    }
                }

                if (meteorite.getSize() < MAX_SIZE) {
                    if (!meteorite.isFalling()) {
                        if (!this.reached) {
                            double xDiff = this.targetPos.x() - this.getX();
                            double yDiff = this.targetPos.y() - this.getY();
                            double zDiff = this.targetPos.z() - this.getZ();
                            double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);

                            if (distance > 0.1D) {
                                double speed = Math.max(0.1D, Math.max(0.25D, Math.min(0.1D, Math.sqrt(distance))));
                                double xMove = xDiff / distance * speed;
                                double yMove = yDiff / distance * speed;
                                double zMove = zDiff / distance * speed;

                                Vec3 movement = new Vec3(xMove, yMove, zMove);
                                this.move(MoverType.SELF, movement);
                                meteorite.move(MoverType.SELF, movement);

                                if (this.target != null) {
                                    this.target.moveTo(this.position());
                                }
                            } else {
                                this.reached = true;
                            }
                        }
                        this.suck();
                    }
                } else {
                    this.blocks.forEach(Entity::discard);
                }
                this.update();
            }
        }
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);

        MeteoriteEntity meteorite = this.getMeteorite();

        if (meteorite != null && meteorite.getSize() > 0) {
            meteorite.drop();
        }
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
        return RenderUtils.getCurrentTick();
    }
}
