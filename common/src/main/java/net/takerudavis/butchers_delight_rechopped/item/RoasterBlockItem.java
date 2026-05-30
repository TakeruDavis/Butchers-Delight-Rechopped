package net.takerudavis.butchers_delight_rechopped.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class RoasterBlockItem extends BlockItem {

    public RoasterBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clicked = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clicked);
        BlockPos abovePos = clicked.above();

        if (clickedState.is(Blocks.CAMPFIRE) || clickedState.is(Blocks.SOUL_CAMPFIRE)) {
            if (level.getBlockState(abovePos).canBeReplaced()) {
                return this.place(BlockPlaceContext.at(new BlockPlaceContext(context), abovePos, Direction.UP));
            }
            return InteractionResult.FAIL;
        }

        if (context.getClickedFace() == Direction.UP && clickedState.isSolidRender(level, clicked)) {
            // Solid block: leave a gap for campfire to be placed below later
            BlockPos shiftedPos = abovePos.above();
            if (level.getBlockState(shiftedPos).canBeReplaced()) {
                return this.place(BlockPlaceContext.at(new BlockPlaceContext(context), shiftedPos, Direction.UP));
            }
        } else if (clickedState.canBeReplaced()) {
            // Replaceable block (short grass, etc.): use it as a gap left for campfire
            BlockState belowState = level.getBlockState(clicked.below());
            if (belowState.isSolidRender(level, clicked.below()) && level.getBlockState(abovePos).canBeReplaced()) {
                return this.place(BlockPlaceContext.at(new BlockPlaceContext(context), abovePos, Direction.UP));
            }
        }

        return super.useOn(context);
    }
}
