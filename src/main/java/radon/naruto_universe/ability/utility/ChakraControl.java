package radon.naruto_universe.ability.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.client.particle.VaporParticle;

import java.util.Random;

public class ChakraControl extends Ability implements Ability.Toggled {

    @Override
    public NinjaRank getRank() {
        return NinjaRank.ACADEMY_STUDENT;
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public long getCombo() {
        return 2;
    }

    @Override
    public boolean isUnlocked(Player player) {
        return true;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        return new AbilityDisplayInfo(iconPath, 0.0F, 0.0F);
    }

    @Override
    public Ability getParent() {
        return null;
    }

    @Override
    public ChatFormatting getChatColor() {
        return ChatFormatting.AQUA;
    }

    @Override
    public float getCost() {
        return 0.001F;
    }

    private boolean isFluid(Level level, BlockPos pos) {
        return level.getFluidState(pos).is(Fluids.WATER) ||
                level.getFluidState(pos).is(Fluids.FLOWING_WATER);
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

        if (this.isFluid(owner.level, new BlockPos(ankles.maxX, ankles.maxY, ankles.maxZ))) {
            movementY = 0.5D;
        }
        else if (this.isFluid(owner.level, new BlockPos(ankles.minX, ankles.minY, ankles.minZ))) {
            movementY = 0.25D;
        }
        else if (movementY < 0.0D && this.isFluid(owner.level, new BlockPos(feet.minX, feet.minY, feet.minZ))) {
            movementY = 0.1D;
        }
        else if (movementY < 0.0D && this.isFluid(owner.level, new BlockPos(feet.minX, feet.minY - 0.1D, feet.minZ))) {
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
        this.checkWaterWalking(owner);
        this.checkWallClimbing(owner);
    }

    @Override
    public void runServer(LivingEntity owner) {
        this.checkWaterWalking(owner);
        this.checkWallClimbing(owner);

        Random random = new Random();

        ServerLevel serverLevel = (ServerLevel) owner.getLevel();

        serverLevel.sendParticles(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.CHAKRA_COLOR, 1.25F, 0.75F, true, 2),
                owner.getX() + (random.nextGaussian() * 0.1D) + 0.15D, owner.getY(), owner.getZ() + random.nextGaussian() * 0.1D,
                0, 0.0D, 0.23D, 0.0D, -0.1D);

        serverLevel.sendParticles(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.CHAKRA_COLOR, 1.25F, 0.75F, true, 2),
                owner.getX() + (random.nextGaussian() * 0.1D) - 0.15D, owner.getY(), owner.getZ() + random.nextGaussian() * 0.1D,
                0, 0.0D, 0.23D, 0.0D, -0.1D);
    }
}
