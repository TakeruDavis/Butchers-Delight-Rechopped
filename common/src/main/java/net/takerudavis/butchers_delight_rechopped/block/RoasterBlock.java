package net.takerudavis.butchers_delight_rechopped.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.takerudavis.butchers_delight_rechopped.block.entity.RoasterBlockEntity;
import net.takerudavis.butchers_delight_rechopped.common.CommonSounds;
import net.takerudavis.butchers_delight_rechopped.item.CarcassBlockItem;
import net.takerudavis.butchers_delight_rechopped.item.CleaverItem;
import org.jetbrains.annotations.Nullable;

public class RoasterBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    public static final BooleanProperty HAS_CARCASS = BooleanProperty.create("has_carcass");

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 10, 16);

    public RoasterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HAS_CARCASS, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_CARCASS);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof RoasterBlockEntity be)) return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);

        if (be.hasCarcass()) {
            ItemStack carcass = be.getCarcass();
            if (!(carcass.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof AbstractCarcassBlock acb)) return InteractionResult.PASS;
            CompoundTag bst = carcass.getOrCreateTagElement("BlockStateTag");

            if (held.getItem() instanceof CleaverItem) {
                boolean beheaded = "true".equals(bst.getString(AbstractCarcassBlock.BEHEADED.getName()));
                if (acb.isBeheadable() && !beheaded) {
                    level.playSound(null, pos, CommonSounds.CLEAVER_CHOP, SoundSource.BLOCKS, 1.0f, 0.45f);
                    bst.putString(AbstractCarcassBlock.BEHEADED.getName(), "true");
                    for (ItemStack drop : acb.getHeadDrops(CarcassBlockItem.getCarcassData(carcass))) {
                        Block.popResource(level, pos, drop);
                    }
                    held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                    be.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                    return InteractionResult.SUCCESS;
                }

                if (acb.getShearStage() != null
                        && ProcessingStage.INTACT.getSerializedName().equals(bst.getString(AbstractCarcassBlock.STAGE.getName()))) {
                    return InteractionResult.SUCCESS;
                }
                BlockState carcassState = AbstractCarcassBlock.applyBlockStateTag(acb.defaultBlockState(), bst);
                level.playSound(null, pos, CommonSounds.CLEAVER_CHOP, SoundSource.BLOCKS, 1.0f, 0.3f);
                ((IRoastableCarcassBlock) acb).processRoasted(level, pos, carcassState, player);
                held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                be.removeCarcass();
                level.setBlockAndUpdate(pos, state.setValue(HAS_CARCASS, false));
                return InteractionResult.SUCCESS;
            }

            if (held.is(Items.SHEARS)) {
                if (acb.getShearStage() == null) return InteractionResult.SUCCESS;
                if (!ProcessingStage.INTACT.getSerializedName().equals(bst.getString(AbstractCarcassBlock.STAGE.getName())))
                    return InteractionResult.SUCCESS;
                for (ItemStack drop : acb.getShearDrops(CarcassBlockItem.getCarcassData(carcass), level.random)) {
                    Block.popResource(level, pos, drop);
                }
                acb.evaluateLootTable(acb.getShearLootTable(), level, pos, player, null, null);
                bst.putString(AbstractCarcassBlock.STAGE.getName(), acb.getShearStage().getSerializedName());
                held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.NEUTRAL, 1.0f, 1.0f);
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.SUCCESS;
            }

            if (held.getItem() instanceof DyeItem dyeItem) {
                CompoundTag cd = CarcassBlockItem.getCarcassData(carcass);
                if (!acb.applyDye(dyeItem.getDyeColor(), bst, cd)) return InteractionResult.PASS;
                carcass.getOrCreateTagElement("BlockEntityTag").put("CarcassData", cd);
                if (!player.isCreative()) held.shrink(1);
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.SUCCESS;
            }

            if (held.isEmpty()) {
                player.getInventory().add(be.removeCarcass());
                level.setBlockAndUpdate(pos, state.setValue(HAS_CARCASS, false));
                // TODO: play retrieval sound
                return InteractionResult.SUCCESS;
            }
        } else if (held.getItem() instanceof BlockItem bi && bi.getBlock() instanceof IRoastableCarcassBlock) {
            CompoundTag bst = held.getTagElement("BlockStateTag");
            if (bst != null && ProcessingStage.DELIMBED.getSerializedName().equals(
                    bst.getString(AbstractCarcassBlock.STAGE.getName()))) return InteractionResult.PASS;
            be.setCarcass(held.copyWithCount(1));
            if (!player.isCreative()) held.shrink(1);
            level.setBlockAndUpdate(pos, state.setValue(HAS_CARCASS, true));
            // TODO: play placement sound
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RoasterBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                             BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, RoasterBlockEntity.TYPE, RoasterBlockEntity::serverTick);
    }
}
