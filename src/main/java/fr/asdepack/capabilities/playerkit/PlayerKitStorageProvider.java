package fr.asdepack.capabilities.playerkit;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerKitStorageProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<IPlayerKitStorage> PLAYER_KIT_STORAGE_CAPABILITY = CapabilityManager.get(
            new CapabilityToken<>() {}
    );

    private PlayerKitStorage storage;
    private final LazyOptional<IPlayerKitStorage> optional = LazyOptional.of(this::createPlayerKitStorage);

    private IPlayerKitStorage createPlayerKitStorage() {
        if (this.storage == null) {
            this.storage = new PlayerKitStorage();
        }
        return this.storage;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_KIT_STORAGE_CAPABILITY) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return (CompoundTag) this.storage.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(this.storage == null) {
            this.storage = new PlayerKitStorage();
        }
        this.storage.deserializeNBT(nbt);
    }
}
