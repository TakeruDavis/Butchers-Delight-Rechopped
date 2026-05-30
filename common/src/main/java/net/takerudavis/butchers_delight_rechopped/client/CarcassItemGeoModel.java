package net.takerudavis.butchers_delight_rechopped.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.item.CarcassBlockItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;

public class CarcassItemGeoModel extends CarcassGeoModel<CarcassBlockItem> {

    ItemStack currentStack = ItemStack.EMPTY;

    @Override
    protected String getGeoId(CarcassBlockItem item) {
        return ((AbstractCarcassBlock) item.getBlock()).getGeoId();
    }

    @Override
    public void setCustomAnimations(CarcassBlockItem animatable, long instanceId, AnimationState<CarcassBlockItem> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        if (!(animatable.getBlock() instanceof AbstractCarcassBlock carcassBlock)) return;

        ItemStack stack = animationState.getData(DataTickets.ITEMSTACK);
        BlockState state = AbstractCarcassBlock.applyBlockStateTag(
                carcassBlock.defaultBlockState(),
                stack.getTagElement("BlockStateTag")
        );

        CompoundTag carcassData = CarcassBlockItem.getCarcassData(stack);
        applyBoneVisibility(carcassBlock, state, carcassData);
    }

    @Override
    protected String getTextureName(CarcassBlockItem item) {
        if (currentStack.isEmpty() || !(item.getBlock() instanceof AbstractCarcassBlock acb)) return "base";
        BlockState state = AbstractCarcassBlock.applyBlockStateTag(
                acb.defaultBlockState(), currentStack.getTagElement("BlockStateTag"));
        return acb.getTextureName(state);
    }

}
