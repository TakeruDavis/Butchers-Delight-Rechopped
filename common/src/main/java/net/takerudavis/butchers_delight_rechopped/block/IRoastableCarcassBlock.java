package net.takerudavis.butchers_delight_rechopped.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IRoastableCarcassBlock {

    //Cleaver used on a roasted carcass
    void processRoasted(Level level, BlockPos pos, BlockState state, Player player);

    // null = no outer layer to burn
    default @Nullable ProcessingStage burnOuterStage() {
        return null;
    }

    // null = no drops when outer layer gets burned
    default @Nullable ResourceLocation getBurnLootTable() {
        return null;
    }

}
