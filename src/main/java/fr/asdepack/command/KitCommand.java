package fr.asdepack.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import fr.asdepack.Asdepack;
import fr.asdepack.client.gui.KitMenu;
import fr.asdepack.server.Server;
import fr.asdepack.types.Kit;
import lombok.SneakyThrows;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KitCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("kit")

                        .executes(ctx -> openMenu(ctx.getSource()))

                        .then(Commands.literal("list")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.list"))
                                .executes(ctx -> {
                                    if(ctx.getSource().getPlayer() == null) return 0;
                                    ctx.getSource().getPlayer().sendSystemMessage(
                                            Component.literal("Liste des kits :").withStyle(ChatFormatting.RED)
                                    );
                                    try {
                                        for (Kit kit : Server.getDatabaseManager().getKitManager().getKits()) {
                                            ctx.getSource().getPlayer().sendSystemMessage(
                                                    Component.literal(" - " + kit.getName()).withStyle(ChatFormatting.YELLOW)
                                            );
                                        }
                                    } catch (SQLException e) {
                                        ctx.getSource().sendSystemMessage(
                                                Component.literal("Erreur lors de la récupération des kits.").withStyle(ChatFormatting.RED)
                                        );
                                        throw new RuntimeException(e);
                                    }
                                    return 1;
                                })
                        )

                        .then(Commands.literal("add")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.add"))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            try {
                                                return addKit(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name")
                                                );
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                        })
                                )
                        )
//
                        .then(Commands.literal("remove")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.remove"))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> {
                                            try {
                                                for (Kit s : Server.getDatabaseManager().getKitManager().getKits()) {
                                                    builder.suggest(s.getName().replace('§', '&').replace(' ', '_'));
                                                }
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
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

    @SneakyThrows
    private static int openMenu(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        player.openMenu(new SimpleMenuProvider(
                (int pContainerId, Inventory pPlayerInventory, Player pPlayer) -> {
                    try {
                        return new KitMenu(
                                pContainerId,
                                pPlayerInventory,
                                pPlayer,
                                0
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                Component.literal("Kit list")
        ));

        return 1;
    }

    private static int listKits(CommandSourceStack source) throws SQLException {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        List<Kit> kits = Server.getDatabaseManager().getKitManager().getKits();

        if (kits.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cAucun kit enregistré."));
            return 1;
        }

        player.sendSystemMessage(Component.literal("§6Liste des kits :"));
        for (Kit kit : kits) {
            player.sendSystemMessage(Component.literal(" §7- §e" + kit.getName()));
        }

        return 1;
    }

    private static int addKit(CommandSourceStack source, String name) throws SQLException {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        name = name.replace('&', '§').replace('_', ' ');


        Kit kit = Server.getDatabaseManager().getKitManager().getKitByName(name);


        if (kit != null) {
            player.sendSystemMessage(Component.literal("§cLe kit §e" + name + " §cexiste déjà."));
            return 0;
        }

        List<ItemStack> items = new ArrayList<>(player.getInventory().items);
        ItemStack icon = player.getMainHandItem().copy();

        Kit k = new Kit();
        k.setName(name);
        k.setIcon(icon);
        k.setItems(items);
        k.setCost(0);
        k.setCooldown(0);
        k.setPermission("");

        if (Server.getDatabaseManager().getKitManager().saveKit(k)) {
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
        Kit kit = Server.getDatabaseManager().getKitManager().getKitByName(name);


        if (kit == null) {
            player.sendSystemMessage(Component.literal("§cLe kit §e" + name + " §cn'existe pas."));
            return 0;
        }

        Server.getDatabaseManager().getKitManager().removeKit(kit.getName());
        player.sendSystemMessage(Component.literal("§aKit §e" + name + " §asupprimé."));
        return 1;
    }
}
