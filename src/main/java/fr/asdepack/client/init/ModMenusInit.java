package fr.asdepack.client.init;

import fr.asdepack.Asdepack;
import fr.asdepack.ModRegistries;
import fr.asdepack.client.screen.ScreenKitList;
import fr.asdepack.client.screen.ScreenRadioConfig;
import fr.asdepack.common.menus.KitPreviewMenu;
import fr.asdepack.common.menus.RadioConfigMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Asdepack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModMenusInit {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent evt) {
        evt.enqueueWork(() -> {
            MenuScreens.<KitPreviewMenu, ScreenKitList>register(
                    ModRegistries.KIT_PREVIEW.get(),
                    ScreenKitList::new
            );
            MenuScreens.<RadioConfigMenu, ScreenRadioConfig>register(
                    ModRegistries.RADIO_CONFIG.get(),
                    ScreenRadioConfig::new
            );
        });
    }
}