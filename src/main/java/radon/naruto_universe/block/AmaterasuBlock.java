package radon.naruto_universe.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import radon.naruto_universe.capability.data.NarutoDataHandler;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AmaterasuBlock extends BaseFireBlock {
    private static final int BURN_CHANCE = 20;

    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((map) -> map.getKey() != Direction.DOWN).collect(Util.toMap());
    public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private final Map<BlockState, VoxelShape> shapesCache;

    public AmaterasuBlock(Properties pProperties) {
        super(pProperties, 0.0F);

        this.registerDefaultState((this.stateDefinition.any().setValue(AGE, 0)).setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE));
        this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().filter(state -> state.getValue(AGE) == 0).collect(Collectors.toMap(Function.identity(), AmaterasuBlock::calculateShape)));
    }

    public static BlockState getState(BlockGetter pReader, BlockPos pPos) {
        return ((AmaterasuBlock) NarutoBlocks.AMATERASU.get()).getStateForPlacement(pReader, pPos);
    }

    @Override
    public void entityInside(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {
        if (pEntity instanceof LivingEntity living) {
            living.getCapability(NarutoDataHandler.INSTANCE).ifPresent(cap -> cap.setLocalBurning(living, true));
        }
    }

    private BlockState getStateForPlacement(BlockGetter pLevel, BlockPos pPos) {
        BlockPos belowPos = pPos.below();
        BlockState belowState = pLevel.getBlockState(belowPos);

        if (!this.canCatchFire(pLevel, pPos) && !belowState.isFaceSturdy(pLevel, belowPos, Direction.UP)) {
            BlockState result = this.defaultBlockState();

            for(Direction direction : Direction.values()) {
                BooleanProperty prop = PROPERTY_BY_DIRECTION.get(direction);

                if (prop != null) {
                    result = result.setValue(prop, this.canCatchFire(pLevel, pPos.relative(direction)));
                }
            }
            return result;
        } else {
            return this.defaultBlockState();
        }
    }

    private BlockState getStateWithAge(LevelAccessor pLevel, BlockPos pPos, int pAge) {
        BlockState state = this.getStateForPlacement(pLevel, pPos);
        return state.is(NarutoBlocks.AMATERASU.get()) ? state.setValue(AGE, pAge) : state;
    }

    private void tryCatchFire(Level level, BlockPos pos, int chance, RandomSource rand, Direction face) {
        if (this.canCatchFire(level, pos) && rand.nextInt(chance) < BURN_CHANCE) {
            BlockState state = level.getBlockState(pos);

            if (rand.nextInt(chance + 10) < 5) {
                int j = Math.min(chance + rand.nextInt(5) / 4, 15);
                level.setBlock(pos, this.getStateWithAge(level, pos, j), 3);
            } else {
                level.removeBlock(pos, false);
            }
            state.onCaughtFire(level, pos, face, null);
        }
    }

    public boolean canSurvive(@NotNull BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos belowPos = pPos.below();
        return pLevel.getBlockState(belowPos).isFaceSturdy(pLevel, belowPos, Direction.UP) || this.isValidFireLocation(pLevel, pPos);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pNeighborPos) {
        return this.canSurvive(pState, pLevel, pCurrentPos) ? this.getStateWithAge(pLevel, pCurrentPos, pState.getValue(AGE)) : Blocks.AIR.defaultBlockState();
    }

    private boolean isValidFireLocation(BlockGetter pLevel, BlockPos pPos) {
        for (Direction direction : Direction.values()) {
            if (this.canCatchFire(pLevel, pPos.relative(direction))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick(@NotNull BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        if (pLevel.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            if (!pState.canSurvive(pLevel, pPos)) {
                pLevel.removeBlock(pPos, false);
            }

            BlockState blockstate = pLevel.getBlockState(pPos.below());
            boolean isFire = blockstate.isFireSource(pLevel, pPos, Direction.UP);
            int i = pState.getValue(AGE);

            int j = Math.min(15, i + pRandom.nextInt(3) / 2);

            if (i != j) {
                pState = pState.setValue(AGE, j);
                pLevel.setBlock(pPos, pState, 4);
            }

            if (!isFire) {
                if (!this.isValidFireLocation(pLevel, pPos)) {
                    BlockPos belowPos = pPos.below();

                    if (!pLevel.getBlockState(belowPos).isFaceSturdy(pLevel, belowPos, Direction.UP) || i > 3) {
                        pLevel.removeBlock(pPos, false);
                    }
                    return;
                }

                if (i == 15 && pRandom.nextInt(4) == 0 && !this.canCatchFire(pLevel, pPos.below())) {
                    pLevel.removeBlock(pPos, false);
                    return;
                }
            }

            boolean isHumid = pLevel.isHumidAt(pPos);
            int k = isHumid ? -50 : 0;
            this.tryCatchFire(pLevel, pPos.east(), 300 + k, pRandom, Direction.WEST);
            this.tryCatchFire(pLevel, pPos.west(), 300 + k, pRandom, Direction.EAST);
            this.tryCatchFire(pLevel, pPos.below(), 250 + k, pRandom, Direction.UP);
            this.tryCatchFire(pLevel, pPos.north(), 300 + k, pRandom, Direction.SOUTH);
            this.tryCatchFire(pLevel, pPos.south(), 300 + k, pRandom, Direction.NORTH);
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            for (int l = -1; l <= 1; ++l) {
                for (int i1 = -1; i1 <= 1; ++i1) {
                    for (int j1 = 0; j1 <= 4; ++j1) {
                        if (l != 0 || j1 != 0 || i1 != 0) {
                            int k1 = 100;

                            if (j1 > 1) {
                                k1 += (j1 - 1) * 100;
                            }

                            pos.setWithOffset(pPos, l, j1, i1);
                            int l1 = this.getIgniteOdds(pLevel, pos);

                            if (l1 > 0) {
                                int i2 = (l1 + 40 + pLevel.getDifficulty().getId() * 7) / (i + 30);

                                if (isHumid) {
                                    i2 /= 2;
                                }

                                if (i2 > 0 && pRandom.nextInt(k1) <= i2) {
                                    int j2 = Math.min(15, i + pRandom.nextInt(5) / 4);
                                    pLevel.setBlock(pos, this.getStateWithAge(pLevel, pos, j2), 3);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private int getIgniteOdds(LevelReader pLevel, BlockPos pPos) {
        if (!pLevel.isEmptyBlock(pPos) && this.canCatchFire(pLevel, pPos)) {
            return BURN_CHANCE;
        }
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
    }

    private static VoxelShape calculateShape(BlockState state) {
        VoxelShape voxelshape = Shapes.empty();

        if (state.getValue(UP)) {
            voxelshape = UP_AABB;
        }
        if (state.getValue(NORTH)) {
            voxelshape = Shapes.or(voxelshape, NORTH_AABB);
        }
        if (state.getValue(SOUTH)) {
            voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
        }
        if (state.getValue(EAST)) {
            voxelshape = Shapes.or(voxelshape, EAST_AABB);
        }
        if (state.getValue(WEST)) {
            voxelshape = Shapes.or(voxelshape, WEST_AABB);
        }
        return voxelshape.isEmpty() ? DOWN_AABB : voxelshape;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return this.shapesCache.get(pState.setValue(AGE, 0));
    }

    public boolean canCatchFire(BlockGetter world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return !state.is(Blocks.AIR) && !state.is(this);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        if (!this.canCatchFire(level, pos) && !belowState.isFaceSturdy(level, belowPos, Direction.UP)) {
            BlockState result = this.defaultBlockState();

            for(Direction direction : Direction.values()) {
                BooleanProperty prop = PROPERTY_BY_DIRECTION.get(direction);

                if (prop != null) {
                    result = result.setValue(prop, this.canCatchFire(level, pos.relative(direction)));
                }
            }
            return result;
        } else {
            return this.defaultBlockState();
        }
    }

    @Override
    protected boolean canBurn(@NotNull BlockState pState) {
        return true;
    }
}