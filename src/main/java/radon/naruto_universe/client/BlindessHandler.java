package radon.naruto_universe.client;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.mixin.client.PostChainAccessor;

import java.io.IOException;

public class BlindessHandler implements ResourceManagerReloadListener {
    public static BlindessHandler INSTANCE = new BlindessHandler();

    private static final ResourceLocation BLUR = new ResourceLocation("shaders/post/blobs2.json");

    private Object postChain;

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager pResourceManager) {
        Minecraft mc = Minecraft.getInstance();

        if (this.postChain != null) {
            ((PostChain) this.postChain).close();
        }

        try {
            if (mc.isSameThread()) {
                this.postChain = new PostChain(mc.getTextureManager(), pResourceManager, mc.getMainRenderTarget(), BLUR);
                ((PostChain) this.postChain).resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            }
        } catch (JsonSyntaxException | IOException ignored) {}
    }

    public void resize(int width, int heigtht) {
        if (postChain != null) {
            ((PostChain) postChain).resize(width, heigtht);
        }
    }

    public void render(float partialTicks, float intensity) {
        Minecraft mc = Minecraft.getInstance();

        if (this.postChain == null) {
            this.onResourceManagerReload(mc.getResourceManager());
        }

        for (PostPass pass : ((PostChainAccessor) this.postChain).getPasses()) {
            Uniform uniform = pass.getEffect().getUniform("Radius");

            if (uniform != null) {
                uniform.set((float) Math.min(25.0F, Math.floor(intensity)));
            }
        }
        ((PostChain) this.postChain).process(partialTicks);
    }
}
