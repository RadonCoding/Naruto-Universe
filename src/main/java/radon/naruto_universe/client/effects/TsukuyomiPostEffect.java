package radon.naruto_universe.client.effects;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.client.NarutoPostEffect;
import radon.naruto_universe.client.genjutsu.TsukuyomiHandler;

public class TsukuyomiPostEffect extends NarutoPostEffect {
    private static final ResourceLocation TSUKUYOMI = new ResourceLocation(NarutoUniverse.MOD_ID, "shaders/post/tsukuyomi.json");

    @Override
    protected ResourceLocation getEffect() {
        return TSUKUYOMI;
    }

    @Override
    public boolean shouldRender(LocalPlayer player) {
        return TsukuyomiHandler.isActive();
    }
}