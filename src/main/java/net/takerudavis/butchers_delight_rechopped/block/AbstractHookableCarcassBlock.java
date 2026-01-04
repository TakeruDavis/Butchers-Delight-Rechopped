package net.takerudavis.butchers_delight_rechopped.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

abstract public class AbstractHookableCarcassBlock extends AbstractCarcassBlock {

    public static final BooleanProperty HOOKED = BooleanProperty.create("hooked");

    public AbstractHookableCarcassBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull BlockState makeDefaultProperties() {
        return super.makeDefaultProperties()
                .setValue(HOOKED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HOOKED);
    }

    //Cleaver used on a hooked carcass
    abstract public void processHooked(Level level, BlockPos pos, BlockState state, Player player);

}
