package radon.naruto_universe.ability.jutsu.fire;

import net.minecraft.world.entity.LivingEntity;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.entity.FireballJutsuProjectile;
import radon.naruto_universe.sound.NarutoSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PhoenixSageFire extends Ability {
    @Override
    public NinjaRank getRank() {
        return NinjaRank.CHUNIN;
    }

    @Override
    public NinjaTrait getRelease() {
        return NinjaTrait.FIRE_RELEASE;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        return new AbilityDisplayInfo(this.getId().getPath(), 3.0F, 2.0F);
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.GREAT_FIREBALL.get();
    }

    public ChatFormatting getChatColor() {
        return ChatFormatting.YELLOW;
    }

    @Override
    public float getDamage() {
        return 2.5F;
    }

    @Override
    public float getCost() {
        return 10.0F;
    }

    @Override
    public boolean hasCombo() {
        return true;
    }

    @Override
    public float getMinPower() {
        return 0.1F;
    }

    @Override
    public void runClient(LivingEntity owner) {

    }

    @Override
    public void runServer(LivingEntity owner) {
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            owner.level.playSound(null, owner.blockPosition(), NarutoSounds.SAGE_FIRE.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            float power = this.getPower();
            int count = Math.min(10, Math.round(power / 0.1F));

            for (int i = 0; i < count; i++) {
                cap.delayTickEvent((playerClone) -> {
                    Vec3 look = playerClone.getLookAngle();
                    FireballJutsuProjectile fireball = new FireballJutsuProjectile(playerClone, look.x(), look.y(), look.z(), power, this.getDamage(), 0.5F, 1.0F);
                    playerClone.level.addFreshEntity(fireball);
                    playerClone.level.playSound(null, playerClone.blockPosition(), SoundEvents.FIRECHARGE_USE,
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                }, 20 + (i * 5));
            }
        });
    }
}
