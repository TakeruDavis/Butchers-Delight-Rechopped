package net.takerudavis.butchers_delight_rechopped.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IRoastableCarcassBlock {

    //Cleaver used on a roasted carcass
    void processRoasted(Level level, BlockPos pos, BlockState state, Player player);

}
