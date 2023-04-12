package radon.naruto_universe.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.naruto_universe.NarutoUniverse;

public class NarutoParticles {
    public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES,
            NarutoUniverse.MOD_ID);

    public static RegistryObject<ParticleType<VaporParticle.VaporParticleOptions>> VAPOR = PARTICLES.register("vapor", () ->
            new ParticleType<>(false, VaporParticle.VaporParticleOptions.DESERIALIZER) {
                @Override
                public Codec<VaporParticle.VaporParticleOptions> codec() {
                    return null;
                }
            });
    public static RegistryObject<SimpleParticleType> FLAME = PARTICLES.register("flame", () ->
            new SimpleParticleType(false));
    public static RegistryObject<SimpleParticleType> EMPTY = PARTICLES.register("empty", () ->
            new SimpleParticleType(false));
}
