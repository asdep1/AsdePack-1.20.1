package fr.asdepack;

import fr.asdepack.common.capabilities.playerkit.IPlayerKitStorage;
import fr.asdepack.common.capabilities.radios.IPlayerRadioStorage;
import fr.asdepack.common.menus.KitPreviewMenu;
import fr.asdepack.common.menus.RadioConfigMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Asdepack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRegistries {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Asdepack.MODID);

    public static final RegistryObject<MenuType<KitPreviewMenu>> KIT_PREVIEW =
            MENU_TYPES.register("kit_preview", () -> KitPreviewMenu.TYPE);

    public static final RegistryObject<MenuType<RadioConfigMenu>> RADIO_CONFIG =
            MENU_TYPES.register("radio_config", () -> RadioConfigMenu.TYPE);

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IPlayerKitStorage.class);
        event.register(IPlayerRadioStorage.class);
    }
}
