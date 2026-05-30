package net.takerudavis.butchers_delight_rechopped.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

import net.takerudavis.butchers_delight_rechopped.common.ButchersConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CarcassGeoModel<T extends GeoAnimatable> extends GeoModel<T> {

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
    private static final Set<ResourceLocation> warnedMissing = new HashSet<>();

    protected abstract String getGeoId(T animatable);

    public static boolean hasGeoModel(String id) {
        Minecraft mc = Minecraft.getInstance();
        return mc.getResourceManager()
                .getResource(new ResourceLocation(ButchersConstants.MODID, "geo/carcass/" + id + ".geo.json"))
                .isPresent();
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return new ResourceLocation(ButchersConstants.MODID, "geo/carcass/" + getGeoId(animatable) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return new ResourceLocation(ButchersConstants.MODID, "textures/block/carcass/" + getGeoId(animatable) + "/" + getTextureName(animatable) + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return new ResourceLocation(ButchersConstants.MODID, "animations/carcass/" + getGeoId(animatable) + ".json");
    }

    protected String getTextureName(T animatable) {
        return "base";
    }

    protected void applyBoneVisibility(AbstractCarcassBlock block, BlockState state, CompoundTag carcassData) {
        List<String> hidden = block.getHiddenBones(state, carcassData);
        for (String boneName : block.getToggleableBones()) {
            getBone(boneName).ifPresent(bone -> bone.setHidden(hidden.contains(boneName)));
        }
    }

    @Override
    public BakedGeoModel getBakedModel(ResourceLocation location) {
        // suppress GeckoLib's exception for missing models, fall back to vanilla model instead
        try {
            return super.getBakedModel(location);
        } catch (GeckoLibException e) {
            if (warnedMissing.add(location)) {
                LOGGER.warn("No GeckoLib model for {}, rendering vanilla", location);
            }
            return null;
        }
    }
}
