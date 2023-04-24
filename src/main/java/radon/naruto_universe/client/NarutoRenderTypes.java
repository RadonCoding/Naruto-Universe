package radon.naruto_universe.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class NarutoRenderTypes extends RenderType {
    private static final TransparencyStateShard GLOWING_TRANSPARENCY = new TransparencyStateShard("glowing_transparency", () -> {
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
    });
    
    private static final Function<ResourceLocation, RenderType> EYES = Util.memoize((pLocation) -> {
        RenderStateShard.TextureStateShard shard = new RenderStateShard.TextureStateShard(pLocation, false, false);
        return create("eyes", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
                false, true, RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_EYES_SHADER)
                        .setTextureState(shard)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false));
    });

    private static final Function<ResourceLocation, RenderType> GLOW = Util.memoize((pLocation) -> {
        TextureStateShard shard = new TextureStateShard(pLocation, false, false);
        return create("susanoo", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
                false, true, CompositeState.builder()
                        .setLayeringState(POLYGON_OFFSET_LAYERING)
                        .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                        .setTextureState(shard)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(false));
    });

    public NarutoRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static @NotNull RenderType eyes(@NotNull ResourceLocation pLocation) {
        return EYES.apply(pLocation);
    }

    public static RenderType glow(ResourceLocation pLocation) {
        return GLOW.apply(pLocation);
    }
}
