package fr.asdepack.client.init;

import fr.asdepack.Asdepack;
import fr.asdepack.ModRegistration;
import fr.asdepack.client.screen.ScreenKitList;
import fr.asdepack.client.screen.menus.KitPreviewMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Asdepack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModMenusInit {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent evt) {
        evt.enqueueWork(() -> {
            MenuScreens.<KitPreviewMenu, ScreenKitList>register(
                    ModRegistration.KIT_PREVIEW.get(),
                    ScreenKitList::new
            );
        });
    }
}