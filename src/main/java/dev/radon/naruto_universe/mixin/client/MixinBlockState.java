package dev.radon.naruto_universe.mixin.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.shinobi.ShinobiPlayerProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.extensions.IForgeBlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(BlockState.class)
public abstract class MixinBlockState extends BlockBehaviour.BlockStateBase implements IForgeBlockState {
    protected MixinBlockState(Block pOwner, ImmutableMap<Property<?>, Comparable<?>> pValues, MapCodec<BlockState> pPropertiesCodec) {
        super(pOwner, pValues, pPropertiesCodec);
    }

    @Override
    public boolean isLadder(LevelReader level, BlockPos pos, LivingEntity entity) {
        AtomicBoolean result = new AtomicBoolean(IForgeBlockState.super.isLadder(level, pos, entity));

        entity.getCapability(ShinobiPlayerProvider.SHINOBI_PLAYER).ifPresent(cap -> {
            if (cap.hasToggledAbility(AbilityRegistry.CHAKRA_CONTROL.get())) {
                result.set(entity.horizontalCollision && !level.noCollision(entity
                        .getBoundingBox().inflate(0.01, entity.maxUpStep, 0.01)));
            }
        });

        return result.get();
    }
}
