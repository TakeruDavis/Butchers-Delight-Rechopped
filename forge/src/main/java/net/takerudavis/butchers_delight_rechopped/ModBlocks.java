package net.takerudavis.butchers_delight_rechopped;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.takerudavis.butchers_delight_rechopped.block.*;
import net.takerudavis.butchers_delight_rechopped.block.entity.SheepHeadBlockEntity;
import net.takerudavis.butchers_delight_rechopped.common.ButchersConstants;
import net.takerudavis.butchers_delight_rechopped.block.carcass.*;
import net.takerudavis.butchers_delight_rechopped.block.entity.CarcassBlockEntity;
import net.takerudavis.butchers_delight_rechopped.block.entity.RoasterBlockEntity;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ButchersConstants.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ButchersConstants.MODID);

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        modEventBus.addListener((FMLCommonSetupEvent event) -> {
            CarcassBlockEntity.TYPE = CARCASS_BLOCK_ENTITY.get();
            RoasterBlockEntity.TYPE = ROASTER_BLOCK_ENTITY.get();
            SheepHeadBlockEntity.TYPE = SHEEP_HEAD_BLOCK_ENTITY.get();
        });
    }

    public static final RegistryObject<HookBlock> HOOK_BLOCK = BLOCKS.register(
            ButchersConstants.HOOK_ID,
            () -> new HookBlock(BlockBehaviour.Properties.of().noOcclusion())
    );

    public static final RegistryObject<RoasterBlock> ROASTER = BLOCKS.register(
            ButchersConstants.ROASTER_ID,
            () -> new RoasterBlock(BlockBehaviour.Properties.of().noOcclusion())
    );

    public static final RegistryObject<BlockEntityType<RoasterBlockEntity>> ROASTER_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            ButchersConstants.ROASTER_ID,
            () -> BlockEntityType.Builder.of(RoasterBlockEntity::new, ROASTER.get()).build(null)
    );

    public static <T extends AbstractCarcassBlock> RegistryObject<T> registerCarcass(
            RegistryObject<T> carcassBlock,
            EntityType<? extends LivingEntity> entityType
    ) {
        CarcassRegistry.register(carcassBlock, entityType);
        return carcassBlock;
    }

    public static final RegistryObject<ChickenCarcassBlock> CHICKEN_CARCASS = registerCarcass(
        BLOCKS.register(ButchersConstants.CHICKEN_CARCASS_ID, () -> new ChickenCarcassBlock(BlockBehaviour.Properties.of().noOcclusion())), EntityType.CHICKEN
    );

    public static final RegistryObject<SheepCarcassBlock> SHEEP_CARCASS = registerCarcass(
        BLOCKS.register(ButchersConstants.SHEEP_CARCASS_ID, () -> new SheepCarcassBlock(BlockBehaviour.Properties.of().noOcclusion())), EntityType.SHEEP
    );

    public static final RegistryObject<SheepHeadBlock> SHEEP_HEAD = BLOCKS.register(
            ButchersConstants.SHEEP_HEAD_ID, () -> new SheepHeadBlock(
                    BlockBehaviour.Properties.of().strength(1.0F).noOcclusion()
                            .instrument(NoteBlockInstrument.CUSTOM_HEAD)
            ));

    public static final RegistryObject<SheepWallHeadBlock> SHEEP_WALL_HEAD = BLOCKS.register(
            ButchersConstants.SHEEP_WALL_HEAD_ID, () -> new
                    SheepWallHeadBlock(
                            BlockBehaviour.Properties.of().strength(1.0F).noOcclusion()
                                    .instrument(NoteBlockInstrument.CUSTOM_HEAD)
            ));

    public static final RegistryObject<BlockEntityType<SheepHeadBlockEntity>> SHEEP_HEAD_BLOCK_ENTITY =
            BLOCK_ENTITIES.register(
                    ButchersConstants.SHEEP_HEAD_ID, () -> BlockEntityType.Builder.of(
                            SheepHeadBlockEntity::new, SHEEP_HEAD.get(), SHEEP_WALL_HEAD.get()).build(null));

    public static final RegistryObject<BlockEntityType<CarcassBlockEntity>> CARCASS_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            ButchersConstants.CARCASS_BLOCK_ENTITY_ID,
            () -> BlockEntityType.Builder.of(
                    CarcassBlockEntity::new,
                    CarcassRegistry.getAll().stream().toArray(AbstractCarcassBlock[]::new)
            ).build(null)
    );

}
