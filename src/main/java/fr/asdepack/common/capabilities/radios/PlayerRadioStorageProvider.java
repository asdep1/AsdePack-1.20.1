package fr.asdepack.common.capabilities.radios;

import fr.asdepack.common.capabilities.playerkit.IPlayerKitStorage;
import fr.asdepack.common.capabilities.playerkit.PlayerKitStorage;
import fr.asdepack.common.radio.Frequency;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerRadioStorageProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<IPlayerRadioStorage> PLAYER_RADIO_STORAGE_CAPABILITY = CapabilityManager.get(
            new CapabilityToken<>() {}
    );

    private PlayerRadioStorage storage;
    private final LazyOptional<IPlayerRadioStorage> optional = LazyOptional.of(this::createPlayerRadioStorage);

    private IPlayerRadioStorage createPlayerRadioStorage() {
        if (this.storage == null) {
            this.storage = new PlayerRadioStorage();
        }
        return this.storage;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == PLAYER_RADIO_STORAGE_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return (CompoundTag) this.storage.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(this.storage == null) {
            this.storage = new PlayerRadioStorage();
        }
        this.storage.deserializeNBT(nbt);
    }
}
