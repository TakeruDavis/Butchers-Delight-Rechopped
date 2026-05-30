package net.takerudavis.butchers_delight_rechopped;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.takerudavis.butchers_delight_rechopped.common.ButchersConstants;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, ButchersConstants.MODID);

    public static final RegistryObject<SoundEvent> CLEAVER_CHOP = register("item.cleaver.chop");
    public static final RegistryObject<SoundEvent> ROASTER_SINGE = register("block.roaster.singe");
    public static final RegistryObject<SoundEvent> ROASTER_SIZZLE = register("block.roaster.sizzle");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(ButchersConstants.MODID, name)));
    }

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }

}
