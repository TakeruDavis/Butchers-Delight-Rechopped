package net.takerudavis.butchers_delight_rechopped;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.takerudavis.butchers_delight_rechopped.item.CleaverItem;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ButchersDelightRechopped.MODID);

    public static final RegistryObject<CleaverItem> CLEAVER = ITEMS.register(
                    "cleaver",
                    () -> new CleaverItem(Tiers.IRON, new Item.Properties())
    );

    public static final RegistryObject<BlockItem> HOOK = ITEMS.register("hook", () -> new BlockItem(ModBlocks.HOOK_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> CHICKEN_CARCASS = ITEMS.register("chicken_carcass", () -> new BlockItem(ModBlocks.CHICKEN_CARCASS.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> SHEEP_CARCASS = ITEMS.register("sheep_carcass", () -> new BlockItem(ModBlocks.SHEEP_CARCASS.get(), new Item.Properties()));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

}
