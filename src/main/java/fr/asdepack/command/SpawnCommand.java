package fr.asdepack.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fr.asdepack.RTPConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class SpawnCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spawn")
                .executes(SpawnCommand::teleport)
                .then(Commands.literal("set")
                        .requires(src -> PermissionUtil.hasPermission(src.getPlayer(), "asdepack.spawn.set"))
                        .executes(SpawnCommand::dispatch))
        );
    }

    private static int dispatch(CommandContext<CommandSourceStack> context) {

        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        RTPConfig.setSpawn(player.getOnPos().getCenter());
        player.sendSystemMessage(Component.literal("Nouveau spawn définis"));

        return 1;
    }

    private static int teleport(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        if (!RTPConfig.hasSpawn()) {
            player.sendSystemMessage(Component.literal("Aucun spawn définis"));
            return 0;
        }
        Vec3 teleportPos = RTPConfig.spawn;

        WaitingManager.start(player, RTPConfig.spawnTimer, p -> {
            p.sendSystemMessage(Component.literal("Retour au spawn"));
            p.teleportTo(teleportPos.x(), teleportPos.y(), teleportPos.z());
        });

        return 1;
    }
}
