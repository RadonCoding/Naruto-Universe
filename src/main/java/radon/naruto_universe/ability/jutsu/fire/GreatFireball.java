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
        return NarutoAbilities.POWER_CHARGE.get();
    }

    public ChatFormatting getChatColor() {
        return ChatFormatting.YELLOW;
    }

    @Override
    public float getDamage() {
        return 10.0F;
    }

    @Override
    public float getCost() {
        return 15.0F;
    }

    @Override
    public boolean hasCombo() {
        return true;
    }

    @Override
    public void runClient(LivingEntity owner) {

    }

    @Override
    public void runServer(LivingEntity owner) {
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            owner.level.playSound(null, owner.blockPosition(), NarutoSounds.GREAT_FIREBALL.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            cap.delayTickEvent((playerClone) -> {
                final Vec3 look = playerClone.getLookAngle();
                final FireballJutsuProjectile fireball = new FireballJutsuProjectile(playerClone, look.x(), look.y(), look.z(), this.getPower(), this.getDamage(), 1.5F, 3.0F);
                playerClone.level.addFreshEntity(fireball);
                playerClone.level.playSound(null, playerClone.blockPosition(), SoundEvents.FIRECHARGE_USE,
                        SoundSource.PLAYERS, 1.0F, 1.0F);
            }, 20);
        });
    }
}
