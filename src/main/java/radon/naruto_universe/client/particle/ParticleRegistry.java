package radon.naruto_universe.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.SimpleParticleType;
import radon.naruto_universe.NarutoUniverse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.naruto_universe.client.event.PlayerModelEvent;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES,
            NarutoUniverse.MOD_ID);

    public static final RegistryObject<ParticleType> VAPOR = PARTICLES.register("vapor", () ->
            new ParticleType<>(false, VaporParticle.VaporParticleOptions.DESERIALIZER) {
                @Override
                public Codec<VaporParticle.VaporParticleOptions> codec() {
                    return null;
                }
            });
    public static final RegistryObject<SimpleParticleType> FLAME = PARTICLES.register("flame", () ->
            new SimpleParticleType(false));

    public static class ModRenderTypes {
        public static final ParticleRenderType GLOW = new ParticleRenderType() {
            @Override
            public void begin(BufferBuilder buffer, TextureManager manager) {
                Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.enableCull();
                RenderSystem.enableDepthTest();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            }

            @Override
            public void end(Tesselator tesselator) {
                tesselator.end();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableDepthTest();
                RenderSystem.disableCull();
                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
                Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
            }
        };

        public static final ParticleRenderType TRANSLUCENT = new ParticleRenderType() {
            @Override
            public void begin(BufferBuilder buffer, TextureManager manager) {
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.enableCull();
                RenderSystem.enableDepthTest();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                        GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE);
                RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            }

            @Override
            public void end(Tesselator tesselator) {
                tesselator.end();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableDepthTest();
                RenderSystem.disableCull();
                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
            }
        };
    }
}
