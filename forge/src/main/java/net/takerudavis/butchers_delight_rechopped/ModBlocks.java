package net.takerudavis.butchers_delight_rechopped;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.block.HookBlock;
import net.takerudavis.butchers_delight_rechopped.block.carcass.*;
import net.takerudavis.butchers_delight_rechopped.block.entity.CarcassBlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ButchersDelightRechopped.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ButchersDelightRechopped.MODID);

    public static final List<RegistryObject<? extends AbstractCarcassBlock>> CARCASS_BLOCKS = new ArrayList<>();

    private static final Map<EntityType<? extends LivingEntity>, RegistryObject<? extends AbstractCarcassBlock>> ENTITY_2_CARCASS = new HashMap<>();

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        modEventBus.addListener((FMLCommonSetupEvent event) -> CarcassBlockEntity.TYPE = CARCASS_BLOCK_ENTITY.get());
    }

    public static final RegistryObject<HookBlock> HOOK_BLOCK = BLOCKS.register(
            "hook",
            () -> new HookBlock(BlockBehaviour.Properties.of().noOcclusion())
    );

    public static <T extends AbstractCarcassBlock> RegistryObject<T> registerCarcass(
            RegistryObject<T> carcassBlock,
        EntityType<? extends LivingEntity> entityType
    ) {
        CARCASS_BLOCKS.add(carcassBlock);
        ENTITY_2_CARCASS.put(entityType, carcassBlock);
        return carcassBlock;
    }

    public static RegistryObject<? extends AbstractCarcassBlock> getCarcassBlockForEntity(EntityType<?> entityType) {
        return ENTITY_2_CARCASS.get(entityType);
    }

    public static final RegistryObject<ChickenCarcassBlock> CHICKEN_CARCASS = registerCarcass(
        BLOCKS.register("chicken_carcass", () -> new ChickenCarcassBlock(BlockBehaviour.Properties.of())), EntityType.CHICKEN
    );

    public static final RegistryObject<SheepCarcassBlock> SHEEP_CARCASS = registerCarcass(
        BLOCKS.register("sheep_carcass", () -> new SheepCarcassBlock(BlockBehaviour.Properties.of())), EntityType.SHEEP
    );

    public static final RegistryObject<BlockEntityType<CarcassBlockEntity>> CARCASS_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "carcass",
            () -> BlockEntityType.Builder.of(
                    CarcassBlockEntity::new,
                    ModBlocks.CARCASS_BLOCKS.stream().map(RegistryObject::get).toArray(Block[]::new)
            ).build(null)
    );

}
