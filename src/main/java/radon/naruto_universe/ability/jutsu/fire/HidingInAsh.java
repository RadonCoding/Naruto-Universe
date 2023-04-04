package radon.naruto_universe.ability.jutsu.fire;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.client.particle.VaporParticle;
import radon.naruto_universe.entity.ParticleSpawnerProjectile;
import radon.naruto_universe.sound.NarutoSounds;

import java.util.List;
import java.util.Random;

public class HidingInAsh extends Ability {
    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.FIRE_RELEASE);
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.CHUNIN;
    }

    @Override
    public NinjaTrait getRelease() {
        return NinjaTrait.FIRE_RELEASE;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        final String iconPath = this.getId().getPath();
        return new AbilityDisplayInfo(iconPath, 4.0F, 2.0F);
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.PHOENIX_SAGE_FIRE.get();
    }

    public ChatFormatting getChatColor() {
        return ChatFormatting.YELLOW;
    }

    @Override
    public float getDamage() {
        return 1.0F;
    }

    @Override
    public float getCost() {
        return 10.0F;
    }

    @Override
    public boolean hasCombo() {
        return true;
    }

    @Override
    public void runClient(LivingEntity owner) {}

    @Override
    public void runServer(LivingEntity owner) {
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            owner.level.playSound(null, owner.blockPosition(), NarutoSounds.HIDING_IN_ASH.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            cap.delayTickEvent((playerClone) -> {
                final Random rand = new Random();
                final int lifetime = rand.nextInt(60, 120);

                final Vec3 look = playerClone.getLookAngle();
                final ParticleOptions particle = new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.SMOKE_COLOR, 10.0F, 1.0F, false, lifetime);
                owner.level.addFreshEntity(new ParticleSpawnerProjectile(owner, look.x(), look.y(), look.z(), this.getPower(), this.getDamage(), NinjaTrait.FIRE_RELEASE, particle, lifetime, 7.5F, 10.0F));
            }, 20);
        });
    }
}
