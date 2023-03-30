package radon.naruto_universe.ability.utility;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.sound.SoundRegistry;

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
    public boolean shouldLog(LivingEntity entity) {
        return false;
    }

    @Override
    public boolean isUnlocked(Player player) {
        return true;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        return new AbilityDisplayInfo(iconPath, 4.0F, 0.0F);
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.POWER_CHARGE.get();
    }

    @Override
    public float getCost() {
        return 1.0F;
    }

    private void jump(LivingEntity owner) {
        if (!owner.isOnGround()) {
            return;
        }

        owner.level.playSound(null, owner.blockPosition(), SoundRegistry.CHAKRA_JUMP.get(), SoundSource.PLAYERS,
                1.0F, 1.0F);

        float jumpFactor = owner.level.getBlockState(owner.blockPosition()).getBlock().getJumpFactor();
        float blockJumpFactor = owner.level.getBlockState(new BlockPos(owner.getX(), owner.getBoundingBox().minY - 0.5000001D, owner.getY()))
                .getBlock().getJumpFactor();
        float jumpPower = jumpFactor == 1.0F ? blockJumpFactor : jumpFactor;

        double totalJumpPower = jumpPower + owner.getJumpBoostPower();
        Vec3 movement = owner.getDeltaMovement();
        owner.setDeltaMovement(movement.x(), totalJumpPower, movement.z());

        if (owner.isSprinting()) {
            float f = owner.getYRot() * ((float) Math.PI / 180.0F);
            owner.setDeltaMovement(owner.getDeltaMovement().add(-Mth.sin(f), 0.0D, Mth.cos(f)));
        }
    }

    @Override
    public void runClient(LivingEntity owner) {
        jump(owner);
    }

    @Override
    public void runServer(LivingEntity owner) {
        jump(owner);
    }
}
