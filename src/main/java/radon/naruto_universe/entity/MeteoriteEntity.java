package radon.naruto_universe.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.ExplosionHandler;
import radon.naruto_universe.entity.base.NarutoEntityBase;
import radon.naruto_universe.entity.serialize.NarutoEntityDataSerializers;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MeteoriteEntity extends NarutoEntityBase implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> DATA_SIZE = SynchedEntityData.defineId(MeteoriteEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<List<Block>> DATA_BLOCKS = SynchedEntityData.defineId(MeteoriteEntity.class, NarutoEntityDataSerializers.BLOCK_LIST.get());

    private UUID ownerUUID;
    private LivingEntity cachedOwner;

    private int explosionTime;
    private boolean falling;

    private static final int EXPLOSION_DURATION = 10 * 20;

    protected MeteoriteEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public MeteoriteEntity(LivingEntity pShooter, double pX, double pY, double pZ) {
        super(NarutoEntities.METEORITE.get(), pShooter.level);

        this.setOwner(pShooter);

        this.moveTo(pX, pY, pZ, this.getYRot(), this.getXRot());
        this.reapplyPosition();
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_SIZE, 0);
        this.entityData.define(DATA_BLOCKS, new ArrayList<>());
    }

    public int getSize() {
        return this.entityData.get(DATA_SIZE);
    }
    public void setSize(int size) {
        this.entityData.set(DATA_SIZE, size);
    }

    public void drop() {
        this.falling = true;
    }

    public boolean isFalling() {
        return this.falling;
    }

    @Override
    public boolean isNoGravity() {
        return !this.falling;
    }

    public List<Block> getBlocks() {
        return this.entityData.get(DATA_BLOCKS);
    }

    public void addBlocks(List<Block> blocks) {
        List<Block> newBlocks = new ArrayList<>(this.entityData.get(DATA_BLOCKS));
        newBlocks.addAll(blocks);
        this.entityData.set(DATA_BLOCKS, newBlocks);
    }

    public void addBlock(Block block) {
        List<Block> newBlocks = new ArrayList<>(this.entityData.get(DATA_BLOCKS));
        newBlocks.add(block);
        this.entityData.set(DATA_BLOCKS, newBlocks);
    }

    public void setOwner(LivingEntity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    public LivingEntity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            this.cachedOwner = (LivingEntity) ((ServerLevel) this.level).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return EntityDimensions.scalable(this.getSize(), this.getSize()).scale(0.75F);
    }

    @Override
    public boolean canCollideWith(@NotNull Entity pEntity) {
        if (pEntity instanceof BlockAppearanceEntity || pEntity instanceof ChibakuTenseiEntity) {
            return false;
        }
        return super.canCollideWith(pEntity);
    }

    @Override
    public boolean isOnGround() {
        AABB bounds = this.getBoundingBox().inflate(0.0D, 1.0D, 0.0D);
        return BlockPos.betweenClosedStream(bounds).anyMatch(pos -> {
            BlockState state = this.level.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(this.level, pos);
            return !shape.isEmpty() && bounds.intersects(shape.bounds().move(pos));
        });
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (!this.level.isClientSide) {
            if (this.falling) {
                for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(0.0D, 5.0D, 0.0D), entity -> !(entity instanceof ChibakuTenseiEntity))) {
                    entity.hurt(DamageSource.fallingBlock(this), 5.0F * this.getSize());
                }

                if (this.isOnGround()) {
                    if (this.explosionTime == 0) {
                        ExplosionHandler.spawn(this.level.dimension(), this.position(), this.getSize(), EXPLOSION_DURATION);
                        this.explosionTime++;
                    }
                }
            }

            if (this.explosionTime > 0) {
                if (this.explosionTime >= this.getSize()) {
                    this.discard();
                } else {
                    BlockPos.betweenClosedStream(this.getBoundingBox().inflate(1.0D)).forEach(pos -> {
                        BlockState state = this.level.getBlockState(pos);

                        if (state.getBlock().defaultDestroyTime() > -1.0F && !state.isAir()) {
                            this.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        }
                    });
                    this.explosionTime++;
                }
            }
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }

        pCompound.putInt("size", this.entityData.get(DATA_SIZE));

        ListTag blocksTag = new ListTag();

        for (Block block : this.entityData.get(DATA_BLOCKS)) {
            ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);

            if (key != null) {
                blocksTag.add(StringTag.valueOf(key.toString()));
            }
        }
        pCompound.put("blocks", blocksTag);

        pCompound.putInt("explosion_time", this.explosionTime);
        pCompound.putBoolean("falling", this.falling);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }

        this.entityData.set(DATA_SIZE, pCompound.getInt("size"));

        List<Block> blocks = new ArrayList<>();

        for (Tag tag : pCompound.getList("blocks", Tag.TAG_STRING)) {
            ResourceLocation key = new ResourceLocation(tag.getAsString());
            Block block = ForgeRegistries.BLOCKS.getValue(key);

            if (block != null) {
                blocks.add(block);
            }
        }
        this.entityData.set(DATA_BLOCKS, blocks);

        this.explosionTime = pCompound.getInt("explosion_time");
        this.falling = pCompound.getBoolean("falling");
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        LivingEntity owner = this.getOwner();

        int ownerId = owner == null ? 0 : owner.getId();
        return new ClientboundAddEntityPacket(this, ownerId);
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        LivingEntity owner = (LivingEntity) this.level.getEntity(pPacket.getData());

        if (owner != null) {
            this.setOwner(owner);
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
