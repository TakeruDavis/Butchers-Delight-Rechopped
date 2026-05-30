package net.takerudavis.butchers_delight_rechopped.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.IRoastableCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.MeatState;
import net.takerudavis.butchers_delight_rechopped.block.ProcessingStage;
import net.takerudavis.butchers_delight_rechopped.client.CarcassGeoModel;
import net.takerudavis.butchers_delight_rechopped.common.CommonSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RoasterBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static BlockEntityType<RoasterBlockEntity> TYPE;

    public static final int COOK_TIME = 200;

    private ItemStack carcass = ItemStack.EMPTY;
    private int cookingProgress = 0;

    public RoasterBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
    }

    private static final RawAnimation ANIM_ROASTED = RawAnimation.begin().thenLoop("roasted");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "pose", 0, state -> {
            if (!hasCarcass()) return PlayState.STOP;
            if (!(carcass.getItem() instanceof BlockItem bi)
                    || !(bi.getBlock() instanceof AbstractCarcassBlock carcassBlock)
                    || !CarcassGeoModel.hasGeoModel(carcassBlock.getGeoId())) {
                return PlayState.STOP;
            }
            state.getController().setAnimation(ANIM_ROASTED);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public ItemStack getCarcass() {
        return carcass;
    }

    public void setCarcass(ItemStack stack) {
        carcass = stack.copy();
        cookingProgress = 0;
        setChanged();
    }

    public ItemStack removeCarcass() {
        ItemStack result = carcass;
        carcass = ItemStack.EMPTY;
        cookingProgress = 0;
        setChanged();
        return result;
    }

    public boolean hasCarcass() {
        return !carcass.isEmpty();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RoasterBlockEntity be) {
        if (!be.hasCarcass()) return;

        BlockState below = level.getBlockState(pos.below());
        boolean hasHeat = (below.is(Blocks.CAMPFIRE) || below.is(Blocks.SOUL_CAMPFIRE))
                && below.getValue(CampfireBlock.LIT);

        if (!hasHeat) return;

        boolean soulFire = below.is(Blocks.SOUL_CAMPFIRE);

        CompoundTag blockStateTag = be.carcass.getOrCreateTagElement("BlockStateTag");

        int effectiveCookTime = soulFire ? (int)(COOK_TIME / 1.25f) : COOK_TIME;

        if (be.carcass.getItem() instanceof BlockItem bi && bi.getBlock() instanceof IRoastableCarcassBlock roastable) {
            ProcessingStage burnTarget = roastable.burnOuterStage();
            if (burnTarget != null) {
                String stage = blockStateTag.getString(AbstractCarcassBlock.STAGE.getName());
                if (stage.isEmpty() || stage.equals(ProcessingStage.INTACT.getSerializedName())) {
                    be.cookingProgress++;
                    if (be.cookingProgress >= effectiveCookTime) {
                        be.cookingProgress = 0;
                        blockStateTag.putString(AbstractCarcassBlock.STAGE.getName(), burnTarget.getSerializedName());
                        level.playSound(null, pos, CommonSounds.ROASTER_SINGE, SoundSource.BLOCKS, 0.5f, 0.25f);

                        ResourceLocation burnLoot = roastable.getBurnLootTable();
                        if (burnLoot != null) {
                            LootTable table = ((ServerLevel) level).getServer().getLootData().getLootTable(burnLoot);
                            LootParams params = new LootParams.Builder((ServerLevel) level)
                                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                                    .create(AbstractCarcassBlock.CARCASS_PROCESSING);
                            table.getRandomItems(params).forEach(drop -> Block.popResource(level, pos, drop));
                        }

                        be.setChanged();
                        level.sendBlockUpdated(pos, state, state, 3);
                    }
                    return;
                }
            }
        }

        if (MeatState.COOKED.getSerializedName().equals(blockStateTag.getString(AbstractCarcassBlock.MEAT.getName()))) {
            return;
        }

        be.cookingProgress++;
        if (be.cookingProgress >= effectiveCookTime) {
            be.cookingProgress = 0;
            blockStateTag.putString(AbstractCarcassBlock.MEAT.getName(), MeatState.COOKED.getSerializedName());
            level.playSound(null, pos, CommonSounds.ROASTER_SIZZLE, SoundSource.BLOCKS, 1.0f, 1.5f);
            be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!carcass.isEmpty()) {
            tag.put("Carcass", carcass.save(new CompoundTag()));
        }
        tag.putInt("CookingProgress", cookingProgress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        carcass = tag.contains("Carcass") ? ItemStack.of(tag.getCompound("Carcass")) : ItemStack.EMPTY;
        cookingProgress = tag.getInt("CookingProgress");
    }
}
