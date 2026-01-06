package fr.asdepack.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import fr.asdepack.Asdepack;
import fr.asdepack.gui.KitMenu;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("kit")

                        .executes(ctx -> openMenu(ctx.getSource()))

                        .then(Commands.literal("list")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.list"))
                                .executes(ctx -> listKits(ctx.getSource()))
                        )

                        .then(Commands.literal("add")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.add"))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .executes(ctx -> addKit(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "name")
                                        ))
                                )
                        )

                        .then(Commands.literal("remove")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.remove"))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> {
                                            for (String s : Asdepack.KITMANAGER.getKitList()) {
                                                builder.suggest(s.replace('§', '&').replace(' ', '_'));
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> removeKit(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "name")
                                        ))
                                )
                        )
        );
    }

    private static int openMenu(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        player.openMenu(new SimpleMenuProvider(
                KitMenu::new,
                Component.literal("Kit list")
        ));

        return 1;
    }

    private static int listKits(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        List<String> kits = Asdepack.KITMANAGER.getKitList();

        if (kits.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cAucun kit enregistré."));
            return 1;
        }

        player.sendSystemMessage(Component.literal("§6Liste des kits :"));
        for (String kit : kits) {
            player.sendSystemMessage(Component.literal(" §7- §e" + kit));
        }

        return 1;
    }

    private static int addKit(CommandSourceStack source, String name) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        name = name.replace('&', '§').replace('_', ' ');

        if (Asdepack.KITMANAGER.getKitList().contains(name)) {
            player.sendSystemMessage(Component.literal("§cLe kit §e" + name + " §cexiste déjà."));
            return 0;
        }

        List<ItemStack> items = new ArrayList<>(player.getInventory().items);
        ItemStack icon = player.getMainHandItem().copy();

        if (Asdepack.KITMANAGER.saveKit(name, icon, items)) {
            player.sendSystemMessage(Component.literal("§aKit §e" + name + " §aajouté avec succès."));
        } else {
            player.sendSystemMessage(Component.literal("§cErreur lors de l'enregistrement du kit §e" + name + " §c."));
            return 0;
        }

        return 1;
    }

    private static int removeKit(CommandSourceStack source, String name) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        name = name.replace('&', '§').replace('_', ' ');
        if (!Asdepack.KITMANAGER.getKitList().contains(name)) {
            player.sendSystemMessage(Component.literal("§cLe kit §e" + name + " §cn'existe pas."));
            return 0;
        }

        Asdepack.KITMANAGER.removeKit(name);
        player.sendSystemMessage(Component.literal("§aKit §e" + name + " §asupprimé."));
        return 1;
    }
}
