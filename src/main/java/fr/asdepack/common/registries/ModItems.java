package fr.asdepack.common.registries;

import fr.asdepack.Asdepack;
import fr.asdepack.common.registries.items.RadioItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(net.minecraftforge.registries.ForgeRegistries.ITEMS, Asdepack.MODID);

    public static final RegistryObject<RadioItem> RADIO = ITEMS.register("radio", () -> new RadioItem(new Item.Properties().stacksTo(1)));

    public static void register(net.minecraftforge.eventbus.api.IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    @SubscribeEvent
    public static void addCreativeTab(BuildCreativeModeTabContentsEvent ev) {
        if(ev.getTabKey() == ModCreativeTabs.ASDEPACK_TAB.getKey()) {
            ev.accept(RADIO);
        }
    }
}
