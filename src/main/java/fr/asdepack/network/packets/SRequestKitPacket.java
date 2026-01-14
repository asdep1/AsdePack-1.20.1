package fr.asdepack.network.packets;

import fr.asdepack.Asdepack;
import fr.asdepack.capabilities.playerkit.IPlayerKitStorage;
import fr.asdepack.capabilities.playerkit.PlayerKitStorageProvider;
import fr.asdepack.server.KitCooldownManager;
import fr.asdepack.types.Kit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class SRequestKitPacket {
    private final Kit requestedKit;
    public SRequestKitPacket(Kit requestedKit) {
        this.requestedKit = requestedKit;
    }
    public SRequestKitPacket(FriendlyByteBuf buffer) {
        this.requestedKit = Kit.Serializer.fromJson(buffer.readUtf());
    }
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(Kit.Serializer.toJson(requestedKit));
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayer player = contextSupplier.get().getSender();
        if(player == null)
            return;

        switch (Kit.canGive(this.requestedKit, player)) {
            case SUCCESS:
                for (ItemStack item : this.requestedKit.getItems()) {
                    player.getInventory().placeItemBackInInventory(item.copy());
                }
                Asdepack.VAULT_ADAPTER.withdraw(player, this.requestedKit.getCost());
                KitCooldownManager.markUsed(player, this.requestedKit);
                player.sendSystemMessage(Component.literal("§aKit reçu."));
                player.closeContainer();
                break;
            case NO_PERMISSION:
                player.sendSystemMessage(Component.literal("§cVous n'avez pas la permission."));
                break;
            case ON_COOLDOWN:
                player.sendSystemMessage(KitCooldownManager.getCooldownMessage(player, this.requestedKit));
                break;
            case INSUFFICIENT_FUNDS:
                player.sendSystemMessage(Component.literal("§cVous n'avez pas assez d'argent (§e" + this.requestedKit.getCost() + "§c)."));
                break;
        }
    }
}
