package net.takerudavis.butchers_delight_rechopped.client;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.CarcassRegistry;
import net.takerudavis.butchers_delight_rechopped.block.entity.CarcassBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import com.mojang.blaze3d.vertex.PoseStack;

public class CarcassBlockEntityRenderer extends GeoBlockRenderer<CarcassBlockEntity> {

    public CarcassBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new PlacedCarcassGeoModel());
        CarcassRegistry.getAll().forEach(block -> block.attachBlockRenderLayers(this));
    }

    @Override
    public void render(CarcassBlockEntity animatable, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!(animatable.getBlockState().getBlock() instanceof AbstractCarcassBlock carcassBlock)
                || !CarcassGeoModel.hasGeoModel(carcassBlock.getGeoId())) {
            return;
        }
        this.animatable = animatable;
        defaultRender(poseStack, animatable, bufferSource, null, null, 0, partialTick, packedLight);
    }

    @Override
    protected Direction getFacing(CarcassBlockEntity block) {
        return block.getBlockState().getValue(AbstractCarcassBlock.FACING);
    }
}
