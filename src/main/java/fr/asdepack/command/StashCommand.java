package fr.asdepack.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fr.asdepack.Asdepack;
import fr.asdepack.client.gui.StashMenu;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

public class StashCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stash")
                .executes(StashCommand::stash));
    }

    private static int stash(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        if (!Asdepack.WG_ADAPTER.isPlayerInRegion(player, "spawn")){
            player.sendSystemMessage(Component.literal("Vous ne pouvez pas utiliser cette commande ici"));
            return 0;
        }
        player.openMenu(new SimpleMenuProvider(
                StashMenu::new,
                Component.literal("Stash")
        ));

        return 1;
    }
}
