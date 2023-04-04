package radon.naruto_universe.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import radon.naruto_universe.ability.utility.ChakraControl;

public class DoubleJumpHandler {
    private static boolean hasJumped = false;

    public static void run(LocalPlayer player) {
        if (!player.isOnGround() && !player.getAbilities().flying && !hasJumped) {
            player.jumpFromGround();
            player.level.addParticle(ParticleTypes.CLOUD, player.getX(), player.getBoundingBox().minY,
                    player.getZ(), 0.0D, 0.0D, 0.0D);
            hasJumped = true;
        }
    }

    public static void tick(LocalPlayer player) {
        if (hasJumped && (ChakraControl.isWaterWalking(player) || player.isOnGround())) {
            hasJumped = false;
        }
    }
}
