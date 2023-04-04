package radon.naruto_universe.ability.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaPlayer;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.client.particle.VaporParticle;
import radon.naruto_universe.config.ConfigHolder;
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
    public boolean isUnlocked(Player player) {
        return true;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        return new AbilityDisplayInfo(iconPath, 0.0F, 0.0F);
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
            float amount = owner.isShiftKeyDown() ? ConfigHolder.SERVER.powerChargeAmount.get() : (cap.getRank().ordinal() * 10.0F) *
                    ConfigHolder.SERVER.powerChargeAmount.get();
            cap.addPower(amount);
            cap.setPowerResetTimer(0);
        });
    }

    @Override
    public void runClient(LivingEntity owner) {
        this.chargePower(owner);

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap ->
                Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("chat_text.power", HelperMethods.round(cap.getPower(), 1)), false));
    }

    @Override
    public void runServer(LivingEntity owner) {
        Random random = new Random();

        ServerLevel serverLevel = (ServerLevel) owner.getLevel();
        serverLevel.sendParticles(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.CHAKRA_COLOR, 1.5F, 1.0F, true, 3),
                owner.getX() + random.nextGaussian() * 0.1D, owner.getY(), owner.getZ() + random.nextGaussian() * 0.1D,
                0, 0.0D, 0.56F, 0.0D, 1.75D);

        owner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1, 10, false, false, false));

        this.chargePower(owner);
    }
}
