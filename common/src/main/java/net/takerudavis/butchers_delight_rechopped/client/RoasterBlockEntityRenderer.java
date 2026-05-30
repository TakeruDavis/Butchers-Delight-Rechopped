package net.takerudavis.butchers_delight_rechopped.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.CarcassRegistry;
import net.takerudavis.butchers_delight_rechopped.block.IRoastableCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.RoasterBlock;
import net.takerudavis.butchers_delight_rechopped.block.entity.RoasterBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RoasterBlockEntityRenderer extends GeoBlockRenderer<RoasterBlockEntity> {

    public RoasterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new RoasterCarcassGeoModel());
        CarcassRegistry.getAll().forEach(block -> block.attachRoasterRenderLayers(this));
    }

    @Override
    public void render(RoasterBlockEntity animatable, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!animatable.hasCarcass()) return;

        var carcassItem = animatable.getCarcass().getItem();
        if (!(carcassItem instanceof BlockItem bi) || !(bi.getBlock() instanceof AbstractCarcassBlock acb)) return;

        if (CarcassGeoModel.hasGeoModel(acb.getGeoId())) {
            this.animatable = animatable;
            defaultRender(poseStack, animatable, bufferSource, null, null, 0, partialTick, packedLight);
        } else if (acb instanceof IRoastableCarcassBlock) {
            // Vanilla fallback: render block model rotated to match roaster facing
            Direction facing = animatable.getBlockState().getValue(RoasterBlock.FACING).getCounterClockWise();
            BlockState state = acb.defaultBlockState()
                    .setValue(AbstractCarcassBlock.FACING, animatable.getBlockState().getValue(RoasterBlock.FACING));
            poseStack.pushPose();
            poseStack.translate(0.5, 0, 0.5);
            rotateForFacing(facing, poseStack);
            poseStack.translate(-0.5, 0, -0.5);
            Minecraft.getInstance().getBlockRenderer()
                    .renderSingleBlock(state, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }

    private static void rotateForFacing(Direction facing, PoseStack poseStack) {
        switch (facing) {
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST  -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case EAST  -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            default    -> {} // NORTH: no rotation
        }
    }

    @Override
    protected Direction getFacing(RoasterBlockEntity block) {
        return block.getBlockState().getValue(RoasterBlock.FACING).getCounterClockWise();
    }
}
