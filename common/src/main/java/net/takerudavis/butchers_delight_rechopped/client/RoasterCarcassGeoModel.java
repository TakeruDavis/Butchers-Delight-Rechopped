package net.takerudavis.butchers_delight_rechopped.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.entity.RoasterBlockEntity;
import net.takerudavis.butchers_delight_rechopped.item.CarcassBlockItem;
import software.bernie.geckolib.core.animation.AnimationState;

public class RoasterCarcassGeoModel extends CarcassGeoModel<RoasterBlockEntity> {

    @Override
    protected String getGeoId(RoasterBlockEntity be) {
        if (be.getCarcass().getItem() instanceof BlockItem bi
                && bi.getBlock() instanceof AbstractCarcassBlock acb) {
            return acb.getGeoId();
        }
        return null;
    }

    @Override
    public void setCustomAnimations(RoasterBlockEntity animatable, long instanceId, AnimationState<RoasterBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        if (!animatable.hasCarcass()) return;
        ItemStack stack = animatable.getCarcass();
        if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof AbstractCarcassBlock acb)) return;
        CompoundTag carcassData = CarcassBlockItem.getCarcassData(stack);
        applyBoneVisibility(acb, resolveState(stack, acb), carcassData);
    }

    @Override
    protected String getTextureName(RoasterBlockEntity be) {
        if (!be.hasCarcass()) return "base";
        ItemStack stack = be.getCarcass();
        if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof AbstractCarcassBlock acb)) return "base";
        return acb.getTextureName(resolveState(stack, acb));
    }

    private static BlockState resolveState(ItemStack stack, AbstractCarcassBlock acb) {
        return AbstractCarcassBlock.applyBlockStateTag(acb.defaultBlockState(), stack.getTagElement("BlockStateTag"));
    }
}
