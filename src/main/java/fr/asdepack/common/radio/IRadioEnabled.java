package fr.asdepack.common.radio;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IRadioEnabled {
    Random RANDOM = new Random();

    default void setFrequency(ItemStack stack, int frequencyName) {
        CompoundTag tag = stack.getOrCreateTag();

        tag.putInt("frequency", frequencyName);

    }

    default CompoundTag getFrequency(ItemStack stack, int frequencyName) {
        CompoundTag tag = stack.getOrCreateTag();

        tag.putInt("frequency", frequencyName);

        return tag;
    }

    default void listen(int frequencyName, UUID owner) {
        Frequency frequency = Frequency.getOrCreateFrequency(frequencyName);
        frequency.tryAddListener(owner);
    }

    default void stopListening(int frequencyName, UUID owner) {
        Frequency frequency = Frequency.getOrCreateFrequency(frequencyName);
        frequency.removeListener(owner);
    }

    default void tick(ItemStack stack, Level level, Entity entity) {
        if (level.isClientSide) return;
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("frequency") || tag.getInt("frequency") == 0)
            setFrequency(stack,
                    100 + RANDOM.nextInt(900)
            );
    }

    default void appendTooltip(ItemStack stack, List<Component> components) {
        CompoundTag tag = stack.getOrCreateTag();

        components.add(Component.literal(
                tag.getString("frequency") + " kHz"
        ).withStyle(ChatFormatting.DARK_GRAY));
    }
}
