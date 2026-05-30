package net.takerudavis.butchers_delight_rechopped.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.takerudavis.butchers_delight_rechopped.block.entity.CarcassBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HookBlock extends Block {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    private static final VoxelShape SHAPE_NORTH = Block.box(7, 8, 12, 9, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(7, 8, 0, 9, 16, 4);
    private static final VoxelShape SHAPE_EAST = Block.box(0, 8, 7, 4, 16, 9);
    private static final VoxelShape SHAPE_WEST = Block.box(12, 8, 7, 16, 16, 9);

    public HookBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(makeDefaultProperties());
    }

    protected @NotNull BlockState makeDefaultProperties() {
        return this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockState below = level.getBlockState(pos.below());
        if (below.getBlock() instanceof AbstractHookableCarcassBlock
                && below.getValue(AbstractHookableCarcassBlock.HOOKED)) {
            return Shapes.block();
        }
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AbstractHookableCarcassBlock carcassBlock) {
            BlockPos belowPos = pos.below();

            if (level.getBlockState(belowPos).canBeReplaced()) {
                CompoundTag blockStateTag = stack.getTagElement("BlockStateTag");
                BlockState carcassState = AbstractCarcassBlock.applyBlockStateTag(
                        carcassBlock.defaultBlockState(), blockStateTag);

                carcassState = carcassState.setValue(AbstractHookableCarcassBlock.HOOKED, true)
                        .setValue(AbstractHookableCarcassBlock.FACING, state.getValue(HookBlock.FACING));

                level.setBlockAndUpdate(belowPos, carcassState);

                CompoundTag blockEntityTag = stack.getTagElement("BlockEntityTag");
                if (level.getBlockEntity(belowPos) instanceof CarcassBlockEntity carcassBlockEntity && blockEntityTag != null) {
                    carcassBlockEntity.load(blockEntityTag.copy());
                    carcassBlockEntity.setChanged();
                    level.sendBlockUpdated(belowPos, carcassState, carcassState, Block.UPDATE_CLIENTS);
                }

                if (!player.isCreative()) stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }

        // Forward to hooked carcass below
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (belowState.getBlock() instanceof AbstractHookableCarcassBlock
                && belowState.getValue(AbstractHookableCarcassBlock.HOOKED)) {
            return belowState.use(level, player, hand,
                    new BlockHitResult(hitResult.getLocation(), hitResult.getDirection(), belowPos, false));
        }

        return InteractionResult.PASS;
    }
}
