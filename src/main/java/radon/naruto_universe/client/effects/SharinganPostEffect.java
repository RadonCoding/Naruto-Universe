package radon.naruto_universe.client.effects;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.client.NarutoPostEffect;

import java.util.concurrent.atomic.AtomicBoolean;

public class SharinganPostEffect extends NarutoPostEffect {
    private static final ResourceLocation SHARINGAN = new ResourceLocation(NarutoUniverse.MOD_ID, "shaders/post/sharingan.json");

    @Override
    protected ResourceLocation getEffect() {
        return SHARINGAN;
    }

    @Override
    public boolean shouldRender(LocalPlayer player) {
        AtomicBoolean result = new AtomicBoolean(false);

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            if (cap.isChannelingAbility(NarutoAbilities.COPY.get())) {
                result.set(true);
            }
        });
        return result.get();
    }
}
