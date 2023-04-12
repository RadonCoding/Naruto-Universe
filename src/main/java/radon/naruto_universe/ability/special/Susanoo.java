package radon.naruto_universe.ability.special;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.entity.SusanooEntity;

import static net.minecraftforge.common.ForgeMod.REACH_DISTANCE;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Susanoo extends Ability implements Ability.IToggled {
    @Override
    public NinjaRank getRank() {
        return null;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return null;
    }

    @Override
    public Ability getParent() {
        return null;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return true;
    }

    @Override
    public float getCost(LivingEntity owner) {
        SusanooEntity susanoo = (SusanooEntity) owner.getVehicle();

        if (susanoo != null) {
            return switch (susanoo.getStage()) {
                case RIBCAGE -> 1.0F;
                case SKELETAL -> 2.5F;
                case ARMORED -> 5.0F;
                case PERFECT -> 10.0F;
            };
        }
        return 0.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 60 * 20; // 5 minute cooldown
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        DamageSource source = event.getSource();

        if (!source.isBypassArmor()) {
            LivingEntity owner = event.getEntity();

            owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                if (cap.hasToggledAbility(NarutoAbilities.SUSANOO.get())) {
                    if (owner.getVehicle() instanceof SusanooEntity susanoo) {
                        susanoo.hurt(source, event.getAmount());
                        event.setCanceled(true);
                    }
                }
            });
        }
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        if (!(owner.getVehicle() instanceof SusanooEntity)) {
            return Status.FAILURE;
        }
        return super.checkStatus(owner);
    }

    @Override
    public void runServer(LivingEntity owner) {
        if (owner.getVehicle() instanceof SusanooEntity susanoo) {
            int multiplier = susanoo.getStage().ordinal();
            owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.increaseMangekyoBlindess(multiplier * 0.001F));
        }
    }

    @Override
    public void runClient(LivingEntity owner) {
        if (owner.getVehicle() instanceof SusanooEntity susanoo) {
            int multiplier = susanoo.getStage().ordinal();
            owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.increaseMangekyoBlindess(multiplier * 0.001F));
        }
    }

    @Override
    public void onToggled(LivingEntity owner, boolean isClientSide) {
        if (!isClientSide) {
            SusanooEntity susanoo = new SusanooEntity(owner);
            owner.level.addFreshEntity(susanoo);
        }
    }

    @Override
    public void onDisabled(LivingEntity owner, boolean isClientSide) {
        if (owner.getVehicle() instanceof SusanooEntity susanoo) {
            susanoo.discard();

            AttributeInstance reachAttribute = owner.getAttribute(REACH_DISTANCE.get());
            AttributeInstance meleeAttribute = owner.getAttribute(Attributes.ATTACK_DAMAGE);
            AttributeInstance knockbackAttribute = owner.getAttribute(Attributes.ATTACK_KNOCKBACK);

            if (reachAttribute != null) {
                reachAttribute.removeModifier(SusanooEntity.REACH_DISTANCE_UUID);
            }

            if (meleeAttribute != null) {
                meleeAttribute.removeModifier(SusanooEntity.ATTACK_DAMAGE_UUID);
            }

            if (knockbackAttribute != null) {
                knockbackAttribute.removeModifier(SusanooEntity.ATTACK_KNOCKBACK_UUID);
            }
        }
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }
}
