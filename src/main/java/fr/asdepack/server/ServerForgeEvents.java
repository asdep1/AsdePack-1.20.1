package fr.asdepack.server;

import fr.asdepack.Asdepack;
import fr.asdepack.common.capabilities.playerkit.PlayerKitStorage;
import fr.asdepack.common.capabilities.playerkit.PlayerKitStorageProvider;
import fr.asdepack.common.network.PacketHelper;
import fr.asdepack.common.network.packets.CSyncKitStorage;
import fr.asdepack.common.network.packets.CSyncRegion;
import fr.asdepack.server.bridges.ProtectedRegion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Asdepack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerForgeEvents {
    private static final ResourceLocation KIT_STORAGE_CAP_LOC = ResourceLocation.fromNamespaceAndPath(Asdepack.MODID, "player_kit_storage");

    @SubscribeEvent
    public static void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player && !event.getObject().getCapability(PlayerKitStorageProvider.PLAYER_KIT_STORAGE_CAPABILITY).isPresent()) {
            event.addCapability(KIT_STORAGE_CAP_LOC, new PlayerKitStorageProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerKitStorageProvider.PLAYER_KIT_STORAGE_CAPABILITY).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerKitStorageProvider.PLAYER_KIT_STORAGE_CAPABILITY).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if(!event.getLevel().isClientSide()) {
            if(event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(PlayerKitStorageProvider.PLAYER_KIT_STORAGE_CAPABILITY).ifPresent(kitStorage -> {
                    if(kitStorage instanceof PlayerKitStorage) {
                        PacketHelper.sendToPlayer(new CSyncKitStorage((PlayerKitStorage) kitStorage), player);
                    }
                });
            }
        }
    }

    private static final Map<UUID, String> lastRegions = new HashMap<>(); // cache to avoid spamming identical messages
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.ServerTickEvent event) {
        for(ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
                String worldName = player.level().dimension().location().getPath();
                if (worldName.equals("overworld")) worldName = "world"; // Ajustement fréquent sur Mohist/Bukkit
                if(player.tickCount % 20 != 0) continue;

                List<ProtectedRegion> regions = Asdepack.WG_ADAPTER.getApplicableRegions(worldName, player.position());

                if (!regions.isEmpty()) {
                    String regionNames = regions.stream()
                            .map(ProtectedRegion::getId)
                            .collect(Collectors.joining(", "));
                    if(lastRegions.containsKey(player.getUUID()) && lastRegions.get(player.getUUID()).equals(regionNames)) {
                        continue;
                    }

                    lastRegions.put(player.getUUID(), regionNames);
                    PacketHelper.sendToPlayer(new CSyncRegion(regionNames), player);
                } else {
                    if(lastRegions.containsKey(player.getUUID()) && lastRegions.get(player.getUUID()).equals("wild")) {
                        continue;
                    }
                    lastRegions.put(player.getUUID(), "wild");
                    PacketHelper.sendToPlayer(new CSyncRegion("Zone Sauvage"), player);
                }
            }
        }
    }
}