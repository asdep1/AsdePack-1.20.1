package fr.asdepack.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fr.asdepack.Asdepack;
import fr.asdepack.RTPConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        int timer = RTPConfig.spawnTimer;
        if (player.isCreative()) {
            timer = 0;
        } else {
            if (Asdepack.WG_ADAPTER.isPlayerInRegion(player, "spawn")){
                player.sendSystemMessage(Component.literal("Vous vous trouvez déjà au spawn"));
                return 0;
            }
            ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
            CompoundTag fireworksTag = new CompoundTag();
            ListTag explosionsList = new ListTag();
            CompoundTag explosionTag = new CompoundTag();

            explosionTag.putByte("Type", (byte) 1);
            explosionTag.putByte("Trail", (byte) 1);
            explosionTag.putByte("Flicker", (byte) 1);
            explosionTag.putIntArray("Colors", new int[]{11743532, 14602026, 15435844});
            explosionTag.putIntArray("FadeColors", new int[]{4312372, 14602026, 6719955, 15435844, 15790320});

            explosionsList.add(explosionTag);
            fireworksTag.put("Explosions", explosionsList);
            fireworksTag.putByte("Flight", (byte) 3);

            fireworkStack.getOrCreateTag().put("Fireworks", fireworksTag);

            Random random = new Random();
            int fireworkCount = 5 + random.nextInt(6); // Between 5 and 10 fireworks

            for (int i = 0; i < fireworkCount; i++) {
                final int delay = i;
                Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                    double offsetX = (random.nextDouble() - 0.5) * 6; // Random offset within 3 blocks
                    double offsetY = random.nextInt(10);
                    double offsetZ = (random.nextDouble() - 0.5) * 6;
                    FireworkRocketEntity delayedFirework = new FireworkRocketEntity(
                            player.level(),
                            player.getX() + offsetX,
                            player.getY() + offsetY,
                            player.getZ() + offsetZ,
                            fireworkStack
                    );
                    player.level().addFreshEntity(delayedFirework);
                }, (delay + 1) * 250L, TimeUnit.MILLISECONDS);
            }
        }
        WaitingManager.start(player, timer, p -> {
            p.sendSystemMessage(Component.literal("Retour au spawn"));
            p.teleportTo(teleportPos.x(), teleportPos.y(), teleportPos.z());
        });

        return 1;
    }
}
