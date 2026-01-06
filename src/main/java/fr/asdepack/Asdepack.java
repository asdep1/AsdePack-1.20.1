package fr.asdepack;

import fr.asdepack.command.PermissionUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.sql.SQLException;


@Mod(Asdepack.MODID)
public class Asdepack {

    public static final StashManager STASHMANAGER;
    public static final ScrapManager SCRAP_MANAGER;
    public static final KitManager KITMANAGER;
    public static final String MODID = "asdepack";

    static {
        try {
            STASHMANAGER = new StashManager("config/stash.db");
            SCRAP_MANAGER = new ScrapManager("config/scrap.db");
            KITMANAGER = new KitManager("config/kits.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Asdepack() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        PermissionUtil.init();
        RTPConfig.load();
    }
}
