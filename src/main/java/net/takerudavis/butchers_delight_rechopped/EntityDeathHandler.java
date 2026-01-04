package net.takerudavis.butchers_delight_rechopped;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.item.CleaverItem;

@EventBusSubscriber(modid = ButchersDelightRechopped.MODID)
public class EntityDeathHandler {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker)) {
            return; // Only proceed if killed by a living entity
        }

        ItemStack weapon = attacker.getMainHandItem();
        if (!(weapon.getItem() instanceof CleaverItem)) {
            return; // Only proceed if killed with a cleaver
        }

        DeferredBlock<? extends AbstractCarcassBlock> carcassBlockDeferred = ModBlocks.getCarcassBlockForEntity(entity.getType());

        if (carcassBlockDeferred == null) {
            return;
        }

        AbstractCarcassBlock abstractCarcassBlock = carcassBlockDeferred.get();

        ItemStack carcassStack = new ItemStack(abstractCarcassBlock.asItem());

        CompoundTag carcassData = abstractCarcassBlock.extractAnimalData(entity);
        if (carcassData != null) {
            CompoundTag blockEntityTag = new CompoundTag();
            blockEntityTag.put("CarcassData", carcassData);
            carcassStack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntityTag));
        }

        carcassStack.set(
                DataComponents.BLOCK_STATE,
                BlockItemStateProperties.EMPTY
                        .with(AbstractCarcassBlock.STAGE, abstractCarcassBlock.getInitialStage(entity))
        );

        //get rid of normal drops and replace with carcass
        event.getDrops().clear();
        ItemEntity itemEntity = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), carcassStack);
        event.getDrops().add(itemEntity);
    }

}
