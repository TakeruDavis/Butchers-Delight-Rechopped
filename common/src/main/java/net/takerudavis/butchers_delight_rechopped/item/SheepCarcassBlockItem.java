package net.takerudavis.butchers_delight_rechopped.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.takerudavis.butchers_delight_rechopped.block.carcass.SheepCarcassBlock;

import java.util.List;

public class SheepCarcassBlockItem extends CarcassBlockItem {

    public SheepCarcassBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected void appendPriorityTooltip(CompoundTag carcassData, List<Component> tooltip) {
        if (!carcassData.contains(SheepCarcassBlock.WOOL_COLOR_KEY)) return;
        DyeColor color = DyeColor.byId(carcassData.getInt(SheepCarcassBlock.WOOL_COLOR_KEY));
        float[] diffuse = color.getTextureDiffuseColors();
        int r = (int) (diffuse[0] * 255);
        int g = (int) (diffuse[1] * 255);
        int b = (int) (diffuse[2] * 255);
        int rgb = (r << 16) | (g << 8) | b;
        tooltip.add(Component.translatable("item.butchers_delight_rechopped.sheep_carcass.wool_color",
                        Component.translatable("color.minecraft." + color.getName()))
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb))));
    }

    @Override
    protected void appendCarcassDataTooltip(CompoundTag carcassData, List<Component> tooltip) {
        CompoundTag rest = carcassData.copy();
        rest.remove(SheepCarcassBlock.WOOL_COLOR_KEY);
        super.appendCarcassDataTooltip(rest, tooltip);
    }
}
