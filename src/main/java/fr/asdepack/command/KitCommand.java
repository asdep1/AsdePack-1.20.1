package fr.asdepack.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import fr.asdepack.Asdepack;
import fr.asdepack.Kit;
import fr.asdepack.gui.KitMenu;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
                                            for (Kit s : Asdepack.KITMANAGER.getAllKits()) {
                                                builder.suggest(s.getName().replace('§', '&').replace(' ', '_'));
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> removeKit(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "name")
                                        ))
                                )
                        )
                        .then(Commands.literal("edit")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.edit"))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> {
                                            for (Kit s : Asdepack.KITMANAGER.getAllKits()) {
                                                builder.suggest(s.getName().replace('§', '&').replace(' ', '_'));
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayer();
                                            String name = StringArgumentType.getString(ctx, "name")
                                                    .replace('&', '§').replace('_', ' ');

                                            Kit kit = Asdepack.KITMANAGER.getKit(name);
                                            if (kit == null) {
                                                player.sendSystemMessage(Component.literal("§cKit introuvable."));
                                                return 0;
                                            }

                                            ItemStack icon = player.getMainHandItem().copy();
                                            if (icon == ItemStack.EMPTY) {
                                                icon = new ItemStack(Items.CHEST);
                                            }

                                            Kit updated = new Kit(
                                                    kit.getName(),
                                                    new ArrayList<>(player.getInventory().items),
                                                    icon,
                                                    kit.getCost(),
                                                    kit.getPermission(),
                                                    kit.getCooldown()
                                            );

                                            Asdepack.KITMANAGER.saveKit(updated);
                                            player.sendSystemMessage(Component.literal("§aKit modifié."));
                                            return 1;
                                        })
                                )
                        )

                        .then(Commands.literal("setpermission")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.permission"))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests((ctx, builder) -> {
                                            for (Kit s : Asdepack.KITMANAGER.getAllKits()) {
                                                builder.suggest(s.getName().replace('§', '&').replace(' ', '_'));
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
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.cooldown"))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests((ctx, builder) -> {
                                            for (Kit s : Asdepack.KITMANAGER.getAllKits()) {
                                                builder.suggest(s.getName().replace('§', '&').replace(' ', '_'));
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("seconds", IntegerArgumentType.integer(0))
                                                .executes(ctx -> setCooldown(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name"),
                                                        IntegerArgumentType.getInteger(ctx, "seconds")
                                                ))
                                        )
                                )
                        )
                        .then(Commands.literal("setcost")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.cost"))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests((ctx, builder) -> {
                                            for (Kit s : Asdepack.KITMANAGER.getAllKits()) {
                                                builder.suggest(s.getName().replace('§', '&').replace(' ', '_'));
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("cost", IntegerArgumentType.integer(0))
                                                .executes(ctx -> setCost(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name"),
                                                        IntegerArgumentType.getInteger(ctx, "cost")
                                                ))
                                        )
                                )
                        )

                        .then(Commands.literal("give")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.kit.give"))
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> {
                                            for (Kit s : Asdepack.KITMANAGER.getAllKits()) {
                                                builder.suggest(s.getName().replace('§', '&').replace(' ', '_'));
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> giveKit(
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

        List<Kit> kits = Asdepack.KITMANAGER.getAllKits();

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

    private static int addKit(CommandSourceStack source, String name) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        name = name.replace('&', '§').replace('_', ' ');

        String finalName = name;
        if (Asdepack.KITMANAGER.getAllKits().stream()
                .anyMatch(kit -> kit.getName().equalsIgnoreCase(finalName))) {
            player.sendSystemMessage(Component.literal("§cLe kit §e" + name + " §cexiste déjà."));
            return 0;
        }

        List<ItemStack> items = new ArrayList<>(player.getInventory().items);
        ItemStack icon = player.getMainHandItem().copy();
        if (icon == ItemStack.EMPTY) {
            icon = new ItemStack(Items.CHEST);
        }

        if (Asdepack.KITMANAGER.saveKit(new Kit(name, items, icon))) {
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
        String finalName = name;
        if (!Asdepack.KITMANAGER.getAllKits().stream()
                .anyMatch(kit -> kit.getName().equalsIgnoreCase(finalName))) {
            player.sendSystemMessage(Component.literal("§cLe kit §e" + name + " §cn'existe pas."));
            return 0;
        }

        Asdepack.KITMANAGER.removeKit(name);
        player.sendSystemMessage(Component.literal("§aKit §e" + name + " §asupprimé."));
        return 1;
    }

    private static int giveKit(CommandSourceStack source, String name) {
        ServerPlayer player = source.getPlayer();
        name = name.replace('&', '§').replace('_', ' ');

        Kit kit = Asdepack.KITMANAGER.getKit(name);
        if (kit == null) {
            player.sendSystemMessage(Component.literal("§cKit introuvable."));
            return 0;
        }

        for (ItemStack stack : kit.getItems()) {
            player.getInventory().placeItemBackInInventory(stack.copy());
        }
        return 1;
    }

    private static int setCooldown(CommandSourceStack source, String name, int seconds) {
        ServerPlayer player = source.getPlayer();
        name = name.replace('&', '§').replace('_', ' ');
        Kit kit = Asdepack.KITMANAGER.getKit(name);
        if (kit == null) return 0;

        Kit updated = new Kit(
                kit.getName(), kit.getItems(), kit.getIcon(),
                kit.getCost(), kit.getPermission(), seconds
        );

        Asdepack.KITMANAGER.saveKit(updated);
        player.sendSystemMessage(Component.literal("§aCooldown défini à " + seconds + "s."));
        return 1;
    }

    private static int setCost(CommandSourceStack source, String name, int cost) {
        ServerPlayer player = source.getPlayer();
        name = name.replace('&', '§').replace('_', ' ');
        Kit kit = Asdepack.KITMANAGER.getKit(name);
        if (kit == null) return 0;

        Kit updated = new Kit(
                kit.getName(), kit.getItems(), kit.getIcon(),
                cost, kit.getPermission(), kit.getCooldown()
        );

        Asdepack.KITMANAGER.saveKit(updated);
        player.sendSystemMessage(Component.literal("§aPrix défini à §e" + cost + "§a."));
        return 1;
    }

    private static int setPermission(CommandSourceStack source, String name, String permission) {
        ServerPlayer player = source.getPlayer();
        name = name.replace('&', '§').replace('_', ' ');
        Kit kit = Asdepack.KITMANAGER.getKit(name);
        if (kit == null) return 0;

        Kit updated = new Kit(
                kit.getName(), kit.getItems(), kit.getIcon(),
                kit.getCost(), permission, kit.getCooldown()
        );

        Asdepack.KITMANAGER.saveKit(updated);
        player.sendSystemMessage(Component.literal("§aPermission mise à jour."));
        return 1;
    }
}
