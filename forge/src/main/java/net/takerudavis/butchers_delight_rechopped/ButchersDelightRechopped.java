package net.takerudavis.butchers_delight_rechopped;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.takerudavis.butchers_delight_rechopped.block.CarcassRegistry;
import net.takerudavis.butchers_delight_rechopped.block.entity.SheepHeadBlockEntity;
import net.takerudavis.butchers_delight_rechopped.client.CarcassBlockEntityRenderer;
import net.takerudavis.butchers_delight_rechopped.client.RoasterBlockEntityRenderer;
import net.takerudavis.butchers_delight_rechopped.client.SheepHeadBlockEntityRenderer;
import net.takerudavis.butchers_delight_rechopped.common.ButchersConstants;
import net.takerudavis.butchers_delight_rechopped.common.CommonSounds;

@Mod(ButchersConstants.MODID)
public class ButchersDelightRechopped {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, ButchersConstants.MODID);

    // Creates a creative tab with the id "butchers_delight_rechopped:main_tab" for mod's items, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS
            .register("main_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.butchers_delight_rechopped"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.CLEAVER.get().getDefaultInstance())
            .build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ButchersDelightRechopped() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        ModBlocks.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ModItems.register(modEventBus);
        // Register the Deferred Register to the mod event bus so sound events get registered
        ModSounds.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ButchersDelightRechopped) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        SheepHeadBlockEntity.TYPE = ModBlocks.SHEEP_HEAD_BLOCK_ENTITY.get();

        CommonSounds.CLEAVER_CHOP   = ModSounds.CLEAVER_CHOP.get();
        CommonSounds.ROASTER_SINGE  = ModSounds.ROASTER_SINGE.get();
        CommonSounds.ROASTER_SIZZLE = ModSounds.ROASTER_SIZZLE.get();
    }

    // Add the mod's items to the mod's main tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == MAIN_TAB.getKey()) {
            event.accept(ModItems.CLEAVER);
            event.accept(ModBlocks.HOOK_BLOCK);
            event.accept(ModBlocks.ROASTER);
            CarcassRegistry.getAll().forEach(event::accept);
            event.accept(ModItems.SHEEP_HEAD);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = ButchersConstants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(
                    ModBlocks.CARCASS_BLOCK_ENTITY.get(),
                    CarcassBlockEntityRenderer::new
            );
            event.registerBlockEntityRenderer(
                    ModBlocks.ROASTER_BLOCK_ENTITY.get(),
                    RoasterBlockEntityRenderer::new
            );
            event.registerBlockEntityRenderer(
                    ModBlocks.SHEEP_HEAD_BLOCK_ENTITY.get(),
                    SheepHeadBlockEntityRenderer::new
            );
        }
    }
}
