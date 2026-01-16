package fr.asdepack.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.asdepack.Asdepack;
import fr.asdepack.common.menus.AdminStashMenu;
import fr.asdepack.common.menus.StashMenu;
import fr.asdepack.server.Server;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

public class StashCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stash")
                .executes(StashCommand::stash)
                .then(Commands.literal("open")
                        .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.stash.open"))

                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> {
                                    try {
                                        ServerPlayer targetPlayer = EntityArgument.getPlayer(ctx, "player");
                                        return adminOpen(ctx, targetPlayer.getStringUUID());
                                    } catch (CommandSyntaxException e) {
                                        ctx.getSource().sendFailure(Component.literal("Player not found"));
                                        return 0;
                                    }
                                })
                        )
                        .then(Commands.argument("uuid", StringArgumentType.string())
                                .executes(ctx -> adminOpen(ctx, StringArgumentType.getString(ctx, "uuid")))
                        )

                )
        );
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

    private static int adminOpen(CommandContext<CommandSourceStack> context, String uuid) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        if (Server.getDatabaseManager().getStashManager().getStashByUUID(uuid) == null) {
            player.sendSystemMessage(Component.literal("Ce joueur n'a pas de stash"));
            return 0;
        }

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new AdminStashMenu(id, inv, p, uuid),
                Component.literal("Admin Stash - " + uuid)
        ));

        return 1;
    }
}
