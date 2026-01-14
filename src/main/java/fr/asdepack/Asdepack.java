package fr.asdepack;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import fr.asdepack.client.init.ModMenusInit;
import fr.asdepack.command.PermissionUtil;
import fr.asdepack.network.PacketHelper;
import fr.asdepack.server.Server;
import fr.asdepack.plugin.VaultAdapter;
import fr.asdepack.plugin.WorldGuardAdapter;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

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
        ModRegistration.register(modEventBus);
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
