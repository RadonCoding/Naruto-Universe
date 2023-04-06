package radon.naruto_universe.ability.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.NinjaPlayerHandler;
import radon.naruto_universe.capability.NinjaRank;
import radon.naruto_universe.capability.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.sound.NarutoSounds;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Sharingan extends Ability implements Ability.IToggled, Ability.ISpecial {
    @Override
    public boolean isDojutsu() {
        return true;
    }

    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.UNLOCKED_SHARINGAN);
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.ACADEMY_STUDENT;
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public AbilityDisplayInfo getDisplay() {
        return new AbilityDisplayInfo(this.getId().getPath(), 6.0F, 0.0F);
    }

    @Override
    public ChatFormatting getChatColor() {
        return ChatFormatting.DARK_RED;
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.POWER_CHARGE.get();
    }

    @Override
    public float getCost() {
        return 0.025F;
    }

    @SubscribeEvent
    public static void onProjectileImpact(final ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof EntityHitResult result) {
            final Entity entity = result.getEntity();

            entity.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> {
                if (cap.hasToggledAbility(NarutoAbilities.SHARINGAN.get())) {
                    final Random rand = new Random();

                    if (entity.level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CLOUD, entity.getX(), entity.getY() + (entity.getBbHeight() / 2.0F), entity.getZ(), 0,
                                0.0D, 0.0D, 0.0D, 0.0D);
                    }

                    BlockPos pos = entity.blockPosition();
                    double diffX = entity.getX() - pos.getX();
                    double diffZ = entity.getZ() - pos.getZ();

                    Vec3 movement = entity.getDeltaMovement();

                    if (diffX <= 0.1D) {
                        movement = new Vec3(rand.nextDouble(), 0.0D, movement.z());
                    } else if (diffX >= 0.9D) {
                        movement = new Vec3(-rand.nextDouble(), 0.0D, movement.z());
                    } else if (diffZ <= 0.1D) {
                        movement = new Vec3(movement.x(), 0.0D, rand.nextDouble());
                    } else if (diffZ >= 0.9D) {
                        movement = new Vec3(movement.x(), 0.0D, -rand.nextDouble());
                    } else {
                        movement = new Vec3(rand.nextDouble() * 2.0D - 1.0D, 0.0D, rand.nextDouble() * 2.0D - 1.0D);
                    }

                    entity.setDeltaMovement(movement);
                    event.setCanceled(true);
                }
            });
        }
    }

    @Override
    public void runClient(LivingEntity owner) {

    }

    @Override
    public void runServer(LivingEntity owner) {

    }

    @Override
    public SoundEvent getActivationSound() {
        return NarutoSounds.SHARINGAN_ACTIVATE.get();
    }

    @Override
    public SoundEvent getDectivationSound() {
        return NarutoSounds.SHARINGAN_DEACTIVATE.get();
    }

    @Override
    public List<Ability> getSpecialAbilities() {
        return List.of(NarutoAbilities.GENJUTSU.get(), NarutoAbilities.AMATERASU.get(), NarutoAbilities.SUSANOO.get());
    }
}
