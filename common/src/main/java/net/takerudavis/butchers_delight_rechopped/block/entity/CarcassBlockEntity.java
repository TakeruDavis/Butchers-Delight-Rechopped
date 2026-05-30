package net.takerudavis.butchers_delight_rechopped.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.AbstractHookableCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.client.CarcassGeoModel;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CarcassBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static BlockEntityType<CarcassBlockEntity> TYPE;

    private CompoundTag carcassData = new CompoundTag();

    public CarcassBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
    }

    private static final RawAnimation ANIM_PLACED = RawAnimation.begin().thenLoop("placed");
    private static final RawAnimation ANIM_HOOKED = RawAnimation.begin().thenLoop("hooked");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "pose", 0, state -> {
            if (!(getBlockState().getBlock() instanceof AbstractCarcassBlock carcassBlock)
                    || !CarcassGeoModel.hasGeoModel(carcassBlock.getGeoId())) {
                return PlayState.STOP;
            }
            BlockState blockState = getBlockState();
            if (blockState.hasProperty(AbstractHookableCarcassBlock.HOOKED)
                    && blockState.getValue(AbstractHookableCarcassBlock.HOOKED)) {
                state.getController().setAnimation(ANIM_HOOKED);
            } else {
                state.getController().setAnimation(ANIM_PLACED);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public CompoundTag getCarcassData() {
        return carcassData;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("CarcassData", carcassData);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("CarcassData")) {
            this.carcassData = tag.getCompound("CarcassData").copy();
        }
    }
}
