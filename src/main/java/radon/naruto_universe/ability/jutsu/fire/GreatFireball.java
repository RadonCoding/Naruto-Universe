package radon.naruto_universe.ability.jutsu.fire;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
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
    public NinjaRank getRank() {
        return NinjaRank.CHUNIN;
    }

    @Override
    public NinjaTrait getRelease() {
        return NinjaTrait.FIRE_RELEASE;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return new AbilityDisplayInfo(this.getId().getPath(), 2.0F, 2.0F);
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
    public float getCost(LivingEntity owner) {
        return 15.0F;
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
            owner.level.playSound(null, owner.blockPosition(), NarutoSounds.GREAT_FIREBALL.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            cap.delayTickEvent((ownerClone) -> {
                Vec3 look = ownerClone.getLookAngle();
                FireballJutsuProjectile fireball = new FireballJutsuProjectile(ownerClone, look.x(), look.y(), look.z(), this.getPower(), this.getDamage(), 1.5F, 3.0F);
                ownerClone.level.addFreshEntity(fireball);
                ownerClone.level.playSound(null, ownerClone.blockPosition(), SoundEvents.FIRECHARGE_USE,
                        SoundSource.PLAYERS, 1.0F, 1.0F);
            }, 20, LogicalSide.SERVER);
        });
    }
}
