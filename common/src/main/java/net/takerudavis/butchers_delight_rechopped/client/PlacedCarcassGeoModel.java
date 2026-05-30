package net.takerudavis.butchers_delight_rechopped.client;

import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.MeatState;
import net.takerudavis.butchers_delight_rechopped.block.entity.CarcassBlockEntity;
import software.bernie.geckolib.core.animation.AnimationState;

public class PlacedCarcassGeoModel extends CarcassGeoModel<CarcassBlockEntity> {

    @Override
    protected String getGeoId(CarcassBlockEntity be) {
        return ((AbstractCarcassBlock) be.getBlockState().getBlock()).getGeoId();
    }

    @Override
    public void setCustomAnimations(CarcassBlockEntity animatable, long instanceId, AnimationState<CarcassBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        if (!(animatable.getBlockState().getBlock() instanceof AbstractCarcassBlock carcassBlock)) return;
        applyBoneVisibility(carcassBlock, animatable.getBlockState(), animatable.getCarcassData());
    }

    @Override
    protected String getTextureName(CarcassBlockEntity be) {
        if (be.getBlockState().getBlock() instanceof AbstractCarcassBlock acb) {
            return acb.getTextureName(be.getBlockState());
        }
        return "base";
    }

}
