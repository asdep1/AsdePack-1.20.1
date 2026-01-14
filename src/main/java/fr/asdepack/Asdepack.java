package fr.asdepack;

import fr.asdepack.server.command.PermissionUtil;
import fr.asdepack.common.network.PacketHelper;
import fr.asdepack.server.Server;
import fr.asdepack.server.bridges.VaultAdapter;
import fr.asdepack.server.bridges.WorldGuardAdapter;
import fr.asdepack.server.modules.rtp.RTPConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.sql.SQLException;


@Mod(Asdepack.MODID)
public class Asdepack {
    public static final String MODID = "asdepack";
    public static WorldGuardAdapter WG_ADAPTER = new WorldGuardAdapter();
    public static VaultAdapter VAULT_ADAPTER = new VaultAdapter();

    private static final String PROTOCOL_VERSION = "1";

    public Asdepack(FMLJavaModLoadingContext context) throws ClassNotFoundException {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        ModRegistries.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHelper::register);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) throws SQLException {
        if(FMLEnvironment.production) {
            PermissionUtil.init();
        }

        WG_ADAPTER.init();
        VAULT_ADAPTER.init();
        RTPConfig.load();
        Server.init();
    }
}
