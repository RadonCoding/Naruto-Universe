package radon.naruto_universe.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.naruto_universe.NarutoUniverse;

public class NarutoEntities {
    public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NarutoUniverse.MOD_ID);

    public static RegistryObject<EntityType<FireballProjectile>> FIREBALL = ENTITIES.register("fireball", () ->
            EntityType.Builder.<FireballProjectile>of(FireballProjectile::new, MobCategory.MISC)
                    .build(new ResourceLocation(NarutoUniverse.MOD_ID, "fireball")
                            .toString()));

    public static RegistryObject<EntityType<ParticleSpawnerProjectile>> PARTICLE_SPAWNER = ENTITIES.register("particle_spawner", () ->
            EntityType.Builder.<ParticleSpawnerProjectile>of(ParticleSpawnerProjectile::new, MobCategory.MISC)
                    .build(new ResourceLocation(NarutoUniverse.MOD_ID, "particle_spawner")
                            .toString()));

    public static RegistryObject<EntityType<HidingInAshEntity>> HIDING_IN_ASH = ENTITIES.register("hiding_in_ash", () ->
            EntityType.Builder.<HidingInAshEntity>of(HidingInAshEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(NarutoUniverse.MOD_ID, "hiding_in_ash")
                            .toString()));

    public static RegistryObject<EntityType<ThrownKunaiEntity>> THROWN_KUNAI = ENTITIES.register("thrown_kunai", () ->
            EntityType.Builder.<ThrownKunaiEntity>of(ThrownKunaiEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(NarutoUniverse.MOD_ID, "thrown_kunai")
                            .toString()));

    public static RegistryObject<EntityType<SusanooEntity>> SUSANOO = ENTITIES.register("susanoo", () ->
            EntityType.Builder.<SusanooEntity>of(SusanooEntity::new, MobCategory.MISC)
                    .build(new ResourceLocation(NarutoUniverse.MOD_ID, "susanoo").toString()));

    public static void createAttributes(EntityAttributeCreationEvent event) {
        event.put(SUSANOO.get(), SusanooEntity.createAttributes().build());
    }
}
