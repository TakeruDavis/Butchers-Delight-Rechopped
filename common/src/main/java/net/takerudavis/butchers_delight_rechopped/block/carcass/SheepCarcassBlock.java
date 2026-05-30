package net.takerudavis.butchers_delight_rechopped.block.carcass;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.takerudavis.butchers_delight_rechopped.block.*;
import net.takerudavis.butchers_delight_rechopped.block.entity.RoasterBlockEntity;
import net.takerudavis.butchers_delight_rechopped.client.SheepWoolItemRenderLayer;
import net.takerudavis.butchers_delight_rechopped.client.SheepWoolRenderLayer;
import net.takerudavis.butchers_delight_rechopped.client.SheepWoolRoasterRenderLayer;
import net.takerudavis.butchers_delight_rechopped.common.ButchersConstants;
import net.takerudavis.butchers_delight_rechopped.block.entity.CarcassBlockEntity;
import net.takerudavis.butchers_delight_rechopped.block.entity.SheepHeadBlockEntity;
import net.takerudavis.butchers_delight_rechopped.item.CarcassBlockItem;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SheepCarcassBlock extends AbstractHookableCarcassBlock implements IRoastableCarcassBlock {

    public static final String WOOL_COLOR_KEY = "WoolColor";
    public static final List<String> WOOL_BONES =
            List.of("wool_body", "wool_head", "wool_leg0", "wool_leg1", "wool_leg2", "wool_leg3");

    public SheepCarcassBlock(Properties properties) {
        super("sheep", properties);
    }

    @Override
    public List<LimbDefinition> getLimbs() {
        return List.of(
                new LimbDefinition("leg0", "tooltip.butchers_delight_rechopped.sheep_carcass.limb.leg0"),
                new LimbDefinition("leg1", "tooltip.butchers_delight_rechopped.sheep_carcass.limb.leg1"),
                new LimbDefinition("leg2", "tooltip.butchers_delight_rechopped.sheep_carcass.limb.leg2"),
                new LimbDefinition("leg3", "tooltip.butchers_delight_rechopped.sheep_carcass.limb.leg3")
        );
    }

    @Override
    public ProcessingStage getInitialStage(LivingEntity entity) {
        return entity instanceof Sheep sheep && sheep.isSheared()
                ? ProcessingStage.SHEARED
                : ProcessingStage.INTACT;
    }

    @Override
    public CompoundTag extractAnimalData(LivingEntity entity) {
        if (entity instanceof Sheep sheep) {
            if (sheep.isBaby()) return null;
            CompoundTag data = new CompoundTag();
            data.putInt(WOOL_COLOR_KEY, sheep.getColor().getId());
            return data;
        }
        return null;
    }

    @Override
    public List<ItemStack> getHeadDrops(CompoundTag carcassData) {
        Item head = BuiltInRegistries.ITEM.get(
                new ResourceLocation(ButchersConstants.MODID, ButchersConstants.SHEEP_HEAD_ID)
        );
        ItemStack stack = new ItemStack(head);
        stack.getOrCreateTag().put("SkullOwner",
                NbtUtils.writeGameProfile(new CompoundTag(), SheepHeadBlockEntity.KOLISH_PROFILE));
        return List.of(stack);
    }

    @Override
    public void processBasic(Level level, BlockPos pos, BlockState state, Player player) {
        evaluateLootTable(new ResourceLocation(ButchersConstants.MODID,
                "gameplay/sheep_carcass/process_basic"), level, pos, player, null, state);
        level.removeBlock(pos, false);
    }

    @Override
    public void processHooked(Level level, BlockPos pos, BlockState state, Player player) {
        if (state.getValue(STAGE) == ProcessingStage.BUTCHERED) {
            evaluateLootTable(new ResourceLocation(ButchersConstants.MODID, "gameplay/sheep_carcass/spine"), level, pos, player, null, state);
            level.removeBlock(pos, false);
            return;
        }

        if (!(level.getBlockEntity(pos) instanceof CarcassBlockEntity be)) return;
        CompoundTag carcassData = be.getCarcassData();

        List<LimbDefinition> limbs = getLimbs();
        for (int i = 0; i < limbs.size(); i++) {
            if (!isLimbRemoved(carcassData, i)) {
                markLimbRemoved(carcassData, i);
                be.setChanged();

                evaluateLootTable(new ResourceLocation(ButchersConstants.MODID,
                        "gameplay/sheep_carcass/limb_leg"), level, pos, player, null, state);
                BlockState newState = state.setValue(STAGE, ProcessingStage.DELIMBED);
                level.setBlock(pos, newState, Block.UPDATE_ALL);
                level.sendBlockUpdated(pos, newState, newState, Block.UPDATE_CLIENTS);
                return;
            }
        }

        evaluateLootTable(new ResourceLocation(ButchersConstants.MODID,
                "gameplay/sheep_carcass/body"), level, pos, player, null, state);
        level.setBlock(pos, state.setValue(STAGE, ProcessingStage.BUTCHERED), Block.UPDATE_ALL);
    }

    @Override
    public void processRoasted(Level level, BlockPos pos, BlockState state, Player player) {
        evaluateLootTable(new ResourceLocation(ButchersConstants.MODID,
                "gameplay/sheep_carcass/roaster"), level, pos, player, null, state);
    }

    @Override
    public boolean applyDye(DyeColor color, CompoundTag blockStateTag, CompoundTag carcassData) {
        if (!ProcessingStage.INTACT.getSerializedName().equals(
                blockStateTag.getString(AbstractCarcassBlock.STAGE.getName()))) return false;
        carcassData.putInt(WOOL_COLOR_KEY, color.getId());
        return true;
    }

    @Override
    public void attachBlockRenderLayers(GeoBlockRenderer<CarcassBlockEntity> renderer) {
        renderer.addRenderLayer(new SheepWoolRenderLayer(renderer));
    }

    @Override
    public void attachItemRenderLayers(GeoItemRenderer<CarcassBlockItem> renderer) {
        renderer.addRenderLayer(new SheepWoolItemRenderLayer(renderer));
    }

    @Override
    public void attachRoasterRenderLayers(GeoBlockRenderer<RoasterBlockEntity> renderer) {
        renderer.addRenderLayer(new SheepWoolRoasterRenderLayer(renderer));
    }

    @Override
    public ProcessingStage getShearStage() {
        return ProcessingStage.SHEARED;
    }

    @Override
    public ProcessingStage burnOuterStage() {
        return ProcessingStage.SHEARED;
    }

    @Override
    public ResourceLocation getBurnLootTable() {
        return new ResourceLocation(
                ButchersConstants.MODID,
                "gameplay/sheep_carcass/burn_outer"
        );
    }

    @Override
    public List<ItemStack> getShearDrops(CompoundTag carcassData, net.minecraft.util.RandomSource random) {
        DyeColor color = DyeColor.byId(carcassData.getInt(WOOL_COLOR_KEY));
        Block wool = BuiltInRegistries.BLOCK.get(new ResourceLocation(color.getName() + "_wool"));
        return List.of(new ItemStack(wool, 1 + random.nextInt(3)));
    }

    @Override
    public List<String> getToggleableBones() {
        List<String> bones = new ArrayList<>(List.of("spine", "head", "body"));
        getLimbs().forEach(l -> bones.add(l.boneId()));
        bones.addAll(WOOL_BONES);
        return bones;
    }

    @Override
    public List<String> getHiddenBones(BlockState state, CompoundTag carcassData) {
        List<String> hiddenBones = new ArrayList<>();

        if (state.getValue(STAGE) != ProcessingStage.BUTCHERED) {
            hiddenBones.add("spine");
        } else {
            hiddenBones.add("body");
        }

        List<LimbDefinition> limbs = getLimbs();
        for (int i : carcassData.getIntArray(LIMBS_REMOVED_KEY)) {
            if (i >= 0 && i < limbs.size()) hiddenBones.add(limbs.get(i).boneId());
        }

        // Always hidden from main pass — SheepWoolRenderLayer renders them with the correct fur texture
        hiddenBones.addAll(WOOL_BONES);

        if (state.getValue(BEHEADED)) {
            hiddenBones.add("head");
        }

        return hiddenBones;
    }

    @Override
    public String getTextureName(BlockState state) {
        return state.getValue(AbstractCarcassBlock.MEAT) == MeatState.COOKED
                ? "skinned" : "base"; // Temporary until we have proper cooked textures
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof DyeItem dyeItem && state.getValue(STAGE) == ProcessingStage.INTACT) {
            if (level.getBlockEntity(pos) instanceof CarcassBlockEntity be) {
                CompoundTag data = be.getCarcassData();
                data.putInt(WOOL_COLOR_KEY, dyeItem.getDyeColor().getId());
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                if (!player.isCreative()) stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }
}
