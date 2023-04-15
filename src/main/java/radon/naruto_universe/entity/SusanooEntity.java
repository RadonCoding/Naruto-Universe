package radon.naruto_universe.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.capability.MangekyoType;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.SusanooStage;
import radon.naruto_universe.client.particle.VaporParticle;
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

import static net.minecraftforge.common.ForgeMod.REACH_DISTANCE;

public class SusanooEntity extends Mob implements GeoAnimatable {
    public static final RawAnimation GRAB = RawAnimation.begin().thenPlayAndHold("attack.grab");
    public static final RawAnimation SWING = RawAnimation.begin().thenPlayAndHold("attack.swing");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private UUID ownerUUID;
    private LivingEntity cachedOwner;

    private SusanooStage stage = SusanooStage.RIBCAGE;
    private MangekyoType variant;
    private boolean grabbing;
    private boolean crushing;
    private int crushingTime;
    private Entity grabbed;

    public static final UUID REACH_DISTANCE_UUID = UUID.fromString("D1BCE29F-8660-464F-9B44-51E2E56F7870");
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("D1BCE29F-8660-464F-9B44-51E2E56F7871");
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("D1BCE29F-8660-464F-9B44-51E2E56F7872");

    public SusanooEntity(EntityType<? extends SusanooEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SusanooEntity(LivingEntity owner) {
        this(NarutoEntities.SUSANOO.get(), owner.level);

        this.init(owner);
    }

    private void init(LivingEntity owner) {
        this.moveTo(owner.position());
        this.reapplyPosition();

        owner.startRiding(this);
        this.setOwner(owner);

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            this.variant = cap.getMangekyoType();
            this.stage = SusanooStage.RIBCAGE;
        });
        this.updateModifiers(stage);
    }

    public SusanooStage getStage() {
        return this.stage;
    }

    public MangekyoType getVariant() {
        return this.variant;
    }

    @Override
    public boolean save(@NotNull CompoundTag pCompound) {
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
            int stage = this.stage.ordinal();
            SusanooStage[] stages = SusanooStage.values();

            if (stage < stages.length) {
                SusanooStage newStage = stages[++stage];

                if (cap.getExperience() >= newStage.getExperience()) {
                    this.stage = newStage;
                    this.updateModifiers(newStage);
                }
            }
        });
    }

    public void decrementStage() {
        int stage = this.stage.ordinal();
        SusanooStage[] stages = SusanooStage.values();

        if (stage > 0) {
            SusanooStage newStage = stages[--stage];
            this.stage = newStage;
            this.updateModifiers(newStage);
        }
    }

    public void onLeftClick() {
        this.swing(InteractionHand.MAIN_HAND);
    }

    public void onRightClick() {
        LivingEntity owner = this.getOwner();

        if (this.stage == SusanooStage.RIBCAGE) {
            if (!this.grabbing) {
                EntityHitResult hit = HelperMethods.getEntityLookAt(owner, 5.0F);

                if (hit != null) {
                    this.grab(hit.getEntity());
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
        this.crushingTime = 10 * 20;
        this.crushing = true;
    }

    private void release() {
        this.crushing = false;
        this.crushingTime = 0;
        this.grabbing = false;
    }

    private void grab(Entity grabbed) {
        this.grabbing = true;
        this.grabbed = grabbed;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 500.0D);
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
    public boolean isAttackable() {
        return false;
    }

    @Override
    public double getPassengersRidingOffset() {
        return switch (this.stage) {
            case RIBCAGE, SKELETAL -> 0.35D;
            default -> super.getPassengersRidingOffset();
        };
    }

    private void updateModifiers(SusanooStage stage) {
        LivingEntity owner = this.getOwner();

        AttributeModifier healthModifier = new AttributeModifier(MAX_HEALTH_UUID, "Max Health", stage.ordinal() * 500.0D, AttributeModifier.Operation.ADDITION);
        AttributeInstance healthAttribute = this.getAttribute(Attributes.MAX_HEALTH);

        if (healthAttribute != null) {
            if (healthAttribute.hasModifier(healthModifier)) {
                healthAttribute.removeModifier(healthModifier);
            }
            healthAttribute.addTransientModifier(healthModifier);
        }

        if (owner != null) {
            float reachAmount = switch (stage) {
                case RIBCAGE -> 3.0F;
                case SKELETAL -> 5.0F;
                default -> 0.0F;
            };

            float meleeAmount = switch (stage) {
                case RIBCAGE -> 15.0F;
                case SKELETAL -> 30.0F;
                default -> 0.0F;
            };

            AttributeModifier reachModifier = new AttributeModifier(REACH_DISTANCE_UUID, "Reach Distance Boost", reachAmount, AttributeModifier.Operation.ADDITION);
            AttributeInstance reachAttribute = owner.getAttribute(REACH_DISTANCE.get());

            if (reachAttribute != null) {
                if (reachAttribute.hasModifier(reachModifier)) {
                    reachAttribute.removeModifier(reachModifier);
                }
                reachAttribute.addTransientModifier(reachModifier);
            }

            AttributeModifier meleeModifier = new AttributeModifier(ATTACK_DAMAGE_UUID, "Attack Damage Boost", meleeAmount, AttributeModifier.Operation.ADDITION);
            AttributeInstance meleeAttribute = owner.getAttribute(Attributes.ATTACK_DAMAGE);

            if (meleeAttribute != null) {
                if (meleeAttribute.hasModifier(meleeModifier)) {
                    meleeAttribute.removeModifier(meleeModifier);
                }
                meleeAttribute.addTransientModifier(meleeModifier);
            }
        }
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
        return switch (this.stage) {
            case RIBCAGE -> EntityDimensions.fixed(1.8F, 2.6F);
            case SKELETAL -> EntityDimensions.fixed(2.4F, 4.0F);
            default -> super.getDimensions(pPose);
        };
    }

    @Override
    public boolean canCollideWith(@NotNull Entity pEntity) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        return this.getFirstPassenger();
    }

    @Override
    public float getStepHeight() {
        return switch (this.stage) {
            case RIBCAGE, SKELETAL -> 1.0F;
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
                this.setSpeed((float) owner.getAttributeValue(Attributes.MOVEMENT_SPEED));
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

                    this.level.addParticle(new VaporParticle.VaporParticleOptions(this.variant.getSusanooColor(), particleSize, 0.1F, false, 1),
                            xPos, yPos, zPos, 0.0D, rand.nextDouble(), 0.0D);
                }
            }

            if (this.grabbing) {
                if ((!this.level.isClientSide && this.grabbed == null) || (this.grabbed.isRemoved() || !this.grabbed.isAlive())) {
                    this.release();
                }
                else {
                    this.grabbed.setDeltaMovement(Vec3.ZERO);

                    Vec3 look = this.getLookAngle().scale(1.25D);
                    Vec3 pos = new Vec3(this.getX() + look.x(), this.getY() + 0.5D, this.getZ() + look.z());
                    this.grabbed.moveTo(pos);

                    if (this.crushing) {
                        this.crushingTime--;

                        if (this.crushingTime > 0) {
                            this.grabbed.hurt(DamageSource.indirectMobAttack(this, owner), 50.0F);
                        } else {
                            this.release();
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
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
        pCompound.putInt("stage", this.stage.ordinal());
        pCompound.putInt("variant", this.variant.ordinal());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }
        this.stage = SusanooStage.values()[pCompound.getInt("stage")];
        this.variant = MangekyoType.values()[pCompound.getInt("variant")];
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

    private PlayState predicate(AnimationState<SusanooEntity> animationState) {
        if (this.swinging) {
            animationState.setAnimation(SWING);
        }
        else if (this.grabbing) {
            if (!animationState.isCurrentAnimation(GRAB)) {
                animationState.setAnimation(GRAB);
            }
        }
        else if (animationState.isCurrentAnimation(GRAB)) {
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", this::predicate));
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

