package fr.asdepack.client;

import fr.asdepack.Asdepack;
import fr.asdepack.common.capabilities.playerkit.IPlayerKitStorage;
import fr.asdepack.common.capabilities.playerkit.PlayerKitStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Asdepack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModHandler {
    public static Logger LOGGER = LogManager.getLogger("AsdepackClient");
    public static IPlayerKitStorage PLAYER_KIT_STORAGE = new PlayerKitStorage(); // Only for client-side usage, received from server
    public static String CURRENT_REGIONS; // Only for client-side usage, received from server

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        LOGGER.info("Registering Keys");
        event.register(Keybindings.INSTANCE.demoKeybind);
    }

//    @SubscribeEvent
//    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
//        event.register("obj", ObjLoader.INSTANCE);
//    }
}
