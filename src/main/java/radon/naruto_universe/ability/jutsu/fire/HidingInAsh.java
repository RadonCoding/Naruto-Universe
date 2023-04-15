package radon.naruto_universe.ability.jutsu.fire;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.client.particle.VaporParticle;
import radon.naruto_universe.entity.HidingInAshEntity;
import radon.naruto_universe.sound.NarutoSounds;

import java.util.Random;

public class HidingInAsh extends Ability {
    @Override
    public NinjaRank getRank() {
        return NinjaRank.CHUNIN;
    }

    @Override
    public NinjaTrait getRelease() {
        return NinjaTrait.FIRE_RELEASE;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return new AbilityDisplayInfo(this.getId().getPath(), 4.0F, 2.0F);
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
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public boolean hasCombo() {
        return true;
    }

    @Override
    public float getMinPower() {
        return 0.1F;
    }

    @Override
    public void runServer(LivingEntity owner) {
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            owner.level.playSound(null, owner.blockPosition(), NarutoSounds.HIDING_IN_ASH.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            cap.delayTickEvent((ownerClone) -> {
                Random rand = new Random();
                int duration = Math.max(10, Math.round(this.getPower())) * rand.nextInt(5, 10);
                int lifetime = 10;
                float scalar = Math.max(5.0F, rand.nextFloat() * 5.0F);

                ParticleOptions particle = new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.SMOKE_COLOR,
                        scalar, 0.75F, false, lifetime);
                owner.level.addFreshEntity(new HidingInAshEntity(owner, this.getPower(), this.getDamage(), particle, duration, 10.0F, 1.0F));
            }, 20, LogicalSide.SERVER);
        });
    }
}
