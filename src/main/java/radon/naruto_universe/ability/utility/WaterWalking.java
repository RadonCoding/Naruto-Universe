package radon.naruto_universe.ability.utility;

import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.client.particle.ParticleRegistry;
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

public class WaterWalking extends Ability implements Ability.Toggled {

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
        else if (movement.y() < 0.0D && this.isFluid(player.level, new BlockPos(feet.minX + 0.1D, feet.minY + 0.1D, feet.minZ + 0.1D))) {
            movementY = 0.1D;
        }
        else if (movement.y() < 0.0D && this.isFluid(player.level, new BlockPos(feet.minX, feet.minY, feet.minZ))) {
            movementY = 0.0D;
        }

        if (movementY != movement.y()) {
            player.setDeltaMovement(movement.x(), movementY, movement.z());
            player.setOnGround(true);
            player.resetFallDistance();
        }
    }

    @Override
    public void runClient(LocalPlayer player) {
        checkWaterWalking(player);

        player.level.addParticle(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.CHAKRA_COLOR, 0.5F), player.level.random.nextGaussian() * 0.1D + player.getX(),
                player.getY() + 0.23D, player.level.random.nextGaussian() * 0.1D + player.getZ(), 0.0D, -0.1D, 0.0D);
    }

    @Override
    public void runServer(ServerPlayer player) {
        checkWaterWalking(player);
    }
}
