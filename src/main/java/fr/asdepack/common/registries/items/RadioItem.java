package fr.asdepack.common.registries.items;

import fr.asdepack.common.menus.RadioConfigMenu;
import fr.asdepack.common.network.PacketHelper;
import fr.asdepack.common.network.packets.SPacketTransmitPacket;
import fr.asdepack.common.radio.IRadioEnabled;
import fr.asdepack.common.registries.SoundRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class RadioItem extends Item implements IRadioEnabled {
    public RadioItem(Properties pProperties) {
        super(pProperties);
    }

    private void transmit(boolean started) {
        PacketHelper.sendToServer(new SPacketTransmitPacket(started));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean b) {
        super.inventoryTick(stack, level, entity, slot, b);
        tick(stack, level, entity);
        if (!level.isClientSide) {
            if (entity instanceof Player player) {
                CompoundTag tag = stack.getOrCreateTag();
                int frequency = tag.getInt("frequency");
                UUID playerUUID = player.getUUID();

                //check if frequency has been changed
                if (tag.contains("changeFrequency")) {
                    int changeFreq = tag.getInt("changeFrequency");
                    stopListening(frequency, playerUUID);
                    tag.remove("changeFrequency");
                    tag.putInt("frequency", changeFreq);
                    listen(changeFreq, playerUUID);
                }

                if (tag.contains("user")) {
                    UUID currentUUID = tag.getUUID("user");
                    if (currentUUID.equals(playerUUID)) {
                        return;
                    }

                    stopListening(frequency, currentUUID);
                }

                System.out.println("RadioItem: Player " + player.getName().getString() + " is now listening to frequency " + frequency + " kHz");
                listen(frequency, playerUUID);
                tag.putUUID("user", playerUUID);
            } else {
                //no longer in a player's inventory, remove last held (current UUID) from listening
                CompoundTag tag = stack.getOrCreateTag();
                int frequency = tag.getInt("frequency");
                if (tag.contains("user")) {
                    UUID currentUUID = tag.getUUID("user");
                    stopListening(frequency, currentUUID);
                    tag.remove("user");
                }
            }
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player)
    {
        CompoundTag tag = item.getOrCreateTag();
        int frequency = tag.getInt("frequency");

        if (tag.contains("user")) {
            UUID currentUUID = tag.getUUID("user");
            stopListening(frequency, currentUUID);
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltip) {
        CompoundTag tag = stack.getOrCreateTag();

        components.add(Component.literal(
                tag.getInt("frequency") + " kHz"
        ).withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, level, components, tooltip);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(!level.isClientSide()) {
            if(player.isCrouching()) {
                System.out.println("Opening radio config menu for player " + player.getName().getString());
                NetworkHooks.openScreen(
                        (ServerPlayer) player,
                        new SimpleMenuProvider(
                                (id, inv, p) -> new RadioConfigMenu(id, inv, player.getInventory().selected),
                                Component.literal("Radio Frequency")
                        ),
                        (buffer) -> {
                            buffer.writeInt(player.getInventory().selected);
                        }
                );
            }
        }

        if(player.isCrouching()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(
                null, player.blockPosition(),
                SoundRegistry.RADIO_START.get(),
                SoundSource.PLAYERS,
                1f,1f
        );
        player.startUsingItem(hand);

        //send started using packet to server
        if (level.isClientSide) {
            transmit(true);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseTicks) {
        if (user instanceof Player player) {
            level.playSound(
                    null, user.blockPosition(),
                    SoundRegistry.RADIO_STOP.get(),
                    SoundSource.PLAYERS,
                    1f,1f
            );

            //send stopped using packet to server
            if (level.isClientSide) {
                transmit(false);
            }

            player.getCooldowns().addCooldown(this, 10);
        }

        super.releaseUsing(stack, level, user, remainingUseTicks);
    }
}
