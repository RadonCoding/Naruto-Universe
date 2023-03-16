package radon.naruto_universe.ability.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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
        return AbilityRegistry.WATER_WALKING.get();
    }

    public ChatFormatting getChatColor() {
        return ChatFormatting.AQUA;
    }

    @Override
    public float getCost() {
        return 0.0F;
    }

    @Override
    public void runClient(LocalPlayer player) {
        player.level.addParticle(new VaporParticle.VaporParticleOptions(VaporParticle.VaporParticleOptions.CHAKRA_COLOR, 0.5F),
                player.level.random.nextGaussian() * 0.1D + player.getX(),
                player.getY() + 0.56F, player.level.random.nextGaussian() * 0.1D + player.getZ(),
                0.0D, 0.75D, 0.0D);

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("chat_text.power", HelperMethods.round(cap.getPower(), 1)), false);
        });
    }

    @Override
    public void runServer(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1, 10, false, false, false));

        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            float amount = 0.01F;

            if (cap.getPower() + amount <= NinjaPlayer.MAX_ABILITY_POWER) {
                cap.addPower(amount);
            }
            cap.startPowerReset();
            PacketHandler.sendToClient(new SyncNinjaPlayerS2CPacket(cap.serializeNBT()), player);
        });
    }
}
