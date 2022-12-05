package dev.radon.naruto_universe.ability.jutsu;

import dev.radon.naruto_universe.ability.Ability;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.capability.NinjaPlayerHandler;
import dev.radon.naruto_universe.capability.NinjaTrait;
import dev.radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import dev.radon.naruto_universe.entity.GreatFireballEntity;
import dev.radon.naruto_universe.sound.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class GreatFireball extends Ability {
    public GreatFireball() {
        this.requirements.add(NinjaTrait.FIRE_RELEASE);
    }

    @Override
    public long getCombo() {
        return 123;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        AbilityDisplayInfo info = new AbilityDisplayInfo(iconPath, 2.0F, 2.0F);
        return info;
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.WATER_WALKING.get();
    }

    public ChatFormatting getChatColor() {
        return ChatFormatting.YELLOW;
    }

    @Override
    public float getCost() {
        return 25.0F;
    }

    @Override
    public void runClient(LocalPlayer player) {

    }

    @Override
    public void runServer(ServerPlayer player) {
        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            player.level.playSound(null, player.blockPosition(), SoundRegistry.GREAT_FIREBALL.get(),
                    SoundSource.PLAYERS, 1.0F, cap.getVoicePitch());

            cap.delayTickEvent((playerClone) -> {
                Vec3 look = playerClone.getLookAngle();
                GreatFireballEntity fireball = new GreatFireballEntity(playerClone, look.x(), look.y(), look.z(), 5.0F, 10.0F);
                playerClone.level.addFreshEntity(fireball);
            }, 40);
        });
    }
}
