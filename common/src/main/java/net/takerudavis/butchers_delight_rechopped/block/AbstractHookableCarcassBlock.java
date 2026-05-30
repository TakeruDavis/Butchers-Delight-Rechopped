package net.takerudavis.butchers_delight_rechopped.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.takerudavis.butchers_delight_rechopped.common.CommonSounds;
import net.takerudavis.butchers_delight_rechopped.item.CleaverItem;
import org.jetbrains.annotations.NotNull;

abstract public class AbstractHookableCarcassBlock extends AbstractCarcassBlock {

    public static final BooleanProperty HOOKED = BooleanProperty.create("hooked");

    public AbstractHookableCarcassBlock(String geoId, Properties properties) {
        super(geoId, properties);
    }

    @Override
    protected @NotNull BlockState makeDefaultProperties() {
        return super.makeDefaultProperties()
                .setValue(HOOKED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HOOKED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        if (state.getValue(HOOKED)) return RenderShape.ENTITYBLOCK_ANIMATED;
        return super.getRenderShape(state);
    }

    @Override
    protected boolean requiresGroundSupport(BlockState state) {
        return !state.getValue(HOOKED);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide() || !state.getValue(HOOKED) || !fromPos.equals(pos.above())) return;
        if (level.getBlockState(pos.above()).getBlock() instanceof HookBlock) return;
        dropAsItem(level, pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (state.getValue(HOOKED) && heldItem.getItem() instanceof CleaverItem
                && state.getValue(BEHEADED)) {
            if (getShearStage() != null && state.getValue(STAGE) == ProcessingStage.INTACT) {
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            if (!level.isClientSide()) {
                level.playSound(null, pos, CommonSounds.CLEAVER_CHOP, SoundSource.BLOCKS, 1.0f, 0.4f);
                processHooked(level, pos, state, player);
                heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    //Cleaver used on a hooked carcass
    abstract public void processHooked(Level level, BlockPos pos, BlockState state, Player player);

}
