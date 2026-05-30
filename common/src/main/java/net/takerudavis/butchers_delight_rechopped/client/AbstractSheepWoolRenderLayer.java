package net.takerudavis.butchers_delight_rechopped.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import net.takerudavis.butchers_delight_rechopped.block.carcass.SheepCarcassBlock;

import java.util.List;

public abstract class AbstractSheepWoolRenderLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {

    private static final ResourceLocation FUR_TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/sheep/sheep_fur.png");
    private static final List<String> PARENT_BONES = List.of("body", "leg0", "leg1", "leg2", "leg3");

    protected AbstractSheepWoolRenderLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    record SheepWoolContext(boolean beheaded, float[] rgb) {}

    @Nullable
    protected abstract SheepWoolContext getContext(T animatable);

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                       float partialTick, int packedLight, int packedOverlay) {
        SheepWoolContext ctx = getContext(animatable);
        if (ctx == null) return;

        var model = getGeoModel();

        // reset visibility, BakedGeoModel is shared across instances and visibility from previous renders can carry over
        for (String name : PARENT_BONES) {
            model.getBone(name).ifPresent(bone -> { bone.setHidden(true); bone.setChildrenHidden(false); });
        }
        model.getBone("head").ifPresent(bone -> { bone.setHidden(true); bone.setChildrenHidden(false); });

        for (String name : SheepCarcassBlock.WOOL_BONES) {
            boolean hide = name.equals("wool_head") && ctx.beheaded();
            model.getBone(name).ifPresent(bone -> bone.setHidden(hide));
        }

        float[] rgb = ctx.rgb();
        RenderType furRenderType = RenderType.entityCutoutNoCull(FUR_TEXTURE);
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, furRenderType,
                bufferSource.getBuffer(furRenderType), partialTick, packedLight, packedOverlay,
                rgb[0], rgb[1], rgb[2], 1.0f);

        for (String name : PARENT_BONES) {
            model.getBone(name).ifPresent(bone -> bone.setHidden(false));
        }

        model.getBone("head").ifPresent(bone -> bone.setHidden(ctx.beheaded()));

        for (String name : SheepCarcassBlock.WOOL_BONES) {
            model.getBone(name).ifPresent(bone -> bone.setHidden(true));
        }
    }
}
