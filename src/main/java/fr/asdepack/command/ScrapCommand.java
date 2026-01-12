package fr.asdepack.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fr.asdepack.client.gui.ScrappingMenu;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

public class ScrapCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("scrap")
                .executes(ScrapCommand::scrap)
                .then(Commands.literal("open")
                        .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.scrap.open"))
                        .executes(ScrapCommand::open)
                )
                .then(Commands.literal("add")
                        .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.scrap.add"))
                        .executes(ScrapCommand::add)
                )
        );
    }

    private static int scrap(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        if (!Asdepack.WG_ADAPTER.isPlayerInRegion(player, "spawn")){
            player.sendSystemMessage(Component.literal("Vous ne pouvez pas utiliser cette commande ici"));
            return 0;
        }

        player.openMenu(new SimpleMenuProvider(
                ScrappingMenu::new,
                Component.literal("Scrapper")
        ));

        return 1;
    }

    private static int open(CommandContext<CommandSourceStack> context) {
//        ServerPlayer player = context.getSource().getPlayer();
//        if (player == null) return 0;
//
//        player.openMenu(new SimpleMenuProvider(
//                ScrapMenu::new,
//                Component.literal("Scrap list")
//        ));

        return 1;
    }

    private static int add(CommandContext<CommandSourceStack> context) {
//        ServerPlayer player = context.getSource().getPlayer();
//        if (player == null) return 0;
//
//        if (player.getMainHandItem() == ItemStack.EMPTY) return 0;
//        if (Asdepack.SCRAP_MANAGER.hasScrap(player.getMainHandItem())) {
//            player.sendSystemMessage(Component.literal("La bdd contient déjà l'item " + player.getMainHandItem()));
//            return 0;
//        }
//        Asdepack.SCRAP_MANAGER.saveScrap(player.getMainHandItem(), new ArrayList<>());
//        player.sendSystemMessage(Component.literal("Ajout de l'item " + player.getMainHandItem()));
        return 1;
    }
}
