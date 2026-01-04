package net.takerudavis.butchers_delight_rechopped.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
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
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (
                stack.getItem() instanceof BlockItem blockItem &&
                blockItem.getBlock() instanceof AbstractHookableCarcassBlock carcassItem
        ) {
            BlockPos belowPos = pos.below();

            if (level.getBlockState(belowPos).canBeReplaced()) {
                BlockState carcassState = carcassItem.defaultBlockState();

                BlockItemStateProperties stateProps = stack.get(DataComponents.BLOCK_STATE);
                if (stateProps != null) {
                    carcassState = stateProps.apply(carcassState);
                }

                carcassState = carcassState.setValue(AbstractHookableCarcassBlock.HOOKED, true)
                        .setValue(AbstractHookableCarcassBlock.FACING, state.getValue(HookBlock.FACING));

                level.setBlockAndUpdate(belowPos, carcassState);

                if (level.getBlockEntity(belowPos) instanceof CarcassBlockEntity carcassBlockEntity) {
                    CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
                    if (data != null) {
                        data.loadInto(carcassBlockEntity, level.registryAccess());
                    }
                }

                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
