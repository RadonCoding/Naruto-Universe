package radon.naruto_universe.ability.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fml.LogicalSide;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.block.AmaterasuBlock;
import radon.naruto_universe.block.NarutoBlocks;
import radon.naruto_universe.capability.data.NarutoDataHandler;
import radon.naruto_universe.capability.ninja.NinjaPlayerHandler;
import radon.naruto_universe.capability.ninja.NinjaRank;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.sound.NarutoSounds;
import radon.naruto_universe.util.HelperMethods;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Amaterasu extends Ability implements Ability.IChanneled {
    public static final float DAMAGE = 1.0F;
    private static final double RAYCAST_RANGE = 50.0D;
    private static final double RAYCAST_RADIUS = 1.0D;

    @Override
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.MANGEKYO);
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean(false);
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> result.set(cap.hasUnlockedAbility(NarutoAbilities.MANGEKYO.get())));
        return result.get();
    }

    @Override
    public SoundEvent getActivationSound() {
        return null;
    }

    @Override
    public boolean shouldLog(LivingEntity owner) {
        return false;
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.CHANNELED;
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.UNRANKED;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        return new AbilityDisplayInfo(this.getId().getPath(), 9.0F, 1.0F);
    }

    @Override
    public Ability getParent() {
        return NarutoAbilities.MANGEKYO.get();
    }

    private void shoot(LivingEntity owner) {
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(ownerCap -> {
            ownerCap.delayTickEvent((ownerClone) -> {
                HitResult result = HelperMethods.getHitResult(owner, RAYCAST_RANGE, RAYCAST_RADIUS);

                if (result != null) {
                    if (result.getType() == HitResult.Type.ENTITY) {
                        EntityHitResult hit = (EntityHitResult) result;

                        if (hit.getEntity() instanceof LivingEntity target) {
                            target.getCapability(NarutoDataHandler.INSTANCE).ifPresent(targetCap -> targetCap.setLocalBurning(target, true));
                        }
                    } else if (result.getType() == HitResult.Type.BLOCK) {
                        BlockHitResult hit = (BlockHitResult) result;
                        BlockPos pos = hit.getBlockPos();
                        BlockState state = owner.level.getBlockState(pos);

                        if (!state.is(NarutoBlocks.AMATERASU.get())) {
                            if (state.getBlock().defaultDestroyTime() > -1.0F) {
                                owner.level.setBlock(pos, AmaterasuBlock.getState(owner.level, pos), 2);
                            }
                        }
                    }
                }
            }, 2 * 20, owner.level.isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER);
        });
    }

    private void extinguish(LivingEntity owner) {
        owner.getCapability(NarutoDataHandler.INSTANCE).ifPresent(ownerCap -> {
            if (ownerCap.isLocalBurning()) {
                ownerCap.setLocalBurning(owner, false);
            } else {
                HitResult result = HelperMethods.getHitResult(owner, RAYCAST_RANGE, RAYCAST_RADIUS);

                if (result != null) {
                    if (result.getType() == HitResult.Type.ENTITY) {
                        EntityHitResult hit = (EntityHitResult) result;

                        if (hit.getEntity() instanceof LivingEntity target) {
                            target.getCapability(NarutoDataHandler.INSTANCE).ifPresent(targetCap -> targetCap.setLocalBurning(target, false));
                        }
                    } else if (result.getType() == HitResult.Type.BLOCK) {
                        BlockHitResult hit = (BlockHitResult) result;

                        for (Direction direction : Direction.values()) {
                            BlockPos pos = hit.getBlockPos().relative(direction);
                            BlockState state = owner.level.getBlockState(pos);

                            if (state.is(NarutoBlocks.AMATERASU.get())) {
                                owner.level.destroyBlock(pos, false);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onStart(LivingEntity owner, boolean isClientSide) {
        if (!owner.isShiftKeyDown()) {
            if (isClientSide) {
                owner.level.playLocalSound(owner.getX(), owner.getY(), owner.getZ(), NarutoSounds.AMATERASU.get(), SoundSource.MASTER, 3.0F, 1.0F, false);
            } else {
                if (owner instanceof Player player) {
                    owner.level.playSound(player, owner.getX(), owner.getY(), owner.getZ(), NarutoSounds.AMATERASU.get(), SoundSource.MASTER, 3.0F, 1.0F);
                } else {
                    owner.level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), NarutoSounds.AMATERASU.get(), SoundSource.MASTER, 3.0F, 1.0F);
                }
            }
        }
        owner.getCapability(NinjaPlayerHandler.INSTANCE).ifPresent(cap -> cap.increaseMangekyoBlindess(0.01F));
    }

    @Override
    public void runServer(LivingEntity owner) {
        if (!owner.isShiftKeyDown()) {
            this.shoot(owner);
        } else {
            this.extinguish(owner);
        }
    }
}
