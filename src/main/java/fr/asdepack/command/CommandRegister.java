package fr.asdepack.command;

import fr.asdepack.Asdepack;
import fr.asdepack.plugin.ProtectedRegion;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class CommandRegister {

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        RTPCommand.register(event.getDispatcher());
        SpawnCommand.register(event.getDispatcher());
        StashCommand.register(event.getDispatcher());
        ScrapCommand.register(event.getDispatcher());
        KitCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.ServerTickEvent event) {
        for(ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
                String worldName = player.level().dimension().location().getPath();
                if (worldName.equals("overworld")) worldName = "world"; // Ajustement fréquent sur Mohist/Bukkit

                List<ProtectedRegion> regions = Asdepack.WG_ADAPTER.getApplicableRegions(worldName, player.position());

                if (!regions.isEmpty()) {
                    String regionNames = regions.stream()
                            .map(ProtectedRegion::getId)
                            .collect(Collectors.joining(", "));

                    player.displayClientMessage(Component.literal("Région(s) : §a" + regionNames), true);
                } else {
                    player.displayClientMessage(Component.literal("§7Zone sauvage"), true);
                }
            }
        }
    }

}
