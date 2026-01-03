package net.takerudavis.butchers_delight_rechopped;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.takerudavis.butchers_delight_rechopped.item.CleaverItem;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ButchersDelightRechopped.MODID);

    public static final DeferredItem<CleaverItem> CLEAVER = ITEMS.register(
                    "cleaver",
                    () -> new CleaverItem(
                            Tiers.IRON,
                            new Item.Properties().attributes(
                                    AxeItem.createAttributes(Tiers.IRON, 6.0F, -3.1F)
                            )
                    )
    );

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

}
