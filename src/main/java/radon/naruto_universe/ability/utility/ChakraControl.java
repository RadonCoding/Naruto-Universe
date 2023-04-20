package radon.naruto_universe.ability.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.client.particle.VaporParticle;
import radon.naruto_universe.entity.SusanooEntity;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChakraControl extends Ability implements Ability.IToggled {
    @Override
    public NinjaRank getRank() {
        return NinjaRank.ACADEMY_STUDENT;
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return new AbilityDisplayInfo(this.getId().getPath(), 2.0F, 0.0F);
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.POWER_CHARGE.get();
    }

    @Override
    public ChatFormatting getChatColor() {
        return ChatFormatting.AQUA;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.001F;
    }

    @Override
    public boolean hasCombo() {
        return true;
    }

    private static boolean isFluid(Level level, BlockPos pos) {
        return level.getFluidState(pos).is(Fluids.WATER) ||
                level.getFluidState(pos).is(Fluids.FLOWING_WATER);
    }

    public static boolean isWaterWalking(LivingEntity entity) {
        if (entity.isShiftKeyDown()) return false;

        AtomicBoolean result = new AtomicBoolean(false);
        entity.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasToggledAbility(NarutoAbilities.CHAKRA_CONTROL.get())) {
                AABB bb = entity.getBoundingBox();
                AABB feet = new AABB(
                        bb.minX,
                        bb.minY,
                        bb.minZ,
                        bb.maxX,
                        bb.minY,
                        bb.maxZ
                );
                result.set(isFluid(entity.level, new BlockPos(feet.minX, feet.minY - 0.1D, feet.minZ)));
            }
        });
        return result.get();
    }

    private void checkWaterWalking(LivingEntity owner) {
        if (owner.isShiftKeyDown()) return;

        AABB bb = owner.getBoundingBox();
        AABB feet = new AABB(
                bb.minX,
                bb.minY,
                bb.minZ,
                bb.maxX,
                bb.minY,
                bb.maxZ
        );
        AABB ankles = new AABB(
                bb.minX,
                bb.minY + 0.5D,
                bb.minZ,
                bb.maxX,
                bb.minY + 0.5D,
                bb.maxZ
        );

        Vec3 movement = owner.getDeltaMovement();
        double movementY = movement.y();

        if (isFluid(owner.level, new BlockPos(ankles.maxX, ankles.maxY, ankles.maxZ))) {
            movementY = 0.5D;
        }
        else if (isFluid(owner.level, new BlockPos(ankles.minX, ankles.minY, ankles.minZ))) {
            movementY = 0.25D;
        }
        else if (movementY < 0.0D && isFluid(owner.level, new BlockPos(feet.minX, feet.minY, feet.minZ))) {
            movementY = 0.1D;
        }
        else if (movementY < 0.0D && isFluid(owner.level, new BlockPos(feet.minX, feet.minY - 0.1D, feet.minZ))) {
            movementY = 0.0D;
        }

        if (movementY != movement.y()) {
            owner.setDeltaMovement(movement.x(), movementY, movement.z());
            owner.setOnGround(true);
            owner.resetFallDistance();
        }
    }

    private void checkWallClimbing(LivingEntity owner) {
        if (!owner.isOnGround() && !owner.isInWater() && owner.level.getBlockState(owner.blockPosition().above()).isAir()) {
            float climbSpeed = 0.2F; // Change this to adjust the climbing speed

            Vec3 motion = owner.getDeltaMovement();

            owner.fallDistance = 0.0F;

            if (owner.horizontalCollision) {
                owner.setDeltaMovement(motion.x, climbSpeed, motion.z);
            }
        }
    }

    @Override
    public void runClient(LivingEntity owner) {
        if (owner.getVehicle() instanceof SusanooEntity susanoo) {
            this.checkWaterWalking(susanoo);
            this.checkWallClimbing(susanoo);
        } else {
            this.checkWaterWalking(owner);
            this.checkWallClimbing(owner);
        }
    }

    @Override
    public void runServer(LivingEntity owner) {
        if (owner.getVehicle() instanceof SusanooEntity susanoo) {
            this.checkWaterWalking(susanoo);
            this.checkWallClimbing(susanoo);
        } else {
            this.checkWaterWalking(owner);
            this.checkWallClimbing(owner);
        }

        Random rand = new Random();

        ServerLevel serverLevel = (ServerLevel) owner.getLevel();

        serverLevel.sendParticles(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.CHAKRA_COLOR, 1.0F, 0.5F, false, 3),
                owner.getX() + (rand.nextGaussian() * 0.1D) + 0.15D, owner.getY(), owner.getZ() + rand.nextGaussian() * 0.1D,
                0, 0.0D, 0.23D, 0.0D, -0.1D);

        serverLevel.sendParticles(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.CHAKRA_COLOR, 1.0F, 0.5F, false, 3),
                owner.getX() + (rand.nextGaussian() * 0.1D) - 0.15D, owner.getY(), owner.getZ() + rand.nextGaussian() * 0.1D,
                0, 0.0D, 0.23D, 0.0D, -0.1D);
    }
}
