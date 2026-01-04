package net.takerudavis.butchers_delight_rechopped.block.carcass;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.takerudavis.butchers_delight_rechopped.block.AbstractHookableCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.IRoastableCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.ProcessingStage;
import net.takerudavis.butchers_delight_rechopped.block.entity.CarcassBlockEntity;

public class SheepCarcassBlock extends AbstractHookableCarcassBlock implements IRoastableCarcassBlock {

    public SheepCarcassBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ProcessingStage getInitialStage(LivingEntity entity) {
        return entity instanceof Sheep sheep && sheep.isSheared()
                ? ProcessingStage.SKINNED
                : ProcessingStage.INTACT;
    }

    @Override
    public CompoundTag extractAnimalData(LivingEntity entity) {
        if (entity instanceof net.minecraft.world.entity.animal.Sheep sheep) {
            CompoundTag data = new CompoundTag();
            // Extract normal/cold/warm variant
            data.putInt("WoolColor", sheep.getColor().getId());
            return data;
        }
        return null;
    }

    @Override
    public void processBasic(Level level, BlockPos pos, BlockState state, Player player) {
        // Implementation for processing a carcass with a cleaver when placed on a block
        // This involves changing the block state to a different processing stage,
        // dropping items, playing sounds, etc.
    }

    @Override
    public void processHooked(Level level, BlockPos pos, BlockState state, Player player) {
        // Implementation for processing a hooked carcass with a cleaver when hooked
    }

    @Override
    public void processRoasted(Level level, BlockPos pos, BlockState state, Player player) {
        // Implementation for processing a roasted carcass with a cleaver when roasted
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (stack.getItem() instanceof DyeItem dyeItem && state.getValue(STAGE) == ProcessingStage.INTACT) {
            if (level.getBlockEntity(pos) instanceof CarcassBlockEntity be) {
                CompoundTag data = be.getCarcassData();
                data.putInt("WoolColor", dyeItem.getDyeColor().getId());
                be.setChanged();
                // Sync to clients
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);

                stack.shrink(1);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

}
