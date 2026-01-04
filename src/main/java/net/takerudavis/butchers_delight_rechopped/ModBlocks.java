package net.takerudavis.butchers_delight_rechopped;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.HookBlock;
import net.takerudavis.butchers_delight_rechopped.block.carcass.*;
import net.takerudavis.butchers_delight_rechopped.block.entity.CarcassBlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ButchersDelightRechopped.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ButchersDelightRechopped.MODID);

    public static final List<DeferredBlock<? extends AbstractCarcassBlock>> CARCASS_BLOCKS = new ArrayList<>();

    private static final Map<EntityType<? extends LivingEntity>, DeferredBlock<? extends AbstractCarcassBlock>> ENTITY_2_CARCASS = new HashMap<>();

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
    }

    public static final DeferredBlock<HookBlock> HOOK_BLOCK = BLOCKS.register(
            "hook",
            () -> new HookBlock(BlockBehaviour.Properties.of().noOcclusion())
    );

    public static <T extends AbstractCarcassBlock> DeferredBlock<T> registerCarcass(
        DeferredBlock<T> carcassBlock,
        EntityType<? extends LivingEntity> entityType
    ) {
        CARCASS_BLOCKS.add(carcassBlock);
        ENTITY_2_CARCASS.put(entityType, carcassBlock);
        return carcassBlock;
    }

    public static DeferredBlock<? extends AbstractCarcassBlock> getCarcassBlockForEntity(EntityType<?> entityType) {
        return ENTITY_2_CARCASS.get(entityType);
    }

    public static final DeferredBlock<ChickenCarcassBlock> CHICKEN_CARCASS = registerCarcass(
        BLOCKS.register("chicken_carcass", () -> new ChickenCarcassBlock(BlockBehaviour.Properties.of())), EntityType.CHICKEN
    );

    public static final DeferredBlock<SheepCarcassBlock> SHEEP_CARCASS = registerCarcass(
        BLOCKS.register("sheep_carcass", () -> new SheepCarcassBlock(BlockBehaviour.Properties.of())), EntityType.SHEEP
    );

    public static final Supplier<BlockEntityType<CarcassBlockEntity>> CARCASS_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "carcass",
            () -> BlockEntityType.Builder.of(
                    CarcassBlockEntity::new,
                    ModBlocks.CARCASS_BLOCKS.stream().map(DeferredBlock::get).toArray(Block[]::new)
            ).build(null)
    );

}
