package fr.asdepack.common.network.packets;

import fr.asdepack.common.capabilities.radios.PlayerRadioStorageProvider;
import fr.asdepack.common.registries.ModItems;
import fr.asdepack.common.registries.SoundRegistry;
import fr.asdepack.common.registries.items.RadioItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SPacketChangeRadioFreq {

    int newFreq;

    public SPacketChangeRadioFreq(int newFreq) {
        this.newFreq = newFreq;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.newFreq);
    }

    public SPacketChangeRadioFreq(FriendlyByteBuf buffer) {
        this.newFreq = buffer.readInt();
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            RadioItem radioItem = ModItems.RADIO.get();
            ServerPlayer player = context.getSender();
            assert player != null;
            ServerLevel level = player.serverLevel();
            ItemStack radio = ((player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == radioItem) ? player.getItemInHand(InteractionHand.MAIN_HAND) : player.getItemInHand(InteractionHand.OFF_HAND));

            if (radio.getItem() != radioItem)
                return;

            radio.getOrCreateTag().putInt("changeFrequency", Math.max(0, Math.min(999, this.newFreq)));

        });

        return true;
    }
}
