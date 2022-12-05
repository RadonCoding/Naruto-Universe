package dev.radon.naruto_universe.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class VaporParticle extends TextureSheetParticle {
    private static final RandomSource RANDOM = RandomSource.create();
    private final SpriteSet sprites;

    protected VaporParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);
        this.setLifetime(2);
        this.setParticleSpeed(pXSpeed, RANDOM.nextDouble() * pYSpeed, pZSpeed);
        this.quadSize = 0.75F * (1.0F - RANDOM.nextFloat() * 0.5F);
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
        this.alpha = (-(0.5F / (float) this.lifetime) * this.age + 1.0F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRegistry.ModRenderTypes.VAPOR;
    }

    public static class ChakraProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public ChakraProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            VaporParticle particle = new VaporParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
            Vec3 color = Vec3.fromRGB24(240116);
            particle.setColor((float) color.x(), (float) color.y(), (float) color.z());
            particle.setAlpha(0.1F);
            return particle;
        }
    }
}
