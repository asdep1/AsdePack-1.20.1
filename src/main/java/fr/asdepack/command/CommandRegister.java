package fr.asdepack.command;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
