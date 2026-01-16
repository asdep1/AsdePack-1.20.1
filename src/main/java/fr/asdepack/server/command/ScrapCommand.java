package fr.asdepack.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fr.asdepack.Asdepack;
import fr.asdepack.common.menus.ScrapMenu;
import fr.asdepack.common.menus.ScrappingMenu;
import fr.asdepack.server.Server;
import fr.asdepack.types.Scrap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;

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
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        player.openMenu(new SimpleMenuProvider(
                ScrapMenu::new,
                Component.literal("Scrap list")
        ));

        return 1;
    }

    private static int add(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        ItemStack mainHandItem = player.getMainHandItem().copy();
        if (mainHandItem == ItemStack.EMPTY) return 0;

        try {
            ItemStack compatItem = Scrap.compatTacz(mainHandItem);

            if (Server.getDatabaseManager().getScrapManager().getScrapByItem(compatItem) != null) {
                player.sendSystemMessage(Component.literal("La bdd contient déjà l'item " + mainHandItem));
                return 0;
            }

            Scrap s = new Scrap();
            s.setItem(Scrap.compatTacz(mainHandItem));
            s.setScraps(new ArrayList<>());
            Server.getDatabaseManager().getScrapManager().addScrap(s);

            player.sendSystemMessage(Component.literal("Ajout de l'item " + mainHandItem));
        } catch (SQLException e) {
            player.sendSystemMessage(Component.literal("Erreur lors de l'ajout de l'item : " + e.getMessage()));
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
}
