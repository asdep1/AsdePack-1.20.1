package fr.asdepack.common.network.packets;

import fr.asdepack.client.ClientModHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CSyncRegion {
    private String newRegion;
    public CSyncRegion(String newRegion) {
        this.newRegion = newRegion;
    }
    public CSyncRegion(FriendlyByteBuf buffer) {
        this.newRegion = buffer.readUtf();
    }
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.newRegion);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            if(contextSupplier.get().getDirection().getReceptionSide().isClient()) {
                this.syncNewRegion();
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void syncNewRegion() {
        ClientModHandler.CURRENT_REGIONS = this.newRegion;
    }
}
