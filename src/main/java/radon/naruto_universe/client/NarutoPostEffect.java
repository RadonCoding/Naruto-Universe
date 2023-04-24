package radon.naruto_universe.client;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.mixin.client.PostChainAccessor;

import java.io.IOException;
import java.util.List;

public abstract class NarutoPostEffect implements ResourceManagerReloadListener {
    private Object postChain;

    protected abstract ResourceLocation getEffect();

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager pResourceManager) {
        Minecraft mc = Minecraft.getInstance();

        if (this.postChain != null) {
            ((PostChain) this.postChain).close();
        }

        try {
            if (mc.isSameThread()) {
                this.postChain = new PostChain(mc.getTextureManager(), pResourceManager, mc.getMainRenderTarget(), this.getEffect());
                ((PostChain) this.postChain).resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            }
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public void resize(int width, int heigtht) {
        if (postChain != null) {
            ((PostChain) postChain).resize(width, heigtht);
        }
    }

    public abstract boolean shouldRender(LocalPlayer player);
    protected void applyUniforms(PostPass pass) {}

    public void render(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        if (this.postChain == null) {
            this.onResourceManagerReload(mc.getResourceManager());
        }

        for (PostPass pass : ((PostChainAccessor) this.postChain).getPasses()) {
            this.applyUniforms(pass);
        }
        ((PostChain) this.postChain).process(partialTicks);
    }
}
