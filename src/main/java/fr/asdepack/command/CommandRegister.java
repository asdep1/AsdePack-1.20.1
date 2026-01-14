package fr.asdepack.command;

import fr.asdepack.Asdepack;
import fr.asdepack.network.PacketHelper;
import fr.asdepack.network.packets.CSyncRegion;
import fr.asdepack.plugin.ProtectedRegion;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
}
