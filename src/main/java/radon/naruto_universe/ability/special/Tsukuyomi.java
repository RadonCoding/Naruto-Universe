package radon.naruto_universe.ability.special;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.NinjaRank;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.capability.ninja.ToggledEyes;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.effect.NarutoEffects;
import radon.naruto_universe.network.PacketHandler;
import radon.naruto_universe.network.packet.GenjutsuS2CPacket;
import radon.naruto_universe.sound.NarutoSounds;
import radon.naruto_universe.util.HelperMethods;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Tsukuyomi extends Ability {
    private static final double RAYCAST_RANGE = 30.0D;

    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.MANGEKYO);
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.GENIN;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return new AbilityDisplayInfo(this.getId().getPath(), 12.0F, 0.0F);
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.SHARINGAN.get();
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap ->
                result.set(((ISpecial) NarutoAbilities.MANGEKYO.get()).getSpecialAbilities(owner).contains(this) &&
                        cap.hasToggledAbility(NarutoAbilities.MANGEKYO.get())));
        return result.get();
    }

    @Override
    public boolean isUnlockable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldLog(LivingEntity owner) {
        return false;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public SoundEvent getActivationSound() {
        return NarutoSounds.GENJUTSU.get();
    }

    @Override
    public boolean canTrigger(LivingEntity owner) {
        return this.getTarget(owner) != null;
    }

    @Override
    public int getCooldown() {
        return 60 * 20;
    }

    private LivingEntity getTarget(LivingEntity owner) {
        EntityHitResult look = HelperMethods.getLivingEntityLookAt(owner, RAYCAST_RANGE, 1.0D);

        if (look != null) {
            if (look.getEntity() instanceof LivingEntity target) {
                if (target instanceof Player) {
                    EntityHitResult hit = HelperMethods.getEntityEyesConnect(owner, RAYCAST_RANGE);

                    if (hit != null && (hit.getEntity() instanceof Player player)) {
                        return player;
                    }
                }
                return target;
            }
        }
        return null;
    }

    @Override
    public void runServer(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target != null) {
            int duration = Math.max(30, Math.round(this.getExperience() * 0.75F)) * 20;
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, 5));
            target.addEffect(new MobEffectInstance(NarutoEffects.STUN.get(), duration, 0, false, false, false));

            if (target instanceof Player player) {
                owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                    ToggledEyes eyes = new ToggledEyes(cap.getCurrentEyes().getId(), cap.getSharinganLevel(), cap.getMangekyoType());
                    PacketHandler.sendToClient(new GenjutsuS2CPacket(eyes, duration), (ServerPlayer) player);
                    cap.increaseMangekyoBlindness(0.1F);
                });
            }
        }
    }
}
