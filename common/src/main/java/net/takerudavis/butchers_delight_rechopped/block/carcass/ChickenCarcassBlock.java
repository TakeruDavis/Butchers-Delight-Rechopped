package net.takerudavis.butchers_delight_rechopped.block.carcass;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.takerudavis.butchers_delight_rechopped.common.ButchersConstants;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.MeatState;
import net.takerudavis.butchers_delight_rechopped.block.ProcessingStage;

import java.util.List;

public class ChickenCarcassBlock extends AbstractCarcassBlock {

    public ChickenCarcassBlock(Properties properties) {
        super("chicken", properties);
    }

    @Override
    public List<LimbDefinition> getLimbs() {
        // Currently unused since the chicken carcass doesn't have beheading or limb harvesting stages, but planned for later.
        return List.of(
                new LimbDefinition("wing_left", "Left Wing"),
                new LimbDefinition("wing_right", "Right Wing"),
                new LimbDefinition("leg_left", "Left Leg"),
                new LimbDefinition("leg_right", "Right Leg")
        );
    }

    @Override
    public CompoundTag extractAnimalData(LivingEntity entity) {
        if (entity instanceof Chicken chicken) {
            if (chicken.isBaby()) return null;
            CompoundTag data = new CompoundTag();
            data.putInt("Variant", 0); // for 1.21+; chickens don't have variants yet in 1.20.1
            return data;
        }
        return null;
    }

    @Override
    public boolean isBeheadable() {
        return false;
    }

    @Override
    public ProcessingStage getShearStage() {
        return ProcessingStage.SHEARED;
    }

    @Override
    public ResourceLocation getShearLootTable() {
        return new ResourceLocation(ButchersConstants.MODID, "gameplay/chicken_carcass/shear");
    }

    @Override
    public void processBasic(Level level, BlockPos pos, BlockState state, Player player) {
        evaluateLootTable(new ResourceLocation(ButchersConstants.MODID,
                "gameplay/chicken_carcass/process_basic"), level, pos, player, null, state);
        level.removeBlock(pos, false);
    }
}
