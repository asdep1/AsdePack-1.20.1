package fr.asdepack.server.command;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Mod.EventBusSubscriber
public class WaitingManager {

    private static final Map<UUID, WaitingAction> WAITING = new ConcurrentHashMap<>();

    public static void start(ServerPlayer player, int seconds, Consumer<ServerPlayer> onFinish) {
        WAITING.put(player.getUUID(), new WaitingAction(player, seconds * 20, onFinish));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Iterator<WaitingAction> it = WAITING.values().iterator();
        while (it.hasNext()) {
            WaitingAction action = it.next();
            if (!action.tick()) {
                it.remove();
            }
        }
    }

    public static class WaitingAction {

        private final ServerPlayer player;
        private final Vec3 startPos;
        private final Consumer<ServerPlayer> onFinish;
        private int ticksRemaining;

        public WaitingAction(ServerPlayer player, int ticks, Consumer<ServerPlayer> onFinish) {
            this.player = player;
            this.startPos = player.position();
            this.ticksRemaining = ticks;
            this.onFinish = onFinish;
        }

        public boolean tick() {
            if (!player.isAlive()) return false;

            if (player.position().distanceTo(startPos) > 0.01) {
                player.sendSystemMessage(
                        net.minecraft.network.chat.Component.literal("Action annulée : vous avez bougé.")
                );
                return false;
            }

            player.sendSystemMessage(Component.literal("§eAttendez : §6" + ticksRemaining / 20 + "s"), true);

            ticksRemaining--;

            if (ticksRemaining <= 0) {
                onFinish.accept(player);
                return false;
            }

            return true;
        }
    }
}