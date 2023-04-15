package radon.naruto_universe.ability.utility;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.sound.NarutoSounds;

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
    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return new AbilityDisplayInfo(this.getId().getPath(), 4.0F, 0.0F);
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.POWER_CHARGE.get();
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        if (!ChakraControl.isWaterWalking(owner) && !owner.isOnGround() || owner.isPassenger()) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    private void jump(LivingEntity owner) {
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

        owner.level.playLocalSound(owner.getX(), owner.getY(), owner.getZ(), NarutoSounds.CHAKRA_JUMP.get(), SoundSource.MASTER, 1.0F, 1.0F, false);
    }

    @Override
    public void runServer(LivingEntity owner) {
        jump(owner);

        if (owner instanceof Player player) {
            owner.level.playSound(player, owner.getX(), owner.getY(), owner.getZ(), NarutoSounds.CHAKRA_JUMP.get(), SoundSource.MASTER, 1.0F, 1.0F);
        }
        else {
            owner.level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), NarutoSounds.CHAKRA_JUMP.get(), SoundSource.MASTER, 1.0F, 1.0F);
        }
    }
}
