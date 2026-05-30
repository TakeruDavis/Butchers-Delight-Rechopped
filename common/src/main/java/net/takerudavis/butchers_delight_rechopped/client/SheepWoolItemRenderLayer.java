package net.takerudavis.butchers_delight_rechopped.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.ProcessingStage;
import net.takerudavis.butchers_delight_rechopped.block.carcass.SheepCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.item.CarcassBlockItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class SheepWoolItemRenderLayer extends AbstractSheepWoolRenderLayer<CarcassBlockItem> {

    public SheepWoolItemRenderLayer(GeoRenderer<CarcassBlockItem> renderer) {
        super(renderer);
    }

    @Override
    protected @Nullable SheepWoolContext getContext(CarcassBlockItem animatable) {
        ItemStack stack = ((GeoItemRenderer<CarcassBlockItem>) getRenderer()).getCurrentItemStack();
        if (stack == null) return null;
        CompoundTag bst = stack.getTagElement("BlockStateTag");
        if (bst == null) return null;
        if (!ProcessingStage.INTACT.getSerializedName().equals(bst.getString(AbstractCarcassBlock.STAGE.getName()))) return null;
        boolean beheaded = "true".equals(bst.getString(AbstractCarcassBlock.BEHEADED.getName()));
        CompoundTag carcassData = CarcassBlockItem.getCarcassData(stack);
        DyeColor color = DyeColor.byId(carcassData.getInt(SheepCarcassBlock.WOOL_COLOR_KEY));
        return new SheepWoolContext(beheaded, color.getTextureDiffuseColors());
    }
}
