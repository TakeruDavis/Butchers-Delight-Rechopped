package net.takerudavis.butchers_delight_rechopped.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.ProcessingStage;
import net.takerudavis.butchers_delight_rechopped.block.carcass.SheepCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.entity.RoasterBlockEntity;
import net.takerudavis.butchers_delight_rechopped.item.CarcassBlockItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoRenderer;

public class SheepWoolRoasterRenderLayer extends AbstractSheepWoolRenderLayer<RoasterBlockEntity> {

    public SheepWoolRoasterRenderLayer(GeoRenderer<RoasterBlockEntity> renderer) {
        super(renderer);
    }

    @Override
    protected @Nullable SheepWoolContext getContext(RoasterBlockEntity animatable) {
        if (!animatable.hasCarcass()) return null;
        ItemStack carcassStack = animatable.getCarcass();
        if (!(carcassStack.getItem() instanceof BlockItem)) return null;
        CompoundTag bst = carcassStack.getTagElement("BlockStateTag");
        if (bst == null) return null;
        if (!ProcessingStage.INTACT.getSerializedName().equals(bst.getString(AbstractCarcassBlock.STAGE.getName()))) return null;
        boolean beheaded = "true".equals(bst.getString(AbstractCarcassBlock.BEHEADED.getName()));
        CompoundTag carcassData = CarcassBlockItem.getCarcassData(carcassStack);
        DyeColor color = DyeColor.byId(carcassData.getInt(SheepCarcassBlock.WOOL_COLOR_KEY));
        return new SheepWoolContext(beheaded, color.getTextureDiffuseColors());
    }
}
