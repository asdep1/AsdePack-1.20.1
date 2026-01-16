package fr.asdepack.common.registries;

import fr.asdepack.Asdepack;
import fr.asdepack.common.registries.items.RadioItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRAR =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Asdepack.MODID);

    public static RegistryObject<CreativeModeTab> ASDEPACK_TAB = REGISTRAR.register("asdepack_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.literal("Asdepack"))
                    .icon(() -> ModItems.RADIO.get().getDefaultInstance())
                    .build()
    );

    public static void register(net.minecraftforge.eventbus.api.IEventBus eventBus) {
        REGISTRAR.register(eventBus);
    }

}
