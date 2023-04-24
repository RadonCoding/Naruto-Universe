package radon.naruto_universe.ability.special;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.capability.ninja.NinjaRank;
import radon.naruto_universe.capability.ninja.NinjaTrait;
import radon.naruto_universe.client.gui.widget.AbilityDisplayInfo;
import radon.naruto_universe.entity.MeteoriteEntity;
import radon.naruto_universe.util.HelperMethods;

import java.util.List;

public class TengaiShinsei extends Ability {
    private static final double RANGE = 50.0D;
    private static final double HEIGHT = 128.0D;

    private static final List<Block> BLOCKS = List.of(Blocks.BASALT, Blocks.SMOOTH_BASALT, Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE);

    @Override
    public float getCost(LivingEntity owner) {
        return 1000.0F;
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
    public List<NinjaTrait> getRequirements() {
        return List.of(NinjaTrait.RINNEGAN);
    }

    @Override
    public NinjaRank getRank() {
        return NinjaRank.UNRANKED;
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
    public void runServer(LivingEntity owner) {
        HitResult result = HelperMethods.getHitResult(owner, RANGE, 1.0D);

        if (result != null) {
            BlockPos pos = null;

            if (result.getType() == HitResult.Type.ENTITY) {
                EntityHitResult hit = (EntityHitResult) result;
                pos = hit.getEntity().blockPosition();

            } else if (result.getType() == HitResult.Type.BLOCK) {
                BlockHitResult hit = (BlockHitResult) result;
                pos = hit.getBlockPos();
            }

            if (pos != null) {
                MeteoriteEntity meteorite = new MeteoriteEntity(owner, pos.getX(), pos.getY() + HEIGHT, pos.getZ());
                meteorite.setSize(64);

                int count = HelperMethods.RANDOM.nextInt(4, 12);

                for (int i = 0; i < count; i++) {
                    meteorite.addBlock(BLOCKS.get(HelperMethods.RANDOM.nextInt(BLOCKS.size())));
                }

                owner.level.addFreshEntity(meteorite);

                meteorite.drop();
            }
        }
    }
}
