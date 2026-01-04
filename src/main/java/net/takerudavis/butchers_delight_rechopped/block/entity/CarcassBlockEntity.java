package net.takerudavis.butchers_delight_rechopped.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.takerudavis.butchers_delight_rechopped.ModBlocks;
import org.jetbrains.annotations.Nullable;

public class CarcassBlockEntity extends BlockEntity {

    private CompoundTag carcassData = new CompoundTag();

    public CarcassBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.CARCASS_BLOCK_ENTITY.get(), pos, blockState);
    }

    public CompoundTag getCarcassData() {
        return carcassData;
    }

    public void setCarcassData(CompoundTag carcassData) {
        this.carcassData = carcassData;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.put("CarcassData", carcassData);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("CarcassData", carcassData);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("CarcassData")) {
            this.carcassData = tag.getCompound("CarcassData");
        }
    }
}
