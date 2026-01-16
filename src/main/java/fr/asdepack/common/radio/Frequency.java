package fr.asdepack.common.radio;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Frequency {
    public static final List<Frequency> frequencies = new ArrayList<>();

    public final int frequency;
    public final List<RadioChannel> listeners;

    public Frequency(int frequency) {
        if (frequency < 0 || frequency > 999)
            frequency = 0;

        this.frequency = frequency;
        this.listeners = new ArrayList<>();

        frequencies.add(this);
    }

    public static int getFrequency(int integer) {
        for (int i = 0; i < frequencies.size(); i++) {
            Frequency frequency = frequencies.get(i);
            if (frequency.frequency == integer)
                return i;
        }

        return -1;
    }

    public RadioChannel getChannel(UUID player) {
        for (RadioChannel listener : listeners)
            if (listener.owner.equals(player)) return listener;

        return null;
    }

    public RadioChannel getChannel(Player player) {
        return getChannel(player.getUUID());
    }

    @Nullable
    public RadioChannel tryAddListener(UUID owner) {
        if (getChannel(owner) == null)
            return addListener(owner);

        return null;
    }

    public RadioChannel addListener(UUID owner) {
        RadioChannel channel = new RadioChannel(owner);
        listeners.add(channel);
        return channel;
    }

    public void removeListener(Player player) {
        removeListener(player.getUUID());
    }
    public void removeListener(UUID player) {
        listeners.removeIf(channel -> channel.owner.equals(player));

        if (listeners.isEmpty())
            frequencies.remove(this);
    }

    public static Frequency getOrCreateFrequency(int frequency) {
        int index = getFrequency(frequency);
        if (index != -1) return frequencies.get(index);
        return new Frequency(frequency);
    }

    public static String incrementFrequency(String frequency, int amt) {
        int freqNum = Integer.parseInt(frequency);
        freqNum += amt;
        if (freqNum > 999) freqNum = 0;
        if (freqNum < 0) freqNum = 999;
        return String.format("%03d", freqNum);
    }
}
