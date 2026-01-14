package fr.asdepack.client;

import fr.asdepack.Asdepack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Asdepack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {
    @SubscribeEvent
    public static void onOverlayRender(RenderGuiOverlayEvent.Post event) {
        if(ClientModHandler.CURRENT_REGIONS == null || ClientModHandler.CURRENT_REGIONS.isEmpty()) {
            return;
        }
        event.getGuiGraphics().drawString(
                Minecraft.getInstance().font,
                Component.literal(ClientModHandler.CURRENT_REGIONS).withStyle(ChatFormatting.GRAY),
                10,
                10,
                0xFFFFFF,
                true
        );
    }
}
