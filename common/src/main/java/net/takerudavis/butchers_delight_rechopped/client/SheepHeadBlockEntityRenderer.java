package net.takerudavis.butchers_delight_rechopped.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.takerudavis.butchers_delight_rechopped.block.entity.SheepHeadBlockEntity;

public class SheepHeadBlockEntityRenderer implements BlockEntityRenderer<SheepHeadBlockEntity> {

    private final SkullModelBase model;

    private static RenderType cachedRenderType;

    public SheepHeadBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new SkullModel(context.bakeLayer(ModelLayers.PLAYER_HEAD));
    }

    @Override
    public void render(SheepHeadBlockEntity entity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int light, int overlay) {
        BlockState state = entity.getBlockState();
        boolean isWall = state.getBlock() instanceof WallSkullBlock;
        Direction facing = isWall ? state.getValue(WallSkullBlock.FACING) : null;
        int rotation = isWall
                ? RotationSegment.convertToSegment(facing.getOpposite())
                : state.getValue(SkullBlock.ROTATION);

        if (cachedRenderType == null)
            cachedRenderType = SkullBlockRenderer.getRenderType(SkullBlock.Types.PLAYER, SheepHeadBlockEntity.KOLISH_PROFILE);

        SkullBlockRenderer.renderSkull(facing, RotationSegment.convertToDegrees(rotation),
                entity.getAnimation(partialTick), poseStack, buffer, light, model,
                cachedRenderType);
    }

}
