package fr.asdepack.common.network.packets;

import fr.asdepack.common.capabilities.playerkit.PlayerKitStorage;
import fr.asdepack.client.ClientModHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CSyncKitStorage {
    PlayerKitStorage cap;
    public CSyncKitStorage(PlayerKitStorage cap) {
        this.cap = cap;
    }
    public CSyncKitStorage(FriendlyByteBuf buffer) {
        CompoundTag nbt = buffer.readNbt();
        this.cap = new PlayerKitStorage();
        assert nbt != null;
        this.cap.deserializeNBT(nbt);
    }
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt((CompoundTag) this.cap.serializeNBT());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            if(contextSupplier.get().getDirection().getReceptionSide().isClient()) {
                this.syncCap();
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void syncCap() {
        ClientModHandler.PLAYER_KIT_STORAGE = this.cap;
    }
}
