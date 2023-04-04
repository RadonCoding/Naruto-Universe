package radon.naruto_universe.entity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.naruto_universe.NarutoUniverse;

import java.util.function.Function;

public class NarutoEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NarutoUniverse.MOD_ID);

    public static final RegistryObject<EntityType<FireballJutsuProjectile>> FIREBALL_JUTSU = ENTITIES.register("fireball_jutsu", () ->
            EntityType.Builder.<FireballJutsuProjectile>of(FireballJutsuProjectile::new, MobCategory.MISC)
                    .build(new ResourceLocation(NarutoUniverse.MOD_ID, "fireball_jutsu")
                            .toString()));

    public static final RegistryObject<EntityType<ParticleSpawnerProjectile>> PARTICLE_SPAWNER = ENTITIES.register("particle_spawner", () ->
            EntityType.Builder.<ParticleSpawnerProjectile>of(ParticleSpawnerProjectile::new, MobCategory.MISC)
                    .build(new ResourceLocation(NarutoUniverse.MOD_ID, "particle_spawner")
                            .toString()));

    public static final RegistryObject<EntityType<ThrownKunaiEntity>> THROWN_KUNAI = ENTITIES.register("thrown_kunai", () ->
            EntityType.Builder.<ThrownKunaiEntity>of(ThrownKunaiEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(NarutoUniverse.MOD_ID, "thrown_kunai")
                            .toString()));

    public static class ModRenderType extends RenderType {
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

        public ModRenderType(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
            super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
        }

        public static RenderType eyes(ResourceLocation pLocation) {
            return EYES.apply(pLocation);
        }

        public static RenderType glow(ResourceLocation pLocation) {
            return GLOW.apply(pLocation);
        }
    }
}
