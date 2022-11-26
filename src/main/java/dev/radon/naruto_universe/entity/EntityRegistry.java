package dev.radon.naruto_universe.entity;

import dev.radon.naruto_universe.NarutoUniverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NarutoUniverse.MOD_ID);

    public static final RegistryObject<EntityType<GreatFireballEntity>> GREAT_FIREBALL = ENTITIES.register("great_fireball", () ->
            EntityType.Builder.<GreatFireballEntity>of(GreatFireballEntity::new, MobCategory.MISC)
                    .sized(3.0F, 3.0F)
                    .build(new ResourceLocation(NarutoUniverse.MOD_ID, "great_fireball")
                            .toString()));
}
