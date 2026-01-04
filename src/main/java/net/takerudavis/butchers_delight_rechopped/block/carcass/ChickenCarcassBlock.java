package net.takerudavis.butchers_delight_rechopped.block.carcass;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;

public class ChickenCarcassBlock extends AbstractCarcassBlock {

    public ChickenCarcassBlock(Properties properties) {
        super(properties);
    }

    @Override
    public CompoundTag extractAnimalData(LivingEntity entity) {
        if (entity instanceof net.minecraft.world.entity.animal.Chicken chicken) {
            CompoundTag data = new CompoundTag();
            // Extract normal/cold/warm variant
            data.putBoolean("Variant", chicken.isBaby());
            return data;
        }
        return null;
    }

    @Override
    public void processBasic(Level level, BlockPos pos, BlockState state, Player player) {
        // Implementation for processing a carcass with a cleaver when placed on a block
        // This involves changing the block state to a different processing stage,
        // dropping items, playing sounds, etc.
    }

}
