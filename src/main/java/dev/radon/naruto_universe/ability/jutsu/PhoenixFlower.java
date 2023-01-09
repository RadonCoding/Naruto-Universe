package dev.radon.naruto_universe.ability.jutsu;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.capability.NinjaPlayerHandler;
import dev.radon.naruto_universe.capability.NinjaRank;
import dev.radon.naruto_universe.capability.NinjaTrait;
import dev.radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import dev.radon.naruto_universe.entity.FireballEntity;
import dev.radon.naruto_universe.sound.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;

public class PhoenixFlower extends Ability {

    @Override
    public List<NinjaTrait> getRequirements() {
        return Arrays.asList(NinjaTrait.FIRE_RELEASE);
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.CHUNIN;
    }

    @Override
    public long getCombo() {
        return 132;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        AbilityDisplayInfo info = new AbilityDisplayInfo(iconPath, 3.0F, 2.0F);
        return info;
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.GREAT_FIREBALL.get();
    }

    public ChatFormatting getChatColor() {
        return ChatFormatting.YELLOW;
    }

    @Override
    public float getCost() {
        return 15.0F;
    }

    @Override
    public void runClient(LocalPlayer player) {

    }

    @Override
    public void runServer(ServerPlayer player) {
        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            player.level.playSound(null, player.blockPosition(), SoundRegistry.PHOENIX_FLOWER.get(),
                    SoundSource.PLAYERS, 1.0F, cap.getVoicePitch());

            int delay = 2 * 20;

            for (int i = 0; i < 6; i++) {
                cap.delayTickEvent((playerClone) -> {
                    Vec3 look = playerClone.getLookAngle();
                    FireballEntity fireball = new FireballEntity(playerClone, look.x(), look.y(), look.z(),
                            10.0F, 2.5F, 5.0F, 1.0F, 20, 0.1F ,0.5F);

                    playerClone.level.addFreshEntity(fireball);
                    playerClone.level.playSound(null, playerClone.blockPosition(), SoundEvents.FIRECHARGE_USE,
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                }, delay + i * 10);
            }
        });
    }
}
