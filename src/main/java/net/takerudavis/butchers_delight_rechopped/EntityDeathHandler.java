package net.takerudavis.butchers_delight_rechopped;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import net.takerudavis.butchers_delight_rechopped.block.AbstractCarcassBlock;
import net.takerudavis.butchers_delight_rechopped.item.CleaverItem;

@Mod.EventBusSubscriber(modid = ButchersDelightRechopped.MODID)
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

        RegistryObject<? extends AbstractCarcassBlock> carcassBlockDeferred = ModBlocks.getCarcassBlockForEntity(entity.getType());

        if (carcassBlockDeferred == null) {
            return;
        }

        AbstractCarcassBlock abstractCarcassBlock = carcassBlockDeferred.get();

        ItemStack carcassStack = new ItemStack(abstractCarcassBlock.asItem());

        CompoundTag carcassData = abstractCarcassBlock.extractAnimalData(entity);
        if (carcassData != null) {
            CompoundTag blockEntityTag = new CompoundTag();
            blockEntityTag.put("CarcassData", carcassData);
            BlockItem.setBlockEntityData(
                    carcassStack,
                    ModBlocks.CARCASS_BLOCK_ENTITY.get(),
                    blockEntityTag
            );
        }

        CompoundTag blockStateTag = carcassStack.getOrCreateTagElement("BlockStateTag");
        blockStateTag.putString(AbstractCarcassBlock.STAGE.getName(), abstractCarcassBlock.getInitialStage(entity).getSerializedName());

        //get rid of normal drops and replace with carcass
        event.getDrops().clear();
        ItemEntity itemEntity = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), carcassStack);
        event.getDrops().add(itemEntity);
    }

}
