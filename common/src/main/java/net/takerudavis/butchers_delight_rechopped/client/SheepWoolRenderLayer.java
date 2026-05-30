package net.takerudavis.butchers_delight_rechopped.client;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.ProcessingStage;
import net.takerudavis.butchers_delight_rechopped.block.carcass.SheepCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.entity.CarcassBlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoRenderer;

public class SheepWoolRenderLayer extends AbstractSheepWoolRenderLayer<CarcassBlockEntity> {

    public SheepWoolRenderLayer(GeoRenderer<CarcassBlockEntity> renderer) {
        super(renderer);
    }

    @Override
    protected @Nullable SheepWoolContext getContext(CarcassBlockEntity animatable) {
        BlockState state = animatable.getBlockState();
        if (state.getValue(AbstractCarcassBlock.STAGE) != ProcessingStage.INTACT) return null;
        DyeColor color = DyeColor.byId(animatable.getCarcassData().getInt(SheepCarcassBlock.WOOL_COLOR_KEY));
        return new SheepWoolContext(state.getValue(AbstractCarcassBlock.BEHEADED), color.getTextureDiffuseColors());
    }
}
