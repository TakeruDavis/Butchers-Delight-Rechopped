package net.takerudavis.butchers_delight_rechopped.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.takerudavis.butchers_delight_rechopped.block.entity.CarcassBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class AbstractCarcassBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    public static final EnumProperty<ProcessingStage> STAGE = EnumProperty.create("stage", ProcessingStage.class);

    public static final BooleanProperty BEHEADED = BooleanProperty.create("beheaded");

    public static final EnumProperty<MeatState> MEAT = EnumProperty.create("meat", MeatState.class);

    public AbstractCarcassBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(makeDefaultProperties());
    }

    protected @NotNull BlockState makeDefaultProperties() {
        return this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(STAGE, ProcessingStage.INTACT)
                .setValue(BEHEADED, false)
                .setValue(MEAT, MeatState.FRESH);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, STAGE, BEHEADED, MEAT);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CarcassBlockEntity(blockPos, blockState);
    }

    public ProcessingStage getInitialStage(LivingEntity entity) {
        return ProcessingStage.INTACT;
    }

    abstract public CompoundTag extractAnimalData(LivingEntity entity);

    //Cleaver used on a carcass placed on a block
    abstract public void processBasic(Level level, BlockPos pos, BlockState state, Player player);

}
