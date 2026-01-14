package fr.asdepack.client;

import com.mojang.blaze3d.platform.InputConstants;
import fr.asdepack.Asdepack;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public class Keybindings {
    public static final Keybindings INSTANCE = new Keybindings();

    private Keybindings() {}

    private static final String CATEGORY = "key.categories." + Asdepack.MODID;

    public final KeyMapping demoKeybind = new KeyMapping(
            "key." + Asdepack.MODID + ".example_packet_key",
            KeyConflictContext.IN_GAME,
            InputConstants.getKey(InputConstants.KEY_V, -1),
            CATEGORY
    );
}
