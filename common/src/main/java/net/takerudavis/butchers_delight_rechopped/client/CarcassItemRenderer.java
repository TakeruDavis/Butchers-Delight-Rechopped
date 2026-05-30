package net.takerudavis.butchers_delight_rechopped.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.CarcassRegistry;
import net.takerudavis.butchers_delight_rechopped.item.CarcassBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CarcassItemRenderer extends GeoItemRenderer<CarcassBlockItem> {

    public CarcassItemRenderer() {
        super(new CarcassItemGeoModel());
        CarcassRegistry.getAll().forEach(block -> block.attachItemRenderLayers(this));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!(stack.getItem() instanceof CarcassBlockItem item)
                || !(item.getBlock() instanceof AbstractCarcassBlock acb)
                || !CarcassGeoModel.hasGeoModel(acb.getGeoId())) {
            return;
        }
        ((CarcassItemGeoModel) getGeoModel()).currentStack = stack;
        super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
    }
}
