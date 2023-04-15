package radon.naruto_universe.client.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaPlayerHandler;

public class AnimationHandler {
    public static void animate(LivingEntity entity, HumanoidModel<?> model) {
        Vec3 movement = entity.getDeltaMovement();
        double speed = Math.sqrt(movement.x() * movement.x() + movement.z() * movement.z());

        if (speed >= 0.15D && !entity.isSwimming() && !(entity instanceof LocalPlayer player && player.getAbilities().flying)) {
            model.body.xRot = 0.5F;

            boolean rotateLeftArm = true, rotateRightArm = true;

            if (entity.isUsingItem()) {
                rotateRightArm = entity.getUsedItemHand() != InteractionHand.MAIN_HAND;
                rotateLeftArm = entity.getUsedItemHand() != InteractionHand.OFF_HAND;
            } else if (entity.swinging) {
                rotateRightArm = entity.swingingArm != InteractionHand.MAIN_HAND;
                rotateLeftArm = entity.swingingArm != InteractionHand.OFF_HAND;
            }

            if (rotateRightArm) {
                model.rightArm.xRot = 1.6F;
            }

            if (rotateLeftArm) {
                model.leftArm.xRot = 1.6F;
            }

            model.head.y = 4.2F;
            model.body.y = 3.2F;
            model.rightArm.y = 5.2F;
            model.leftArm.y = 5.2F;
            model.rightLeg.y = 12.2F;
            model.leftLeg.y = 12.2F;
            model.rightLeg.z = 4.0F;
            model.leftLeg.z = 4.0F;
        }
    }
}
