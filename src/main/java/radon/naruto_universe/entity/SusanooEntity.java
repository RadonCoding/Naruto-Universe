package radon.naruto_universe.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.capability.MangekyoType;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.SusanooStage;
import radon.naruto_universe.client.particle.VaporParticle;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

import static net.minecraftforge.common.ForgeMod.REACH_DISTANCE;

public class SusanooEntity extends Mob {
    private UUID ownerUUID;
    private LivingEntity cachedOwner;

    private SusanooStage stage = SusanooStage.RIBCAGE;
    private MangekyoType variant;

    public static final UUID REACH_DISTANCE_UUID = UUID.fromString("D1BCE29F-8660-464F-9B44-51E2E56F7870");
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("D1BCE29F-8660-464F-9B44-51E2E56F7871");
    public static final UUID ATTACK_KNOCKBACK_UUID = UUID.fromString("D1BCE29F-8660-464F-9B44-51E2E56F7873");
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("D1BCE29F-8660-464F-9B44-51E2E56F7872");

    public SusanooEntity(EntityType<? extends SusanooEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SusanooEntity(LivingEntity owner) {
        this(NarutoEntities.SUSANOO.get(), owner.level);

        this.moveTo(owner.position());
        this.reapplyPosition();

        owner.startRiding(this);
        this.setOwner(owner);

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> this.variant = cap.getMangekyoType());
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
                case SKELETAL -> 5.0F;
                default -> 0.0F;
            };

            float meleeAmount = switch (stage) {
                case SKELETAL -> 30.0F;
                default -> 0.0F;
            };

            float knockbackAmount = switch (stage) {
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

            AttributeModifier knockbackModifier = new AttributeModifier(ATTACK_KNOCKBACK_UUID, "Attack Knockback Boost", knockbackAmount, AttributeModifier.Operation.ADDITION);
            AttributeInstance knockbackAttribute = owner.getAttribute(Attributes.ATTACK_KNOCKBACK);

            if (knockbackAttribute != null) {
                if (knockbackAttribute.hasModifier(knockbackModifier)) {
                    knockbackAttribute.removeModifier(knockbackModifier);
                }
                knockbackAttribute.addTransientModifier(knockbackModifier);
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
            case RIBCAGE -> EntityDimensions.fixed(1.0F, 2.2F);
            case SKELETAL -> EntityDimensions.fixed(3.0F, 4.2F);
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
            this.setRot(owner.getYRot(), owner.getXRot());
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

                AABB bounds = this.getBoundingBox();

                double bbWidth = bounds.maxX - bounds.minX;
                double bbHeight = bounds.maxY - bounds.minY;
                double bbDepth = bounds.maxZ - bounds.minZ;
                double bbSize = Math.max(Math.max(bbWidth, bbHeight), bbDepth);
                int particleCount = (int) Math.round(bbSize * 3.0D);

                for (int i = 0; i < particleCount; i++) {
                    double xPos = bounds.minX + rand.nextDouble() * bbWidth;
                    double yPos = bounds.minY + rand.nextDouble() * bbHeight;
                    double zPos = bounds.minZ + rand.nextDouble() * bbDepth;

                    float particleSize = Math.min(2.5F, (float) bbSize * 3.0F);

                    this.level.addParticle(new VaporParticle.VaporParticleOptions(this.variant.getSusanooColor(), particleSize, 0.25F, false, 1),
                            xPos, yPos, zPos, 0.0D, rand.nextDouble(), 0.0D);
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

    int pack(int val1, int val2, int val3)
    {
        return (((val1 & 0xFFFF) << 16) | ((val2 & 0xFFFF) << 8) | (val3 & 0xFFFF));
    }

    int[] unpack(int packed)
    {
        int val1 = ((packed >> 16) & 0xFFFF);

        if ((val1 & 0x8000) != 0) {
            val1 |= 0xFFFF0000;
        }

        int val2 = ((packed >> 8) & 0xFF);

        if ((val2 & 0x80) != 0) {
            val2 |= 0xFFFFFF00;
        }

        int val3 = (packed & 0xFF);

        if ((val3 & 0x80) != 0) {
            val3 |= 0xFFFFFF00;
        }

        return new int[] { val1, val2, val3 };
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        LivingEntity owner = this.getOwner();

        int ownerId = owner == null ? 0 : owner.getId();
        int stage = this.stage.ordinal();
        int variant = this.variant.ordinal();

        int data = pack(ownerId, stage, variant);
        return new ClientboundAddEntityPacket(this, data);
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        int[] data = unpack(pPacket.getData());
        int ownerId = data[0];
        int stage = data[1];
        int variant = data[2];

        LivingEntity owner = (LivingEntity) this.level.getEntity(ownerId);

        if (owner != null) {
            this.moveTo(owner.position());
            this.reapplyPosition();

            owner.startRiding(this);
            this.setOwner(owner);

            this.stage = SusanooStage.values()[stage];
            this.variant = MangekyoType.values()[variant];

            this.updateModifiers(this.stage);
        }
    }
}

