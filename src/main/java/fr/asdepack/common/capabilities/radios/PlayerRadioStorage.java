package fr.asdepack.common.capabilities.radios;

import fr.asdepack.common.radio.Frequency;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerRadioStorage implements IPlayerRadioStorage, INBTSerializable<CompoundTag> {
    private int frequency;

    @Override
    public int getFrequency() {
        return this.frequency;
    }

    @Override
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public void copyFrom(IPlayerRadioStorage source) {
        this.frequency = source.getFrequency();
    }

    public Frequency getFrequencyObj() {
        return Frequency.getOrCreateFrequency(frequency);
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("frequency", this.frequency);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.frequency = nbt.getInt("frequency");
    }
}
