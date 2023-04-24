package radon.naruto_universe.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.capability.ninja.MangekyoType;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.SusanooStage;
import radon.naruto_universe.client.particle.VaporParticle;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SyncSusanooAnimationS2CPacket;
import radon.naruto_universe.util.HelperMethods;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

public class SusanooEntity extends Mob implements GeoAnimatable {
    public static final RawAnimation GRAB = RawAnimation.begin().thenPlayAndHold("attack.grab");
    public static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    public static final RawAnimation CRUSH = RawAnimation.begin().thenLoop("attack.crush");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(SusanooEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(SusanooEntity.class, EntityDataSerializers.INT);

    private static final double GRAB_RAYCAST_RANGE = 10.0D;
    private static final float LAUNCH_POWER = 10.0F;

    private UUID ownerUUID;
    private LivingEntity cachedOwner;

    private SusanooAnimationState state = new SusanooAnimationState();
    private Entity grabbed;

    public SusanooEntity(EntityType<? extends SusanooEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SusanooEntity(LivingEntity owner) {
        this(NarutoEntities.SUSANOO.get(), owner.level);

        this.init(owner);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_VARIANT, -1);
        this.entityData.define(DATA_STAGE, -1);
    }

    private void init(LivingEntity owner) {
        this.moveTo(owner.position());
        this.reapplyPosition();

        owner.startRiding(this);
        this.setOwner(owner);

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            this.entityData.set(DATA_VARIANT, cap.getMangekyoType().ordinal());
            this.entityData.set(DATA_STAGE, SusanooStage.RIBCAGE.ordinal());
        });
    }

    public SusanooStage getStage() {
        return SusanooStage.values()[this.entityData.get(DATA_STAGE)];
    }

    public MangekyoType getVariant() {
        return MangekyoType.values()[this.entityData.get(DATA_VARIANT)];
    }

    @Override
    public boolean isColliding(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return false;
    }

    @Override
    public boolean showVehicleHealth() {
        return true;
    }

    @Override
    protected float tickHeadTurn(float pYRot, float pAnimStep) {
        return 0.0F;
    }

    public void incrementStage() {
        LivingEntity owner = this.getOwner();

        assert owner != null;

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            int stage = this.getStage().ordinal();
            SusanooStage[] stages = SusanooStage.values();

            if (stage < stages.length) {
                SusanooStage newStage = stages[++stage];

                if (cap.getExperience() >= newStage.getExperience()) {
                    this.entityData.set(DATA_STAGE, newStage.ordinal());
                }
            }
        });
    }

    public void decrementStage() {
        int stage = this.getStage().ordinal();

        if (stage > 0) {
            this.entityData.set(DATA_STAGE, --stage);
        }
    }

    public float getReach() {
        return switch (this.getStage()) {
            case RIBCAGE -> 5.0F;
            case SKELETAL -> 7.5F;
            default -> 4.5F;
        };
    }

    public float getDamage() {
        return switch (this.getStage()) {
            case RIBCAGE -> 25.0F;
            case SKELETAL -> 50.0F;
            default -> 1.0F;
        };
    }

    public void updateAnimationState(SusanooAnimationState state) {
        this.state = state;
    }

    public void onLeftClick() {
        if (!this.state.grabbing && !this.state.crushing) {
            this.swing(InteractionHand.MAIN_HAND, true);

            LivingEntity owner = this.getOwner();
            EntityHitResult result = HelperMethods.getLivingEntityLookAt(owner, this.getReach(), 1.0F);

            if (result != null) {
                Entity target = result.getEntity();
                target.hurt(DamageSource.indirectMobAttack(this, this.getOwner()), this.getDamage());

                Vec3 look = owner.getLookAngle();
                target.setDeltaMovement(look.x() * LAUNCH_POWER, look.y() * LAUNCH_POWER, look.z() * LAUNCH_POWER);

                if (!this.level.isClientSide) {
                    Random rand = new Random();

                    if (owner instanceof Player player) {
                        this.level.playSound(player, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 3.0F, 1.0F);
                    }
                    else {
                        this.level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 3.0F, 1.0F);
                    }

                    for (int i = 0; i < 5; i++) {
                        ServerLevel level = (ServerLevel) this.level;

                        float f = (rand.nextFloat() - 0.5F) * 4.0F;
                        float f1 = (rand.nextFloat() - 0.5F) * 2.0F;
                        float f2 = (rand.nextFloat() - 0.5F) * 4.0F;
                        level.sendParticles(ParticleTypes.EXPLOSION,
                                target.getX() + f,
                                target.getY() + 2.0D + f1,
                                target.getZ() + f2,
                                0, 1.0D, 1.0D, 1.0D, 0.1D);
                    }
                } else {
                    this.level.playLocalSound(owner.getX(), owner.getY(), owner.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 3.0F, 1.0F, false);
                }
            }
        }
    }

    public void onRightClick() {
        LivingEntity owner = this.getOwner();

        SusanooStage stage = this.getStage();

        if (stage == SusanooStage.RIBCAGE || stage == SusanooStage.SKELETAL) {
            if (!this.state.grabbing) {
                EntityHitResult hit = HelperMethods.getLivingEntityLookAt(owner, GRAB_RAYCAST_RANGE, 1.0F);

                if (hit != null) {
                    Entity entity = hit.getEntity();

                    if (entity.getBbWidth() <= 0.9F && entity.getBbHeight() <= 1.95F) {
                        this.grab(entity);
                    }
                }
            }
            else if (owner.isShiftKeyDown()) {
                this.release();
            }
            else {
                this.crush();
            }
        }
    }

    private void crush() {
        this.state.crushingTime = 10 * 20;
        this.state.crushing = true;

        if (!this.level.isClientSide) {
            PacketHandler.broadcast(new SyncSusanooAnimationS2CPacket(this.getId(), this.state));
        }
    }

    private void release() {
        this.state.crushing = false;
        this.state.crushingTime = 0;
        this.state.grabbing = false;

        if (!this.level.isClientSide) {
            PacketHandler.broadcast(new SyncSusanooAnimationS2CPacket(this.getId(), this.state));
        }
    }

    private void grab(Entity grabbed) {
        this.state.grabbing = true;
        this.grabbed = grabbed;

        if (!this.level.isClientSide) {
            PacketHandler.broadcast(new SyncSusanooAnimationS2CPacket(this.getId(), this.state));
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 500.0D).add(Attributes.MOVEMENT_SPEED, 1.0D);
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return true;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public double getPassengersRidingOffset() {
        return switch (this.getStage()) {
            case RIBCAGE, SKELETAL -> 0.35D;
            case HUMANOID -> 3.5D;
            default -> super.getPassengersRidingOffset();
        };
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        return false;
    }

    @Override
    public boolean addEffect(@NotNull MobEffectInstance pEffectInstance, @Nullable Entity pEntity) {
        return false;
    }

    @Override
    public boolean isInWall() {
        return false;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        LivingEntity owner = this.getOwner();
        return owner instanceof Player player && player.isCreative();
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() == this.getOwner()) {
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return SoundEvents.PLAYER_ATTACK_KNOCKBACK;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return switch (this.getStage()) {
            case RIBCAGE -> EntityDimensions.fixed(1.8F, 2.6F);
            case SKELETAL -> EntityDimensions.fixed(2.4F, 4.0F);
            case HUMANOID -> EntityDimensions.fixed(2.8F, 6.0F);
            default -> super.getDimensions(pPose);
        };
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        return this.getFirstPassenger();
    }

    @Override
    public float getStepHeight() {
        return switch (this.getStage()) {
            case RIBCAGE, SKELETAL, HUMANOID -> 1.0F;
            default -> super.getStepHeight();
        };
    }

    @Override
    protected void removePassenger(@NotNull Entity pPassenger) {
        super.removePassenger(pPassenger);

        if (!this.level.isClientSide) {
            this.discard();
        }
    }

    @Override
    public void travel(@NotNull Vec3 pTravelVector) {
        LivingEntity owner = this.getOwner();

        if (owner != null) {
            this.setYRot(owner.getYRot());
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

            float f = owner.xxa;
            float f1 = owner.zza;

            if (f1 <= 0.0F) {
                f1 *= 0.25F;
            }

            this.flyingSpeed = this.getSpeed() * 0.1F;

            if (this.isControlledByLocalInstance()) {
                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(f, pTravelVector.y(), f1));
            } else if (owner instanceof Player) {
                this.setDeltaMovement(this.getX() - this.xOld, this.getY() - this.yOld, this.getZ() - this.zOld);
            }
        }
    }

    @Override
    public void tick() {
        this.refreshDimensions();

        LivingEntity owner = this.getOwner();

        if (!this.level.isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();

            if (owner != null) {
                Random rand = new Random();

                if (rand.nextInt(20) == 0) {
                    this.level.playSound(null, owner.blockPosition(), SoundEvents.FIRE_AMBIENT,
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                }

                AABB bounds = this.getBoundingBox().inflate(0.25D);

                double bbWidth = bounds.maxX - bounds.minX;
                double bbHeight = bounds.maxY - bounds.minY;
                double bbDepth = bounds.maxZ - bounds.minZ;
                double bbSize = Math.max(Math.max(bbWidth, bbHeight), bbDepth);
                int particleCount = (int) Math.round(bbSize * 5.0D);

                for (int i = 0; i < particleCount; i++) {
                    double xPos = bounds.minX + rand.nextDouble() * bbWidth;
                    double yPos = bounds.minY + rand.nextDouble() * bbHeight;
                    double zPos = bounds.minZ + rand.nextDouble() * bbDepth;

                    float particleSize = Math.min(2.5F, (float) bbSize * 5.0F);

                    this.level.addParticle(new VaporParticle.VaporParticleOptions(this.getVariant().getSusanooColor(), particleSize, 0.15F, false, 3),
                            xPos, yPos, zPos, 0.0D, rand.nextDouble(), 0.0D);
                }
            }

            if (this.state.grabbing) {
                if (this.grabbed != null) {
                    if (this.grabbed.isRemoved() || !this.grabbed.isAlive()) {
                        this.release();
                    } else {
                        this.grabbed.setDeltaMovement(Vec3.ZERO);

                        Vec3 look = this.getLookAngle();
                        double yOffset = 0.0D;

                        SusanooStage stage = this.getStage();

                        if (stage == SusanooStage.RIBCAGE) {
                            look = look.multiply(1.2D, 1.2D, 1.2D);
                            yOffset = 0.5D;
                        } else if (stage == SusanooStage.SKELETAL) {
                            look = look.multiply(1.8D, 1.8D, 1.8D);
                            yOffset = 1.0D;
                        }

                        Vec3 pos = new Vec3(this.getX() + look.x(), this.getY() + yOffset, this.getZ() + look.z());
                        this.grabbed.moveTo(pos);
                    }
                }

                if (this.state.crushing) {
                    this.state.crushingTime--;

                    if (this.state.crushingTime > 0) {
                        if (this.grabbed != null) {
                            this.grabbed.hurt(DamageSource.indirectMobAttack(this, owner), this.getDamage() * 0.25F);
                        }
                    } else {
                        this.release();
                    }
                }
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
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
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }
        pCompound.putInt("stage", this.getStage().ordinal());
        pCompound.putInt("variant", this.getVariant().ordinal());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.entityData.set(DATA_STAGE, pCompound.getInt("stage"));
        this.entityData.set(DATA_VARIANT, pCompound.getInt("variant"));
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
            this.init(owner);
        }
    }

    private PlayState attackPredicate(AnimationState<SusanooEntity> animationState) {
        if (this.swinging && animationState.getController().getAnimationState() == AnimationController.State.STOPPED) {
            animationState.getController().forceAnimationReset();
            animationState.setAnimation(SWING);
            this.swinging = false;
        }
        return PlayState.CONTINUE;
    }

    private PlayState predicate(AnimationState<SusanooEntity> animationState) {
        if (this.state.crushing) {
            if (!animationState.isCurrentAnimation(CRUSH)) {
                animationState.setAnimation(CRUSH);
            }
            return PlayState.CONTINUE;
        }
        else if (this.state.grabbing) {
            if (!animationState.isCurrentAnimation(GRAB)) {
                animationState.setAnimation(GRAB);
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackController", this::attackPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object o) {
        return RenderUtils.getCurrentTick();
    }

    public static class SusanooAnimationState {
        public boolean grabbing;
        public boolean crushing;
        public int crushingTime;

        public SusanooAnimationState() {
            this.grabbing = false;
            this.crushing = false;
            this.crushingTime = 0;
        }

        public SusanooAnimationState(FriendlyByteBuf buf) {
            this.grabbing = buf.readBoolean();
            this.crushing = buf.readBoolean();
            this.crushingTime = buf.readInt();
        }

        public void serialize(FriendlyByteBuf buf) {
            buf.writeBoolean(this.grabbing);
            buf.writeBoolean(this.crushing);
            buf.writeInt(this.crushingTime);
        }
    }
}