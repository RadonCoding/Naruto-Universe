package radon.naruto_universe.ability.jutsu.fire_release;

import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.entity.FireballJutsuEntity;
import radon.naruto_universe.sound.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PhoenixSageFire extends Ability {
    private static final int FIREBALL_DELAY = 20;

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
        return 132;
    }

    @Override
    public NinjaTrait getRelease() {
        return NinjaTrait.FIRE_RELEASE;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        String iconPath = this.getId().getPath();
        return new AbilityDisplayInfo(iconPath, 3.0F, 2.0F);
    }

    @Override
    public Ability getParent() {
        return AbilityRegistry.GREAT_FIREBALL.get();
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
        return 15.0F;
    }

    @Override
    public void runClient(LocalPlayer player) {

    }

    @Override
    public void runServer(ServerPlayer player) {
        player.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            player.level.playSound(null, player.blockPosition(), SoundRegistry.SAGE_FIRE.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            float power = this.getPower();
            int count = Math.min(10, Math.round(power / 0.1F));

            for (int i = 0; i < count; i++) {
                cap.delayTickEvent((playerClone) -> {
                    Vec3 look = playerClone.getLookAngle();
                    FireballJutsuEntity fireball = new FireballJutsuEntity(playerClone, look.x(), look.y(), look.z(), 0.5F, power, 0.5F, 1.0F);
                    playerClone.level.addFreshEntity(fireball);
                    playerClone.level.playSound(null, playerClone.blockPosition(), SoundEvents.FIRECHARGE_USE,
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                }, FIREBALL_DELAY + (i * 5));
            }
        });
    }
}
