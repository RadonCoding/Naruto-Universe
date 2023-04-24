package radon.naruto_universe.ability.special;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
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

public class Genjutsu extends Ability {
    private static final double RAYCAST_RANGE = 30.0D;

    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.SHARINGAN, NinjaTrait.MANGEKYO);
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.GENIN;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return new AbilityDisplayInfo(this.getId().getPath(), 9.0F, 0.0F);
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.SHARINGAN.get();
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean(false);

        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
            result.set(cap.hasToggledAbility(NarutoAbilities.SHARINGAN.get()) || cap.hasToggledAbility(NarutoAbilities.MANGEKYO.get()));
        });
        return result.get();
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
    public Status checkTriggerable(LivingEntity owner) {
        if (this.getTarget(owner) == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
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
            int duration = Math.max(10, Math.round(this.getExperience() * 0.25F)) * 20;
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, 5));
            target.addEffect(new MobEffectInstance(NarutoEffects.STUN.get(), duration, 0, false, false, false));

            if (target instanceof Player player) {
                owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(ownerCap -> {
                    ToggledEyes eyes = new ToggledEyes(ownerCap.getCurrentEyes().getId(), ownerCap.getSharinganLevel(), ownerCap.getMangekyoType());
                    PacketHandler.sendToClient(new GenjutsuS2CPacket(eyes, duration), (ServerPlayer) player);
                });
            }
        }
    }
}
