package net.takerudavis.butchers_delight_rechopped.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class CarcassBlockItem extends BlockItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CarcassBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    private static final RawAnimation ANIM_PLACED = RawAnimation.begin().thenLoop("placed");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "pose", 0, state -> {
            state.getController().setAnimation(ANIM_PLACED);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static CompoundTag getCarcassData(ItemStack stack) {
        CompoundTag bet = stack.getTagElement("BlockEntityTag");
        return bet != null && bet.contains("CarcassData") ? bet.getCompound("CarcassData") : new CompoundTag();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        CompoundTag bst = tag.getCompound("BlockStateTag");
        CompoundTag carcassData = getCarcassData(stack);

        appendPriorityTooltip(carcassData, tooltip);

        if (bst.contains(AbstractCarcassBlock.STAGE.getName())) {
            tooltip.add(Component.translatable("tooltip.butchers_delight_rechopped.stage."
                    + bst.getString(AbstractCarcassBlock.STAGE.getName())).withStyle(ChatFormatting.DARK_GRAY));
        }
        if (bst.contains(AbstractCarcassBlock.MEAT.getName())) {
            tooltip.add(Component.translatable("tooltip.butchers_delight_rechopped.meat."
                    + bst.getString(AbstractCarcassBlock.MEAT.getName())).withStyle(ChatFormatting.DARK_GRAY));
        }
        if ("true".equals(bst.getString(AbstractCarcassBlock.BEHEADED.getName()))) {
            tooltip.add(Component.translatable("tooltip.butchers_delight_rechopped.beheaded")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        appendCarcassDataTooltip(carcassData, tooltip);
    }

    protected void appendPriorityTooltip(CompoundTag carcassData, List<Component> tooltip) {}

    protected void appendCarcassDataTooltip(CompoundTag carcassData, List<Component> tooltip) {
        if (getBlock() instanceof AbstractCarcassBlock acb) {
            List<AbstractCarcassBlock.LimbDefinition> limbs = acb.getLimbs();
            if (!limbs.isEmpty()) {
                List<Component> removed = new ArrayList<>();
                for (int i : carcassData.getIntArray(AbstractCarcassBlock.LIMBS_REMOVED_KEY)) {
                    if (i >= 0 && i < limbs.size()) removed.add(Component.translatable(limbs.get(i).translationKey()));
                }
                if (!removed.isEmpty()) {
                    tooltip.add(Component.translatable("tooltip.butchers_delight_rechopped.removed_limbs")
                            .withStyle(ChatFormatting.DARK_GRAY));
                    for (Component label : removed) {
                        tooltip.add(Component.literal("  • ").append(label).withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
            }
        }
        for (String key : carcassData.getAllKeys()) {
            if (key.equals(AbstractCarcassBlock.LIMBS_REMOVED_KEY)) continue;
            tooltip.add(Component.literal(key + ": " + carcassData.get(key)).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
