package radon.naruto_universe.ability.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.client.particle.VaporParticle;
import radon.naruto_universe.sound.SoundRegistry;

import java.util.List;
import java.util.Random;

public class Sharingan extends Ability implements Ability.Toggled {
    @Override
    public boolean isDojutsu() {
        return true;
    }

    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.UNLOCKED_SHARINGAN);
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.ACADEMY_STUDENT;
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        return new AbilityDisplayInfo(iconPath, 6.0F, 0.0F);
    }

    @Override
    public ChatFormatting getChatColor() {
        return ChatFormatting.DARK_RED;
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.POWER_CHARGE.get();
    }

    @Override
    public float getCost() {
        return 0.025F;
    }

    @Override
    public void runClient(LocalPlayer player) {
        double radius = 50.0D;

        for (Entity entity : player.level.getEntities(null, player.getBoundingBox().inflate(radius))) {
            if (entity == player) {
                continue;
            }

            if (!entity.getCapability(NinjaPlayerHandler.INSTANCE).isPresent()) {
                continue;
            }

            Random random = new Random();

            entity.level.addParticle(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.CHAKRA_COLOR, 0.5F, true, 3),
                    random.nextGaussian() * 0.1D + entity.getX(),
                    entity.getY() + 0.56F, random.nextGaussian() * 0.1D + entity.getZ(),
                    0.0D, 0.75D, 0.0D);
        }
    }

    @Override
    public void runServer(ServerPlayer player) {

    }

    @Override
    public SoundEvent getActivationSound() {
        return SoundRegistry.SHARINGAN_ACTIVATE.get();
    }

    @Override
    public SoundEvent getDectivationSound() {
        return SoundRegistry.SHARINGAN_DEACTIVATE.get();
    }
}
