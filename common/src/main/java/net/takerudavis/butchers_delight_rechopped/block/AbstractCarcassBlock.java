package net.takerudavis.butchers_delight_rechopped.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.takerudavis.butchers_delight_rechopped.block.entity.CarcassBlockEntity;
import net.takerudavis.butchers_delight_rechopped.block.entity.RoasterBlockEntity;
import net.takerudavis.butchers_delight_rechopped.common.CommonSounds;
import net.takerudavis.butchers_delight_rechopped.item.CarcassBlockItem;
import net.takerudavis.butchers_delight_rechopped.item.CleaverItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

abstract public class AbstractCarcassBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    public static final EnumProperty<ProcessingStage> STAGE = EnumProperty.create("stage", ProcessingStage.class);

    public static final BooleanProperty BEHEADED = BooleanProperty.create("beheaded");

    public static final EnumProperty<MeatState> MEAT = EnumProperty.create("meat", MeatState.class);

    public static final String LIMBS_REMOVED_KEY = "limbs_removed";

    public record LimbDefinition(String boneId, String translationKey) {}

    private final String geoId;

    public AbstractCarcassBlock(String geoId, Properties properties) {
        super(properties);
        this.geoId = geoId;
        this.registerDefaultState(makeDefaultProperties());
    }

    public String getGeoId() {
        return geoId;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) return null;
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (!requiresGroundSupport(state)) return true;
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    // Hooked carcasses override this to return false — they're supported by the hook above.
    protected boolean requiresGroundSupport(BlockState state) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide() || !requiresGroundSupport(state)) return;
        if (fromPos.equals(pos.below()) && !canSurvive(state, level, pos)) {
            dropAsItem(level, pos, state);
        }
    }

    protected void dropAsItem(Level level, BlockPos pos, BlockState state) {
        ItemStack drop = getCloneItemStack(level, pos, state);
        if (level.getBlockEntity(pos) instanceof CarcassBlockEntity be) {
            CompoundTag beData = be.saveWithoutMetadata();
            if (!beData.isEmpty()) drop.addTagElement("BlockEntityTag", beData);
        }
        level.removeBlock(pos, false);
        Block.popResource(level, pos, drop);
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
    public RenderShape getRenderShape(BlockState state) {
        return net.takerudavis.butchers_delight_rechopped.client.CarcassGeoModel.hasGeoModel(geoId)
                ? RenderShape.ENTITYBLOCK_ANIMATED
                : RenderShape.MODEL;
    }

    public String getTextureName(BlockState state) {
        return "base";
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CarcassBlockEntity(blockPos, blockState);
    }

    public ProcessingStage getInitialStage(LivingEntity entity) {
        return ProcessingStage.INTACT;
    }

    abstract public CompoundTag extractAnimalData(LivingEntity entity);

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.getItem() instanceof CleaverItem) {
            if (isBeheadable() && !state.getValue(BEHEADED)) {
                if (!level.isClientSide()) {
                    level.playSound(null, pos, CommonSounds.CLEAVER_CHOP, SoundSource.BLOCKS, 1.0f, 0.5f);
                    level.setBlock(pos, state.setValue(BEHEADED, true), Block.UPDATE_ALL);

                    CompoundTag carcassData = level.getBlockEntity(pos) instanceof CarcassBlockEntity be
                            ? be.getCarcassData() : new CompoundTag();
                    for (ItemStack drop : getHeadDrops(carcassData)) {
                        Block.popResource(level, pos, drop);
                    }
                    heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }

                return InteractionResult.sidedSuccess(level.isClientSide());
            } else {
                if (getShearStage() != null && state.getValue(STAGE) == ProcessingStage.INTACT) {
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
                if (!level.isClientSide()) {
                    level.playSound(null, pos, CommonSounds.CLEAVER_CHOP, SoundSource.BLOCKS, 1.0f, 0.3f);
                    processBasic(level, pos, state, player);
                    heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        if (heldItem.isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack drop = getCloneItemStack(level, pos, state);
                level.removeBlock(pos, false);
                player.getInventory().add(drop);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        ProcessingStage shearStage = getShearStage();
        if (heldItem.is(Items.SHEARS) && shearStage != null) {
            if (state.getValue(STAGE) == ProcessingStage.INTACT && !level.isClientSide()) {
                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.NEUTRAL, 1.0f, 1.0f);
                level.setBlock(pos, state.setValue(STAGE, shearStage), Block.UPDATE_ALL);
                CompoundTag carcassData = level.getBlockEntity(pos) instanceof CarcassBlockEntity be
                        ? be.getCarcassData() : new CompoundTag();
                for (ItemStack drop : getShearDrops(carcassData, level.random)) {
                    Block.popResource(level, pos, drop);
                }
                evaluateLootTable(getShearLootTable(), level, pos, player, heldItem, state);
                heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    public boolean isBeheadable() {
        return true;
    }

    public List<ItemStack> getHeadDrops(CompoundTag carcassData) {
        return List.of();
    }

    public List<ItemStack> getShearDrops(CompoundTag carcassData, net.minecraft.util.RandomSource random) {
        return List.of();
    }

    @Nullable
    public ProcessingStage getShearStage() {
        return null;
    }

    @Nullable
    public ResourceLocation getShearLootTable() {
        return null;
    }

    public void attachBlockRenderLayers(GeoBlockRenderer<CarcassBlockEntity> renderer) {}
    public void attachItemRenderLayers(GeoItemRenderer<CarcassBlockItem> renderer) {}
    public void attachRoasterRenderLayers(GeoBlockRenderer<RoasterBlockEntity> renderer) {}

    public boolean applyDye(DyeColor color, CompoundTag blockStateTag, CompoundTag carcassData) {
        return false;
    }

    public static final LootContextParamSet CARCASS_PROCESSING = new LootContextParamSet.Builder()
            .required(LootContextParams.ORIGIN)
            .optional(LootContextParams.THIS_ENTITY)
            .optional(LootContextParams.TOOL)
            .optional(LootContextParams.BLOCK_STATE)
            .build();

    public void evaluateLootTable(@Nullable ResourceLocation location, Level level, BlockPos pos,
                                  @Nullable Player player, @Nullable ItemStack tool, @Nullable BlockState blockState) {
        if (location == null) return;
        LootTable table = ((ServerLevel) level).getServer().getLootData().getLootTable(location);
        LootParams params = new LootParams.Builder((ServerLevel) level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                .withOptionalParameter(LootContextParams.TOOL, tool)
                .withOptionalParameter(LootContextParams.BLOCK_STATE, blockState)
                .create(CARCASS_PROCESSING);
        table.getRandomItems(params).forEach(drop -> Block.popResource(level, pos, drop));
    }

    abstract public List<LimbDefinition> getLimbs();

    public static boolean isLimbRemoved(CompoundTag carcassData, int index) {
        return IntStream.of(carcassData.getIntArray(LIMBS_REMOVED_KEY)).anyMatch(n -> n == index);
    }

    public static void markLimbRemoved(CompoundTag carcassData, int index) {
        int[] current = carcassData.getIntArray(LIMBS_REMOVED_KEY);
        int[] updated = Arrays.copyOf(current, current.length + 1);
        updated[current.length] = index;
        carcassData.putIntArray(LIMBS_REMOVED_KEY, updated);
    }

    public List<String> getToggleableBones() {
        return List.of();
    }

    public List<String> getHiddenBones(BlockState state, CompoundTag carcassData) {
        return List.of();
    }

    //Cleaver used on a carcass placed on a block
    abstract public void processBasic(Level level, BlockPos pos, BlockState state, Player player);

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        CompoundTag blockStateTag = new CompoundTag();
        for (Property<?> property : state.getProperties()) {
            if (property == FACING) continue;
            if (property.getName().equals("hooked")) continue;
            writeProperty(blockStateTag, property, state);
        }
        if (!blockStateTag.isEmpty()) {
            stack.addTagElement("BlockStateTag", blockStateTag);
        }
        if (level.getBlockEntity(pos) instanceof CarcassBlockEntity be) {
            CompoundTag beData = be.saveWithoutMetadata();
            if (!beData.isEmpty()) stack.addTagElement("BlockEntityTag", beData);
        }
        return stack;
    }

    // write a single blockstate property into NBT for storage in item form, items don't support the same values as blockstates
    private static <T extends Comparable<T>> void writeProperty(CompoundTag tag, Property<T> property, BlockState state) {
        tag.putString(property.getName(), property.getName(state.getValue(property)));
    }

    public static BlockState applyBlockStateTag(BlockState state, @Nullable CompoundTag tag) {
        if (tag == null || tag.isEmpty()) return state;
        StateDefinition<Block, BlockState> def = state.getBlock().getStateDefinition();
        for (String key : tag.getAllKeys()) {
            Property<?> property = def.getProperty(key);
            if (property != null) {
                state = applyProperty(state, property, tag.getString(key));
            }
        }
        return state;
    }

    // Unify Property wildcard into T so setValue type-checks
    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(v -> state.setValue(property, v)).orElse(state);
    }

}
