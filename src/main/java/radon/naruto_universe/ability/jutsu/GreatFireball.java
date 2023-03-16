package radon.naruto_universe.ability.jutsu;

import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.entity.FireballEntity;
import radon.naruto_universe.sound.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;

public class GreatFireball extends Ability {
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
        return 123;
    }

    @Override
    public NinjaTrait getRelease() {
        return NinjaTrait.FIRE_RELEASE;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        return new AbilityDisplayInfo(iconPath, 2.0F, 2.0F);
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.WATER_WALKING.get();
    }

    @Override
    public float getMinPower() {
        return 0.1F;
    }

    @Override
    public float getMaxPower() {
        return 10.0F;
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
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            cap.delayTickEvent((playerClone) -> {
                Vec3 look = playerClone.getLookAngle();
                FireballEntity fireball = new FireballEntity(playerClone, look.x(), look.y(), look.z(), 0.5F, this.getPower(cap), 2.0F);
                playerClone.level.addFreshEntity(fireball);
                playerClone.level.playSound(null, playerClone.blockPosition(), SoundEvents.FIRECHARGE_USE,
                        SoundSource.PLAYERS, 1.0F, 1.0F);
            }, 20);
        });
    }
}
