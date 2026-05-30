package net.takerudavis.butchers_delight_rechopped;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.takerudavis.butchers_delight_rechopped.block.entity.SheepHeadBlockEntity;
import net.takerudavis.butchers_delight_rechopped.common.ButchersConstants;
import net.takerudavis.butchers_delight_rechopped.client.CarcassItemRenderer;
import net.takerudavis.butchers_delight_rechopped.item.CarcassBlockItem;
import net.takerudavis.butchers_delight_rechopped.item.CleaverItem;
import net.takerudavis.butchers_delight_rechopped.item.RoasterBlockItem;
import net.takerudavis.butchers_delight_rechopped.item.SheepCarcassBlockItem;

import java.util.function.Consumer;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ButchersConstants.MODID);

    public static final RegistryObject<CleaverItem> CLEAVER = ITEMS.register(
                    ButchersConstants.CLEAVER_ID,
                    () -> new CleaverItem(Tiers.IRON, new Item.Properties())
    );

    public static final RegistryObject<BlockItem> HOOK = ITEMS.register(ButchersConstants.HOOK_ID, () -> new BlockItem(ModBlocks.HOOK_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> ROASTER = ITEMS.register(ButchersConstants.ROASTER_ID, () -> new RoasterBlockItem(ModBlocks.ROASTER.get(), new Item.Properties()));

    public static final RegistryObject<CarcassBlockItem> CHICKEN_CARCASS = ITEMS.register(ButchersConstants.CHICKEN_CARCASS_ID, () -> new CarcassBlockItem(ModBlocks.CHICKEN_CARCASS.get(), new Item.Properties()) {
        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer) {
            consumer.accept(newCarcassExtensions());
        }
    });

    public static final RegistryObject<SheepCarcassBlockItem> SHEEP_CARCASS = ITEMS.register(ButchersConstants.SHEEP_CARCASS_ID, () -> new SheepCarcassBlockItem(ModBlocks.SHEEP_CARCASS.get(), new Item.Properties()) {
        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer) {
            consumer.accept(newCarcassExtensions());
        }
    });

    public static final RegistryObject<StandingAndWallBlockItem> SHEEP_HEAD = ITEMS.register(
            ButchersConstants.SHEEP_HEAD_ID, () -> new StandingAndWallBlockItem(
                    ModBlocks.SHEEP_HEAD.get(), ModBlocks.SHEEP_WALL_HEAD.get(),
                    new Item.Properties(), Direction.DOWN) {
                @Override
                public void initializeClient(Consumer<IClientItemExtensions> consumer) {
                    consumer.accept(new IClientItemExtensions() {
                        private BlockEntityWithoutLevelRenderer renderer;
                        @Override
                        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                            if (renderer == null) {
                                Minecraft mc = Minecraft.getInstance();
                                renderer = new BlockEntityWithoutLevelRenderer(
                                        mc.getBlockEntityRenderDispatcher(), mc.getEntityModels()) {
                                    private final SheepHeadBlockEntity be = new SheepHeadBlockEntity(
                                            BlockPos.ZERO, ModBlocks.SHEEP_HEAD.get().defaultBlockState());
                                    @Override
                                    public void renderByItem(ItemStack stack, ItemDisplayContext ctx,
                                                             PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
                                        pose.translate(0.5, 0, 0.5);
                                        pose.mulPose(Axis.YP.rotationDegrees(180));
                                        pose.translate(-0.5, 0, -0.5);
                                        mc.getBlockEntityRenderDispatcher().renderItem(be, pose, buffer, light, overlay);
                                    }
                                };
                            }
                            return renderer;
                        }
                    });
                }
            });

    private static IClientItemExtensions newCarcassExtensions() {
        return new IClientItemExtensions() {
            private CarcassItemRenderer renderer;
            @Override
            public CarcassItemRenderer getCustomRenderer() {
                if (renderer == null) renderer = new CarcassItemRenderer();
                return renderer;
            }
        };
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

}
