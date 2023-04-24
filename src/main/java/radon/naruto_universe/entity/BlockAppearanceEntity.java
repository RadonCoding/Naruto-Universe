package radon.naruto_universe.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.entity.base.NarutoEntityBase;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.Optional;

public class BlockAppearanceEntity extends NarutoEntityBase implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Optional<BlockState>> DATA_BLOCK = SynchedEntityData.defineId(BlockAppearanceEntity.class, EntityDataSerializers.BLOCK_STATE);

    protected BlockAppearanceEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BlockAppearanceEntity(Level level, BlockPos pos, BlockState state) {
        super(NarutoEntities.BLOCK_APPEARANCE.get(), level);

        this.moveTo(pos.getX(), pos.getY(), pos.getZ(), this.getYRot(), this.getXRot());
        this.reapplyPosition();

        this.entityData.set(DATA_BLOCK, Optional.of(state));
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canCollideWith(@NotNull Entity pEntity) {
        if (pEntity instanceof BlockAppearanceEntity || pEntity instanceof ChibakuTenseiEntity) {
            return false;
        }
        return super.canCollideWith(pEntity);
    }

    public BlockState getBlockState() {
        return this.entityData.get(DATA_BLOCK).orElse(null);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_BLOCK, Optional.empty());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        BlockState state = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), pCompound.getCompound("block"));
        this.entityData.set(DATA_BLOCK, Optional.of(state));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        pCompound.put("block", NbtUtils.writeBlockState(this.getBlockState()));
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
