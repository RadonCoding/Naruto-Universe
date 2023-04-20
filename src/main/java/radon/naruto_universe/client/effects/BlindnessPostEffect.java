package radon.naruto_universe.client.effects;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.client.NarutoPostEffect;

import java.util.concurrent.atomic.AtomicBoolean;

public class BlindnessPostEffect extends NarutoPostEffect {
    private static final ResourceLocation BLUR = new ResourceLocation("shaders/post/blobs2.json");

    @Override
    protected void applyUniforms(PostPass pass) {
        Minecraft mc = Minecraft.getInstance();

        Uniform uniform = pass.getEffect().getUniform("Radius");

        if (uniform != null) {
            assert mc.player != null;

            mc.player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                uniform.set((float) Math.min(25.0F, Math.floor(cap.getMangekyoBlindess())));
            });
        }
    }

    @Override
    protected ResourceLocation getEffect() {
        return BLUR;
    }

    @Override
    public boolean shouldRender(LocalPlayer player) {
        AtomicBoolean result = new AtomicBoolean(false);

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasUnlockedAbility(NarutoAbilities.MANGEKYO.get()) && !player.getAbilities().instabuild) {
                result.set(true);
            }
        });
        return result.get();
    }
}
