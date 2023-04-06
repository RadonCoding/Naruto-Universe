package radon.naruto_universe.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.naruto_universe.NarutoUniverse;

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
}
