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

public class SPacketTransmitPacket {

    private boolean transmitting;

    public SPacketTransmitPacket(boolean transmitting) {
        this.transmitting = transmitting;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.transmitting);
    }

    public SPacketTransmitPacket(FriendlyByteBuf buffer) {
        this.transmitting = buffer.readBoolean();
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        boolean start = this.transmitting;

        context.enqueueWork(() -> {
            //we are on the server
            RadioItem radioItem = ModItems.RADIO.get();
            ServerPlayer player = context.getSender();
            ServerLevel level = player.serverLevel();
            ItemStack radio = ((player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == radioItem) ? player.getItemInHand(InteractionHand.MAIN_HAND) : player.getItemInHand(InteractionHand.OFF_HAND));

            if (radio.getItem() != radioItem)
                return;

            int frequency = radio.getOrCreateTag().getInt("frequency");
            if (start && !player.getCooldowns().isOnCooldown(radioItem)) {
                player.getCapability(PlayerRadioStorageProvider.PLAYER_RADIO_STORAGE_CAPABILITY).ifPresent(freq -> freq.setFrequency(frequency));
                level.playSound(
                        null, player.blockPosition(),
                        SoundRegistry.RADIO_START.get(),
                        SoundSource.PLAYERS,
                        1f,1f
                );
            } else {
                player.getCapability(PlayerRadioStorageProvider.PLAYER_RADIO_STORAGE_CAPABILITY).ifPresent(freq -> freq.setFrequency(-1));
                level.playSound(
                        null, player.blockPosition(),
                        SoundRegistry.RADIO_STOP.get(),
                        SoundSource.PLAYERS,
                        1f,1f
                );
            }
        });

        return true;
    }
}
