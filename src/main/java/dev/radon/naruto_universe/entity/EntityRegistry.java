package dev.radon.naruto_universe.entity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.radon.naruto_universe.NarutoUniverse;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NarutoUniverse.MOD_ID);

    public static final RegistryObject<EntityType<GreatFireballEntity>> GREAT_FIREBALL = ENTITIES.register("great_fireball", () ->
            EntityType.Builder.<GreatFireballEntity>of(GreatFireballEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(NarutoUniverse.MOD_ID, "great_fireball")
                            .toString()));

    public static class ModRenderType extends RenderType {
        private static final Function<ResourceLocation, RenderType> EYES_BACKGROUND = Util.memoize((pLocation) -> {
            RenderStateShard.TextureStateShard shard = new RenderStateShard.TextureStateShard(pLocation, false, false);
            return create("eyes_background", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
                    false, true, RenderType.CompositeState.builder()
                            .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                            .setTextureState(shard)
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setCullState(NO_CULL)
                            .setLightmapState(LIGHTMAP)
                            .setOverlayState(OVERLAY)
                            .createCompositeState(false));
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

        public ModRenderType(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
            super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
        }

        public static RenderType eyes(ResourceLocation pLocation) {
            return EYES.apply(pLocation);
        }

        public static RenderType eyesBackground(ResourceLocation pLocation) {
            return EYES_BACKGROUND.apply(pLocation);
        }
    }
}
