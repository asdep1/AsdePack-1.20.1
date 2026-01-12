package fr.asdepack.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fr.asdepack.Asdepack;
import fr.asdepack.RTPConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class RTPCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("rtp")
                        .executes(RTPCommand::teleport)
                        .then(Commands.literal("add")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.rtp.add"))

                                .executes(RTPCommand::add))
                        .then(Commands.literal("remove")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.rtp.remove"))

                                .executes(RTPCommand::remove))
                        .then(Commands.literal("list")
                                .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.rtp.list"))

                                .executes(RTPCommand::list))
        );
    }

    private static int list(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();

        player.sendSystemMessage(Component.literal("Liste des positions de tp :"));
        int i = 0;
        for (Vec3 pos : RTPConfig.positions) {
            player.sendSystemMessage(Component.literal(i++ + ": " + pos + " (" + (int) pos.distanceTo(player.position()) + "m)"));
        }
        return 1;
    }

    private static int add(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        RTPConfig.add(player.getOnPos().getCenter());
        player.sendSystemMessage(Component.literal("Position ajouter"));
        RTPConfig.save();
        return 1;
    }

    private static int remove(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        if (RTPConfig.positions.contains(player.getOnPos().getCenter())) {
            RTPConfig.remove(player.getOnPos().getCenter());
            player.sendSystemMessage(Component.literal("Position supprimer"));
            RTPConfig.save();
        } else {
            player.sendSystemMessage(Component.literal("Cette position n'est pas enregistrer"));
        }

        return 1;
    }

    private static int teleport(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        if (!Asdepack.WG_ADAPTER.isPlayerInRegion(player, "spawn")){
            player.sendSystemMessage(Component.literal("Vous ne pouvez pas utiliser cette commande ici"));
            return 0;
        }

        Random rnd = new Random();
        int i = rnd.nextInt(RTPConfig.positions.size());
        Vec3 teleportPos = RTPConfig.positions.get(i);
        int timer = RTPConfig.rtpTimer;
        if (player.isCreative()) timer = 0;
        WaitingManager.start(player, timer, p -> {
            p.sendSystemMessage(Component.literal("Téléportation"));
            p.teleportTo(teleportPos.x(), teleportPos.y(), teleportPos.z());
        });

        return 1;
    }
}
