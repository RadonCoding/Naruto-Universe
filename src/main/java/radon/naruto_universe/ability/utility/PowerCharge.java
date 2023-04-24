package radon.naruto_universe.ability.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.capability.ninja.NinjaPlayer;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.client.particle.VaporParticle;
import radon.naruto_universe.effect.NarutoEffects;
import radon.naruto_universe.sound.NarutoSounds;
import radon.naruto_universe.util.HelperMethods;

import java.util.Random;

public class PowerCharge extends Ability implements Ability.IChanneled {

    @Override
    public NinjaRank getRank() {
        return NinjaRank.ACADEMY_STUDENT;
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.CHANNELED;
    }


    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return new AbilityDisplayInfo(this.getId().getPath(), 0.0F, 0.0F);
    }

    @Override
    public Ability getParent() {
        return null;
    }

    public ChatFormatting getChatColor() {
        return ChatFormatting.AQUA;
    }

    @Override
    public boolean hasCombo() {
        return true;
    }

    private void chargePower(LivingEntity owner) {
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            float amount = owner.isShiftKeyDown() ? NinjaPlayer.POWER_CHARGE_AMOUNT :
                    Math.max(NinjaPlayer.POWER_CHARGE_AMOUNT, (cap.getRank().ordinal() * 10.0F) * NinjaPlayer.POWER_CHARGE_AMOUNT);
            cap.addPower(amount);
            cap.setPowerResetTimer(0);
        });

        owner.addEffect(new MobEffectInstance(NarutoEffects.STUN.get(), 1, 0, false, false, false));
    }

    @Override
    public void runClient(LivingEntity owner) {
        this.chargePower(owner);

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap ->
                Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("chat_text.power", HelperMethods.round(cap.getPower(), 1)), false));

        if (owner.level.getGameTime() % 10 == 0) {
            owner.level.playLocalSound(owner.blockPosition(), NarutoSounds.POWER_CHARGE.get(),
                    SoundSource.MASTER, 3.0F, 1.0F, false);
        }
    }

    @Override
    public void runServer(LivingEntity owner) {
        this.chargePower(owner);

        Random rand = new Random();
        ServerLevel level = (ServerLevel) owner.getLevel();

        if (level.getGameTime() % 10 == 0) {
            if (owner instanceof Player player) {
                level.playSound(player, owner.blockPosition(), NarutoSounds.POWER_CHARGE.get(),
                        SoundSource.MASTER, 3.0F, 1.0F);
            } else {
                level.playSound(null, owner.blockPosition(), NarutoSounds.POWER_CHARGE.get(),
                        SoundSource.MASTER, 3.0F, 1.0F);
            }
        }

        for (int i = 0; i < 8; i++) {
            level.sendParticles(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.CHAKRA_COLOR, 1.5F, 0.5F, false, 1),
                    owner.getX() + (rand.nextGaussian() * 0.1D), owner.getY() + rand.nextDouble(owner.getBbHeight()), owner.getZ() + (rand.nextGaussian() * 0.1D),
                    0, 0.0D, rand.nextDouble(), 0.0D, 2.5D);
        }
    }
}
