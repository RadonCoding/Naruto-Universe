package radon.naruto_universe.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class NarutoRenderTypes extends RenderType {
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
        RenderStateShard.TextureStateShard shard = new RenderStateShard.TextureStateShard(pLocation, true, false);
        return create("fireball", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256,
                false, false, RenderType.CompositeState.builder()
                        .setLayeringState(POLYGON_OFFSET_LAYERING)
                        .setShaderState(new ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setTextureState(shard)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .setLightmapState(NO_LIGHTMAP)
                        .createCompositeState(false));
    });

    public NarutoRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType eyes(ResourceLocation pLocation) {
        return EYES.apply(pLocation);
    }

    public static RenderType glow(ResourceLocation pLocation) {
        return GLOW.apply(pLocation);
    }
}
