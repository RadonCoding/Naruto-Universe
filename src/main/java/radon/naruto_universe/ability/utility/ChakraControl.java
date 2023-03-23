package radon.naruto_universe.ability.utility;

import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

    private void checkWaterWalking(Player player) {
        if (player.isShiftKeyDown()) return;

        AABB bb = player.getBoundingBox();
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

        Vec3 movement = player.getDeltaMovement();
        double movementY = movement.y();

        if (this.isFluid(player.level, new BlockPos(ankles.maxX, ankles.maxY, ankles.maxZ))) {
            movementY = 0.5D;
        }
        else if (this.isFluid(player.level, new BlockPos(ankles.minX, ankles.minY, ankles.minZ))) {
            movementY = 0.25D;
        }
        else if (movementY < 0.0D && this.isFluid(player.level, new BlockPos(feet.minX, feet.minY, feet.minZ))) {
            movementY = 0.1D;
        }
        else if (movementY < 0.0D && this.isFluid(player.level, new BlockPos(feet.minX, feet.minY - 0.1D, feet.minZ))) {
            movementY = 0.0D;
        }

        if (movementY != movement.y()) {
            player.setDeltaMovement(movement.x(), movementY, movement.z());
            player.setOnGround(true);
            player.resetFallDistance();
        }
    }

    private void checkWallClimbing(Player player) {
        if (!player.isOnGround() && !player.isInWater() && player.level.getBlockState(player.blockPosition().above()).isAir()) {
            float climbSpeed = 0.2F; // Change this to adjust the climbing speed

            Vec3 motion = player.getDeltaMovement();

            player.fallDistance = 0.0F;

            if (player.horizontalCollision) {
                player.setDeltaMovement(motion.x, climbSpeed, motion.z);
            }
        }
    }

    @Override
    public void runClient(LocalPlayer player) {
        this.checkWaterWalking(player);
        this.checkWallClimbing(player);
    }

    @Override
    public void runServer(ServerPlayer player) {
        this.checkWaterWalking(player);
        this.checkWallClimbing(player);

        Random random = new Random();

        ServerLevel serverLevel = player.getLevel();

        serverLevel.sendParticles(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.CHAKRA_COLOR, 0.5F, true, 3),
                player.getX() + (random.nextGaussian() * 0.1D) + 0.15D, player.getY(), player.getZ() + random.nextGaussian() * 0.1D,
                0, 0.0D, 0.23D, 0.0D, -0.1D);

        serverLevel.sendParticles(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.CHAKRA_COLOR, 0.5F, true, 3),
                player.getX() + (random.nextGaussian() * 0.1D) - 0.15D, player.getY(), player.getZ() + random.nextGaussian() * 0.1D,
                0, 0.0D, 0.23D, 0.0D, -0.1D);
    }
}
