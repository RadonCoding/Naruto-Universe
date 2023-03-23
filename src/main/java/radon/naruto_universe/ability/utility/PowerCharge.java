package radon.naruto_universe.ability.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.NinjaPlayer;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.client.particle.VaporParticle;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.SyncNinjaPlayerS2CPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import radon.naruto_universe.util.HelperMethods;

import java.util.Random;

public class PowerCharge extends Ability implements Ability.Channeled {

    @Override
    public NinjaRank getRank() {
        return NinjaRank.ACADEMY_STUDENT;
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.CHANNELED;
    }

    @Override
    public long getCombo() {
        return 1;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        return new AbilityDisplayInfo(iconPath, 2.0F, 0.0F);
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.CHAKRA_CONTROL.get();
    }

    public ChatFormatting getChatColor() {
        return ChatFormatting.AQUA;
    }

    @Override
    public float getCost() {
        return 0.0F;
    }

    private void chargePower(Player player) {
        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            float amount = player.isShiftKeyDown() ? NinjaPlayer.POWER_CHARGE_AMOUNT : Math.max(NinjaPlayer.POWER_CHARGE_AMOUNT,
                    (cap.getRank().ordinal() * 10.0F) * NinjaPlayer.POWER_CHARGE_AMOUNT);
            cap.addPower(amount);
            cap.setPowerResetTimer(0);
        });
    }

    @Override
    public void runClient(LocalPlayer player) {
        this.chargePower(player);

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap ->
                Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("chat_text.power", HelperMethods.round(cap.getPower(), 1)), false));
    }

    @Override
    public void runServer(ServerPlayer player) {
        Random random = new Random();

        ServerLevel serverLevel = player.getLevel();
        serverLevel.sendParticles(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.CHAKRA_COLOR, 1.0F, true, 3),
                player.getX() + random.nextGaussian() * 0.1D, player.getY(), player.getZ() + random.nextGaussian() * 0.1D,
                0, 0.0D, 0.56F, 0.0D, 1.75D);

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1, 10, false, false, false));

        this.chargePower(player);
    }
}
