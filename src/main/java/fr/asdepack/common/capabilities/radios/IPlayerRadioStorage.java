package fr.asdepack.common.capabilities.radios;

public interface IPlayerRadioStorage {
    int getFrequency();
    void setFrequency(int frequency);
    void copyFrom(IPlayerRadioStorage source);
}
