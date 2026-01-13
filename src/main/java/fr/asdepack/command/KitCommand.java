package fr.asdepack.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import net.minecraft.world.item.Items;

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
                                    try {
                                        return listKits(ctx.getSource());
                                    } catch (SQLException e) {
                                        ctx.getSource().sendSystemMessage(
                                                Component.literal("Erreur lors de la récupération des kits.").withStyle(ChatFormatting.RED)
                                        );
                                        throw new RuntimeException(e);
                                    }
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
                                                ctx.getSource().sendSystemMessage(
                                                        Component.literal("Erreur lors de l'enregistrement du kit.").withStyle(ChatFormatting.RED)
                                                );
                                                throw new RuntimeException(e);
                                            }
                                        })
                                )
                        )
                        .then(Commands.literal("remove")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.remove"))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> {
                                            try {
                                                for (Kit kit : Server.getDatabaseManager().getKitManager().getKits()) {
                                                    String s = kit.getName().replace('§', '&').replace(' ', '_');
                                                    s = "\"" + s + "\"";
                                                    builder.suggest(s);
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
                        .then(Commands.literal("setpermission")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.setpermission"))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests((ctx, builder) -> {
                                            try {
                                                for (Kit kit : Server.getDatabaseManager().getKitManager().getKits()) {
                                                    String s = kit.getName().replace('§', '&').replace(' ', '_');
                                                    s = "\"" + s + "\"";
                                                    builder.suggest(s);
                                                }
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("permission", StringArgumentType.string())
                                                .executes(ctx -> setPermission(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name"),
                                                        StringArgumentType.getString(ctx, "permission")
                                                ))
                                        )
                                )
                        )
                        .then(Commands.literal("setcooldown")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.setcooldown"))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests((ctx, builder) -> {
                                            try {
                                                for (Kit kit : Server.getDatabaseManager().getKitManager().getKits()) {
                                                    String s = kit.getName().replace('§', '&').replace(' ', '_');
                                                    s = "\"" + s + "\"";
                                                    builder.suggest(s);
                                                }
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("seconds", IntegerArgumentType.integer())
                                                .executes(ctx -> setCooldown(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name"),
                                                        IntegerArgumentType.getInteger(ctx, "seconds")
                                                ))
                                        )
                                )
                        )
                        .then(Commands.literal("setcost")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.setcost"))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests((ctx, builder) -> {
                                            try {
                                                for (Kit kit : Server.getDatabaseManager().getKitManager().getKits()) {
                                                    String s = kit.getName().replace('§', '&').replace(' ', '_');
                                                    s = "\"" + s + "\"";
                                                    builder.suggest(s);
                                                }
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("cost", IntegerArgumentType.integer())
                                                .executes(ctx -> setCost(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name"),
                                                        IntegerArgumentType.getInteger(ctx, "cost")
                                                ))
                                        )
                                )
                        )
                        .then(Commands.literal("seticon")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.seticon"))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> {
                                            try {
                                                for (Kit kit : Server.getDatabaseManager().getKitManager().getKits()) {
                                                    String s = kit.getName().replace('§', '&').replace(' ', '_');
                                                    s = "\"" + s + "\"";
                                                    builder.suggest(s);
                                                }
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> setIcon(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "name")
                                        ))
                                )
                        )
                        .then(Commands.literal("give")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.give"))
                                .then(Commands.argument("player", StringArgumentType.string())
                                        .suggests((ctx, builder) -> {
                                            for (ServerPlayer player : ctx.getSource().getServer().getPlayerList().getPlayers()) {
                                                builder.suggest(player.getName().getString());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                                .suggests((ctx, builder) -> {
                                                    try {
                                                        for (Kit kit : Server.getDatabaseManager().getKitManager().getKits()) {
                                                            builder.suggest(kit.getName().replace('§', '&').replace(' ', '_'));
                                                        }
                                                    } catch (SQLException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(ctx -> give(ctx.getSource(), StringArgumentType.getString(ctx, "player"), StringArgumentType.getString(ctx, "name")))
                                        )
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
        if (icon == ItemStack.EMPTY) icon = new ItemStack(Items.NAME_TAG);
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

    private static int setPermission(CommandSourceStack source, String name, String permission) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        name = name.replace('&', '§').replace('_', ' ');

        Server.getDatabaseManager().getKitManager().setPermission(name, permission);

        player.sendSystemMessage(Component.literal("§aPermission du kit §e" + name + " §amise à jour avec succès."));
        return 1;
    }

    private static int setCooldown(CommandSourceStack source, String name, int cooldown) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        name = name.replace('&', '§').replace('_', ' ');

        Server.getDatabaseManager().getKitManager().setCooldown(name, cooldown);

        player.sendSystemMessage(Component.literal("§aCooldown du kit §e" + name + " §amis à jour avec succès."));
        return 1;
    }

    private static int setCost(CommandSourceStack source, String name, int cost) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        name = name.replace('&', '§').replace('_', ' ');

        Server.getDatabaseManager().getKitManager().setCost(name, cost);

        player.sendSystemMessage(Component.literal("§aCoûts du kit §e" + name + " §amis à jour avec succès."));
        return 1;
    }

    private static int setIcon(CommandSourceStack source, String name) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        name = name.replace('&', '§').replace('_', ' ');
        ItemStack icon = player.getMainHandItem().copy();
        if (icon == ItemStack.EMPTY) icon = new ItemStack(Items.NAME_TAG);
        Server.getDatabaseManager().getKitManager().setIcon(name, icon);

        player.sendSystemMessage(Component.literal("§aIcône du kit §e" + name + " §amis à jour avec succès."));
        return 1;
    }

    private static int give(CommandSourceStack source, String playername, String name) {
        ServerPlayer player = source.getServer().getPlayerList().getPlayerByName(playername);
        if (player == null) return 0;

        name = name.replace('&', '§').replace('_', ' ');

        for (ItemStack item : Server.getDatabaseManager().getKitManager().getKitByName(name).getItems()) {
            player.getInventory().placeItemBackInInventory(item.copy());
        }
        player.sendSystemMessage(Component.literal("§aKit §e" + name + " §aenvoyé avec succès."));
        return 1;
    }
}
