package dev.radon.naruto_universe.ability.utility;

import dev.radon.naruto_universe.capability.NinjaRank;
import dev.radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.sound.SoundRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ChakraJump extends Ability {

    @Override
    public NinjaRank getRank() {
        return NinjaRank.ACADEMY_STUDENT;
    }

    @Override
    public SoundEvent getActivationSound() {
        return null;
    }

    @Override
    public boolean shouldLog() {
        return false;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        AbilityDisplayInfo info = new AbilityDisplayInfo(iconPath, 4.0F, 0.0F);
        return info;
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.CHAKRA_CHARGE.get();
    }

    @Override
    public float getCost() {
        return 1.0F;
    }

    private void jump(Player player) {
        if (!player.isOnGround()) {
            return;
        }

        player.level.playSound(null, player.blockPosition(), SoundRegistry.CHAKRA_JUMP.get(), SoundSource.PLAYERS,
                1.0F, 1.0F);

        float jumpFactor = player.level.getBlockState(player.blockPosition()).getBlock().getJumpFactor();
        float blockJumpFactor = player.level.getBlockState(new BlockPos(player.getX(), player.getBoundingBox().minY - 0.5000001D, player.getY()))
                .getBlock().getJumpFactor();
        float jumpPower = jumpFactor == 1.0F ? blockJumpFactor : jumpFactor;

        double totalJumpPower = jumpPower + player.getJumpBoostPower();
        Vec3 movement = player.getDeltaMovement();
        player.setDeltaMovement(movement.x(), totalJumpPower, movement.z());

        if (player.isSprinting()) {
            float f = player.getYRot() * ((float) Math.PI / 180.0F);
            player.setDeltaMovement(player.getDeltaMovement().add(-Mth.sin(f), 0.0D, Mth.cos(f)));
        }
    }

    @Override
    public void runClient(LocalPlayer player) {
        jump(player);
    }

    @Override
    public void runServer(ServerPlayer player) {
        jump(player);
    }
}
