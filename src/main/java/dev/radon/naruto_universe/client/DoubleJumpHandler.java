package dev.radon.naruto_universe.client;

import dev.radon.naruto_universe.sound.SoundRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jline.keymap.KeyMap;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Min;

public class DoubleJumpHandler {
    private static boolean hasJumped = false;

    public static void run(LocalPlayer player) {
        if (!player.isOnGround() && !hasJumped) {
            player.jumpFromGround();
            player.level.addParticle(ParticleTypes.CLOUD, player.getX(), player.getBoundingBox().minY,
                    player.getZ(), 0.0D, 0.0D, 0.0D);
            hasJumped = true;
        }
    }

    public static void tick(LocalPlayer player) {
        if (hasJumped && player.isOnGround()) {
            hasJumped = false;
        }
    }
}
