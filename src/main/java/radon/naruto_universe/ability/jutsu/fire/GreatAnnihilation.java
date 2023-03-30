package radon.naruto_universe.ability.jutsu.fire;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.client.particle.ParticleRegistry;
import radon.naruto_universe.entity.ParticleSpawnerProjectile;
import radon.naruto_universe.sound.SoundRegistry;

import java.util.List;
import java.util.Random;

public class GreatAnnihilation extends Ability {
    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.FIRE_RELEASE);
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.CHUNIN;
    }

    @Override
    public long getCombo() {
        return 323;
    }

    @Override
    public NinjaTrait getRelease() {
        return NinjaTrait.FIRE_RELEASE;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        return new AbilityDisplayInfo(iconPath, 6.0F, 2.0F);
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.GREAT_FLAME.get();
    }

    @Override
    public float getMinPower() {
        return 0.1F;
    }

    public ChatFormatting getChatColor() {
        return ChatFormatting.YELLOW;
    }

    @Override
    public float getCost() {
        return 35.0F;
    }

    @Override
    public float getDamage() {
        return 3.0F;
    }

    @Override
    public void runClient(LivingEntity owner) {

    }

    @Override
    public void runServer(LivingEntity owner) {
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            owner.level.playSound(null, owner.blockPosition(), SoundRegistry.GREAT_ANNIHILATION.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            cap.delayTickEvent((playerClone) -> {
                final Random rand = new Random();
                final int lifetime = rand.nextInt(60, 120);

                final Vec3 look = playerClone.getLookAngle();
                owner.level.addFreshEntity(new ParticleSpawnerProjectile(owner, look.x(), look.y(), look.z(), this.getPower(), this.getDamage(), NinjaTrait.FIRE_RELEASE, ParticleRegistry.FLAME.get(), lifetime, 2.5F));
            }, 20);
        });
    }
}