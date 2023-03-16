package radon.naruto_universe.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Locale;

public class VaporParticle<T extends VaporParticle.VaporParticleOptions> extends TextureSheetParticle {
    private static final RandomSource RANDOM = RandomSource.create();
    private final SpriteSet sprites;

    protected VaporParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, T options, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);
        this.setLifetime(3);
        this.setParticleSpeed(pXSpeed, RANDOM.nextFloat() * pYSpeed, pZSpeed);

        Vector3f color = options.color();
        this.setColor(color.x(), color.y(), color.z());

        this.quadSize = 1.0F - (options.scalar() * RANDOM.nextFloat());
        this.sprites = pSprites;
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.fadeOut();
        this.setSpriteFromAge(this.sprites);
    }

    private void fadeOut() {
        this.alpha = 1.0F - Mth.clamp(((float) this.age) / (float)this.lifetime, 0.0F, 0.5F);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRegistry.ModRenderTypes.GLOW;
    }

    public record VaporParticleOptions(Vector3f color, float scalar) implements ParticleOptions {
        public static final Vector3f CHAKRA_COLOR = Vec3.fromRGB24(240116).toVector3f();
        public static final Vector3f FIRE_COLOR = Vec3.fromRGB24(16727040).toVector3f();

        public static final Deserializer<VaporParticleOptions> DESERIALIZER = new Deserializer<>() {
            public @NotNull VaporParticleOptions fromCommand(ParticleType<VaporParticleOptions> type, StringReader reader) throws CommandSyntaxException {
                Vector3f color = VaporParticleOptions.readColorVector3f(reader);
                reader.expect(' ');
                float scalar = reader.readFloat();
                return new VaporParticleOptions(color, scalar);
            }

            public @NotNull VaporParticleOptions fromNetwork(ParticleType<VaporParticleOptions> type, FriendlyByteBuf buf) {
                return new VaporParticleOptions(VaporParticleOptions.readColorFromNetwork(buf), buf.readFloat());
            }
        };

        @Override
        public @NotNull ParticleType<?> getType() {
            return ParticleRegistry.VAPOR.get();
        }

        public static Vector3f readColorVector3f(StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float f0 = reader.readFloat();
            reader.expect(' ');
            float f1 = reader.readFloat();
            reader.expect(' ');
            float f2 = reader.readFloat();
            return new Vector3f(f0, f1, f2);
        }

        public static Vector3f readColorFromNetwork(FriendlyByteBuf buf) {
            return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buf) {
            buf.writeFloat(this.color.x());
            buf.writeFloat(this.color.y());
            buf.writeFloat(this.color.z());
            buf.writeFloat(this.scalar);
        }

        @Override
        public @NotNull String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.color.x(), this.color.y(), this.color.z(), this.scalar);
        }
    }

    public static class Provider implements ParticleProvider<VaporParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(@NotNull VaporParticleOptions pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            return new VaporParticle<>(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, this.sprites);
        }
    }
}
